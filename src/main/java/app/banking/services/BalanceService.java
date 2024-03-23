package app.banking.services;

import app.banking.DTO.BalanceData;
import app.banking.models.AccountBalance;
import app.banking.models.Interest;
import app.banking.models.Transaction;
import app.banking.repository.BalanceRepository;
import app.banking.repository.InterestRepository;
import app.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceService {
  private final AccountService accountService;
  private final BalanceRepository balanceRepo;
  private final InterestRepository interestRepo;
  private final TransactionRepository transactionRepo;

  private Integer checkInterest(Interest interest, boolean late){
    if(late) return interest.getEffectiveInterest();
    return interest.getFirstInterest();
  }

  private boolean checkIfPaymentLate(Long accountId){
    return transactionRepo
      .findLastNotPaidByAccountId(accountId)
      .map(Transaction::getCreatedAt)
      .map(Timestamp::from)
      .map(timestamp -> timestamp.toLocalDateTime().toLocalDate())
      .map(localDate -> {
        LocalDate offset7Days = localDate.plusDays(7);
        return localDate.isBefore(offset7Days);
      })
      .orElse(false);
  }

  private BalanceData commonBalancer(
    Long accountId,
    Optional<AccountBalance> balance,
    Double sumOfNotPayed
  ){
    BalanceData balanceData = new BalanceData();
    accountService
      .getOne(accountId)
      .ifPresent(balanceData::setAccount);

    balance.
      ifPresentOrElse(
        accountBalance -> balanceData.setCurrentBalance(accountBalance.getCurrentBalance()),
        () -> balanceData.setCurrentBalance(0D)
      );

    double debit = Math.abs(sumOfNotPayed);
    balanceData.setWithdraws(debit);
    boolean isLatePaid = checkIfPaymentLate(accountId);
    interestRepo
      .findByAccountId(accountId)
      .map(interest -> {
        double taxes = checkInterest(interest, isLatePaid);
        return debit * taxes / 100;
      })
      .ifPresentOrElse(
        balanceData::setInterests,
        () -> balanceData.setInterests(0D)
      );
    return balanceData;
  }

  public BalanceData getCurrentBalance(Long accountId){
    return commonBalancer(
      accountId,
      balanceRepo.findCurrentByAccountId(accountId),
      transactionRepo.findSumOfNoPayedByAccountId(accountId)
    );
  }

  public BalanceData getByDate(Long accountId, LocalDate date){
    return commonBalancer(
      accountId,
      balanceRepo.findAtDateByAccountId(accountId, date),
      transactionRepo.findSumOfNoPayedByAccountIdAtDate(accountId, date)
    );
  }
}
