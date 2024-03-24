package app.banking.services;

import app.banking.DTO.DepositPayload;
import app.banking.DTO.GroupTransferPayload;
import app.banking.DTO.TransferPayload;
import app.banking.DTO.WithdrawPayload;
import app.banking.models.Account;
import app.banking.models.AccountBalance;
import app.banking.models.Transaction;
import app.banking.models.TransactionGroup;
import app.banking.repository.AccountRepository;
import app.banking.repository.BalanceRepository;
import app.banking.repository.TransactionGroupRepo;
import app.banking.repository.TransactionRepository;
import app.banking.utils.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferService {
  private final TransactionService transactionService;
  private final AccountRepository accountRepo;
  private final TransactionGroupRepo transactionGroupRepo;
  private final TransactionRepository transactionRepo;
  private final BalanceRepository balanceRepo;

  public Object simpleTransfer(TransferPayload payload){
    double amount = payload.getAmount();
    if(amount < 0) {
      return ResponseEntity
        .badRequest()
        .body(new ResponseError(
          HttpStatus.BAD_REQUEST.value(),
          "cannot transfer this amount"
        ));
    }

    Account accountFrom;
    Account accountTarget;
    try {
      accountFrom = accountRepo.
        findById(payload.getAccountFrom())
        .orElseThrow();
      accountTarget = accountRepo
        .findById(payload.getAccountTarget())
        .orElseThrow();
    }catch (Exception ignored){
      return ResponseEntity
        .badRequest()
        .body(new ResponseError(
          HttpStatus.BAD_REQUEST.value(),
          "invalid account"
        ));
    }
    transactionService.doWithdraw(
      WithdrawPayload
        .builder()
        .accountId(accountFrom.getId())
        .amount(amount)
        .build()
    );
    LocalDate effect = payload.getEffectDate();
    if(accountFrom.getBank().equals(accountTarget.getBank())){
      effect = effect.plusDays(2);
    }
    return transactionService.deposit(
      DepositPayload
        .builder()
        .accountId(accountTarget.getId())
        .amount(amount)
        .label(
          "money transfer transfer from " +
            accountFrom.getLastname() + " " +
            accountFrom.getFirstname()
        )
        .effectDate(effect)
        .build()
    );
  }

  public Object groupedTransfer(GroupTransferPayload payload){
    TransactionGroup transactionGroup = new TransactionGroup();
    transactionGroup.setLabel(payload.getLabel());
    transactionGroup.setIdAccount(Account.onlyId(payload.getAccountId()));
    Optional<TransactionGroup> savedGroup = transactionGroupRepo.save(transactionGroup);
    if (savedGroup.isPresent()) {
      TransactionGroup group = savedGroup.get();
      LocalDate effectDate = payload.getEffectDate();
      String label = payload.getLabel();
      List<Transaction> transactionQueue = new ArrayList<>();
      for (Long idAccount : payload.getTargetsId()) {
        Account accountTarget = accountRepo
          .findById(idAccount)
          .orElseThrow();
        transactionQueue.add(
          Transaction
            .builder()
            .idAccount(transactionGroup.getIdAccount())
            .idGrouping(group)
            .label(label)
            .planedAt(Timestamp.valueOf(effectDate.atStartOfDay()).toInstant())
            .amount(Math.abs(payload.getAmount()))
            .receiver(accountTarget)
            .build()
        );
      }
      AccountBalance currentBalance = balanceRepo.findCurrentByAccountId(payload.getAccountId()).orElse(null);
      if(currentBalance != null){
        for (Transaction transaction : transactionQueue) {
          transactionRepo.save(transaction);
          AccountBalance targetBalance = balanceRepo
            .findCurrentByAccountId(transaction.getReceiver().getId())
            .orElse(null);
          if(targetBalance != null){
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setIdAccount(targetBalance.getIdAccount());
            accountBalance.setCurrentBalance(targetBalance.getCurrentBalance() + payload.getAmount());
            balanceRepo.save(accountBalance);
          }
        }
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setIdAccount(transactionGroup.getIdAccount());
        accountBalance.setCurrentBalance(currentBalance.getCurrentBalance() - (payload.getAmount() * transactionQueue.size()));
        balanceRepo.save(accountBalance);
      }
      return ResponseEntity.badRequest().body(
        new ResponseError(
          HttpStatus.BAD_REQUEST.value(),
          "Cannot group transfer this operation"
        )
      );
    }
    return Optional.empty();
  }
}
