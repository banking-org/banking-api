package app.banking.controllers;

import app.banking.DTO.GroupTransferPayload;
import app.banking.DTO.TransferPayload;
import app.banking.services.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {
  private final TransferService service;

  @PutMapping("/simple")
  private Object doSimpleTransfer(
    @RequestBody TransferPayload payload
  ){
    return service.simpleTransfer(payload);
  }

  @PutMapping("/grouped")
  private Object doGroupedTransfer(
    @RequestBody GroupTransferPayload payload
  ){
    return service.groupedTransfer(payload);
  }
}
