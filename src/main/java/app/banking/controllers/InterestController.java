package app.banking.controllers;

import app.banking.DTO.UpSetInterestPayload;
import app.banking.models.Interest;
import app.banking.services.InterestService;
import app.banking.services.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interest")
public class InterestController {
  private final InterestService interestService;
  private final TransferService transferService;

  @GetMapping("/{id}")
  public Optional<Interest> getAccountInterest(
    @PathVariable Long id
  ){
    transferService.checkTransfersByAccountId(id);
    return interestService.getByAccountId(id);
  }

  @PutMapping
  public Optional<Interest> upSetInterest(
    @RequestBody UpSetInterestPayload payload
  ){
    return interestService.createOrUpdate(payload);
  }
}
