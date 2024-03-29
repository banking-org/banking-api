package app.banking.controllers;

import app.banking.DTO.StateItem;
import app.banking.services.StatementService;
import app.banking.services.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statement")
public class StatementController {
  private final StatementService service;
  private final TransferService transferService;

  @GetMapping("/{id}")
  public List<StateItem> getAccountStatement(
    @PathVariable Long id,
    @RequestParam LocalDate start,
    @RequestParam LocalDate end
  ){
    transferService.checkTransfersByAccountId(id);
    return service.getStatement(id, start, end);
  }
}
