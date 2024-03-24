package app.banking.controllers;

import app.banking.models.TransactionGroup;
import app.banking.repository.TransactionGroupRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grouping/transaction")
public class TransactionGroupController {
  private final TransactionGroupRepo repository;

  @GetMapping
  public List<TransactionGroup> getAll(){
    return repository.findAll();
  }

  @GetMapping("/{id}")
  public Optional<TransactionGroup> getById(
    @PathVariable Long id
  ){
    return repository.findById(id);
  }

  @PostMapping
  public Optional<TransactionGroup> create(
    @RequestBody TransactionGroup body
  ){
    return repository.save(body);
  }

  @PutMapping("/{id}")
  public Optional<TransactionGroup> updateById(
    @PathVariable Long id,
    @RequestBody TransactionGroup body
  ){
    return repository.updateById(id,  body);
  }

  @DeleteMapping("/{id}")
  public Optional<TransactionGroup> deleteById(
    @PathVariable Long id
  ){
    return repository.deleteById(id);
  }
}
