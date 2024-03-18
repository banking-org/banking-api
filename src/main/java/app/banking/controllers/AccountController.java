package app.banking.controllers;

import app.banking.DTO.GetAccountDto;
import app.banking.DTO.UpSetAccountDto;
import app.banking.services.AccountService;
import app.banking.utils.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
  private final AccountService service;

  @GetMapping
  public List<GetAccountDto> getAllAccounts(){
    return service.getAll();
  }

  @GetMapping("/{id}")
  public Optional<GetAccountDto> getAccountById(
      @PathVariable Long id
  ){
    return service.getById(id);
  }

  @PostMapping
  public Object createAccount(
      @RequestBody UpSetAccountDto data
  ) {
    int birthYear = data.getBirthdate().getYear();
    int nowYear = LocalDate.now().getYear();
    int age = nowYear - birthYear;
    if(age > 21){
      return service.createOne(data.toAccount());
    }
    return ResponseEntity.badRequest().body(
      new ResponseError(
          HttpStatus.BAD_REQUEST.value(),
          "age should be greater than 21"
      )
    );
  }

  @PutMapping("/{id}")
  public Optional<GetAccountDto> updateAccountById(
      @PathVariable Long id,
      @RequestBody UpSetAccountDto data
  ){
    return service.updateById(id, data.toAccount());
  }
}
