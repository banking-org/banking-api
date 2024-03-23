package app.banking.controllers;

import app.banking.DTO.DepositPayload;
import app.banking.DTO.TransactionData;
import app.banking.DTO.WithdrawPayload;
import app.banking.models.Transaction;
import app.banking.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
  private final TransactionService service;

  @GetMapping("/{id}")
  public List<Transaction> getAllAccountTransaction(
    @PathVariable Long id
  ){
    return service.getAllTransaction(id);
  }

  @PatchMapping("/{id}")
  public Optional<Transaction> updateCategory(
    @PathVariable Long id,
    @RequestParam Long categoryId
  ){
    return service.setCategoryById(id, categoryId);
  }

  @PutMapping("/withdraw")
  public Object doWithdraw(
    @RequestBody WithdrawPayload payload
  ){
    return service.doWithdraw(payload);
  }

  @PutMapping("/deposit")
  public Optional<TransactionData> deposit(
    @RequestBody DepositPayload payload
  ){
    return service.deposit(payload);
  }
}
