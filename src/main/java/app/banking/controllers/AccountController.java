package app.banking.controllers;

import app.banking.DTO.AccountData;
import app.banking.DTO.UpSetAccountPayload;
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
  public List<AccountData> getAll(){
    return service.getList();
  }

  @GetMapping("/{id}")
  public Optional<AccountData> getOne(
    @PathVariable Long id
  ){
    return service.getOne(id);
  }

  @PostMapping
  public Object createOne(
    @RequestBody UpSetAccountPayload payload
  ){
    int birthYear = payload.getBirthdate().getYear();
    int nowYear = LocalDate.now().getYear();
    int age = nowYear - birthYear;
    if(age > 21){
      return service.saveOne(payload);
    }
    return ResponseEntity
      .badRequest()
      .body(new ResponseError(
        HttpStatus.BAD_REQUEST.value(),
        "age should be greater than 21"
      )
    );
  }

  @PutMapping("/{id}")
  public Optional<AccountData> updateOne(
    @PathVariable Long id,
    @RequestBody UpSetAccountPayload payload
  ){
    return service.edit(id, payload);
  }

  @DeleteMapping("/{id}")
  public Optional<AccountData> deleteOn(
    @PathVariable Long id
  ){
    return service.deleteOne(id);
  }
}
