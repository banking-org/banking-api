package app.banking.services;

import app.banking.DTO.*;
import app.banking.models.*;
import app.banking.repository.AccountRepository;
import app.banking.repository.BalanceRepository;
import app.banking.repository.CategoryRepository;
import app.banking.repository.TransactionRepository;
import app.banking.utils.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final AccountRepository accountRepo;
  private final BalanceRepository balanceRepo;
  private final TransactionRepository transactionRepo;
  private final CategoryRepository categoryRepo;

  private TransactionData toData(AccountBalance value, Account account){
    TransactionData data = new TransactionData();
    data.setAccount(AccountService.accountToDto(account));
    data.setCurrentAmount(value.getCurrentBalance());
    return data;
  }

  public Optional<Transaction> setCategoryById(Long id, Long categoryId){
    return categoryRepo
      .findById(categoryId)
      .map(category ->
        transactionRepo
          .findById(id)
          .map(value -> {
            if (
              (
                value.getType() == TransactionType.CREDIT &&
                category.getOnlyOn() == TransactionType.CREDIT
              ) || (
                value.getType() == TransactionType.DEBIT &&
                category.getOnlyOn() == TransactionType.DEBIT
              )
            ) {
              value.setCategory(category);
            }
            return transactionRepo
              .updateById(id, value)
              .orElseThrow();
          })
          .orElseThrow()
      )
    ;
  }

  public List<Transaction> getAllTransaction(Long accountId){
    return transactionRepo.findAllById(accountId);
  }

  public Object doWithdraw(WithdrawPayload payload) {
    try {
      Account account = accountRepo
        .findById(payload.getAccountId())
        .orElseThrow();

      AccountBalance defaultBalance = new AccountBalance();
      defaultBalance.setCurrentBalance(0D);
      AccountBalance accountBalance = balanceRepo
        .findCurrentByAccountId(account.getId())
        .orElse(defaultBalance);

      Double amount = payload.getAmount();
      Double salary = account.getNetMonthSalary();
      Double currentBalance = accountBalance.getCurrentBalance();
      double currentDebit = currentBalance - amount;

      AccountBalance debits = new AccountBalance();
      debits.setIdAccount(account);
      debits.setCurrentBalance(currentDebit);

      Transaction transaction = new Transaction();
      transaction.setIdAccount(account);
      transaction.setAmount(Math.abs(amount));
      transaction.setType(TransactionType.DEBIT);

      if(
        ((salary / 3) >= amount && currentBalance >= amount && currentDebit >= 0)
      ){
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepo.save(transaction).orElseThrow();
        return balanceRepo
          .save(debits)
          .map(v -> this.toData(v, account));
      }else if(
        (account.getAccountType() == AccountType.DEBIT) &&
        currentDebit < 0
      ){
        if (
          transactionRepo
          .findLastNotPaidByAccountId(account.getId())
          .isEmpty()
        ) {
          transaction.setStatus(TransactionStatus.NO_PAYED);
          transactionRepo.save(transaction);
          return balanceRepo
            .save(debits)
            .map(v -> this.toData(v, account));
        }else {
          return ResponseEntity
            .badRequest()
            .body(new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "you should pay your last negative debit"
              )
            );
        }
      }
    }catch (Exception ignored) {}
    return ResponseEntity
      .status(HttpStatus.NOT_ACCEPTABLE)
      .body(new ResponseError(
          HttpStatus.BAD_REQUEST.value(),
          "Cannot perform transactions"
        )
      );
  }

  public Optional<TransactionData> deposit(DepositPayload payload){
    try {
      Account account = accountRepo
        .findById(payload.getAccountId())
        .orElseThrow();
      Transaction transaction = new Transaction();
      transaction.setLabel(payload.getLabel());
      transaction.setIdAccount(
        Account.onlyId(
          payload.getAccountId()
        )
      );
      transaction.setAmount(payload.getAmount());
      LocalDate effect = payload.getEffectDate();
      transaction.setEffectDate(effect);
      if(effect != null && !LocalDate.now().equals(effect)){
        transaction.setStatus(TransactionStatus.PENDING);
      }else {
        transaction.setStatus(TransactionStatus.SUCCESS);
      }
      transaction.setType(TransactionType.CREDIT);
      transactionRepo.save(transaction);

      Double currentBalance = balanceRepo
        .findCurrentByAccountId(account.getId())
        .map(AccountBalance::getCurrentBalance)
        .orElse(0D);
      Double amount = payload.getAmount();
      double currentCredit = currentBalance + amount;

      AccountBalance credits = new AccountBalance();
      credits.setIdAccount(account);
      credits.setCurrentBalance(currentCredit);
      return balanceRepo.save(credits).map(v -> this.toData(v, account));
    }catch (Exception ignored){
      return Optional.empty();
    }
  }
}
