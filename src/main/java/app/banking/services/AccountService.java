package app.banking.services;

import app.banking.DTO.GetAccountDto;
import app.banking.models.Account;
import app.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
  private final AccountRepository repository;

  public AccountService(AccountRepository repository){
    this.repository = repository;
  }

  public List<GetAccountDto> getAll(){
    return repository
        .findAll()
        .stream()
        .map(GetAccountDto::getFromAccount)
        .toList();
  }

  public Optional<GetAccountDto> getById(Long id){
    return repository
        .findById(id)
        .map(GetAccountDto::getFromAccount);
  }

  public Optional<GetAccountDto> createOne(Account account){
    Optional<Account> savedAccount = repository.save(account);
    return savedAccount
        .map(GetAccountDto::getFromAccount);
  }

  public Optional<GetAccountDto> updateById(Long id, Account newAccountData){
    return repository
        .updateById(id, newAccountData)
        .map(GetAccountDto::getFromAccount);
  }
}
