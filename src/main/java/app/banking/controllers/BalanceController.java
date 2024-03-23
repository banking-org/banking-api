package app.banking.controllers;

import app.banking.DTO.BalanceData;
import app.banking.services.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalanceController {
  private final BalanceService service;

  @GetMapping("/{id}")
  public BalanceData getBalanceById(
    @PathVariable Long id,
    @RequestParam(required=false) LocalDate date
  ){
    long accountId = Math.abs(id);
    if(date != null){
      return service.getByDate(accountId, date);
    }
    return service.getCurrentBalance(accountId);
  }
}
