package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode
public class TransferPayload {
  private String label;
  private Double amount;
  private Instant effectDate;
  private Long accountFrom;
  private Long accountTarget;
}
