package app.banking.services;

import app.banking.DTO.StateItem;
import app.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {
  private final TransactionRepository repository;

  public List<StateItem> getStatement(
    Long accountId,
    LocalDate start,
    LocalDate end
  ){
    return repository
      .findAllByIntervalAndAccountId(
        accountId,
        start,
        end
      );
  }
}
