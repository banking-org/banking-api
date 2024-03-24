package app.banking.services;

import app.banking.DTO.AccountData;
import app.banking.DTO.UpSetAccountPayload;
import app.banking.models.Account;
import app.banking.models.AccountType;
import app.banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final AccountRepository repository;

  private Account payloadToAccount(UpSetAccountPayload account){
    Account value = new Account();
    value.setFirstname(account.getFirstname());
    value.setLastname(account.getLastname());
    value.setNetMonthSalary(account.getSalary());
    value.setBirthdate(account.getBirthdate());
    value.setBank(account.getBank());
    value.setAccountNumber(account.getAccountNumber());
    if(value.getAccountNumber() == null){
      value.setAccountNumber(createAccountNumber());
    }
    try {
      value.setAccountType(AccountType.valueOf(account.getType()));
    }catch (Exception ignored){
      value.setAccountType(AccountType.NORMAL);
    }
    return value;
  }

  private static String createAccountNumber(){
    Random rand = new Random();
    return String.format((Locale) null,
      "101%02d-%04d-%04d-%04d",
      rand.nextInt(10),
      rand.nextInt(10000),
      rand.nextInt(10000),
      rand.nextInt(10000)
    );
  }

  public static AccountData accountToDto(Account account){
    AccountData data = new AccountData();
    data.setId(account.getId());
    data.setLastname(account.getLastname());
    data.setFirstname(account.getFirstname());
    data.setBirthdate(account.getBirthdate());
    data.setSalary(account.getNetMonthSalary());
    data.setAccountNumber(account.getAccountNumber());
    data.setAccountType(account.getAccountType().name());
    data.setBank(account.getBank());
    data.setUpdatedAt(account.getUpdatedAt());
    return data;
  }

  public Optional<AccountData> saveOne(UpSetAccountPayload account){
    return repository
      .save(payloadToAccount(account))
      .map(AccountService::accountToDto);
  }

  public List<AccountData> getList(){
    return repository
      .findAll()
      .stream()
      .map(AccountService::accountToDto)
      .toList();
  }

  public Optional<AccountData> getOne(Long id){
    return repository
      .findById(id)
      .map(AccountService::accountToDto);
  }

  public Optional<AccountData> edit(Long id, UpSetAccountPayload updates){
    Account account = payloadToAccount(updates);
    account.setUpdatedAt(Instant.now());
    Account found = repository.findById(id).orElseThrow();
    if(found.getAccountNumber().isEmpty()){
      account.setAccountNumber(createAccountNumber());
    }
    return repository
      .updateById(id, account)
      .map(AccountService::accountToDto);
  }

  public Optional<AccountData> deleteOne(Long id){
    return repository
      .deleteById(id)
      .map(AccountService::accountToDto);
  }
}
