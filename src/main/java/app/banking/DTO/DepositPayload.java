package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode
public class DepositPayload {
  private Long accountId;
  private Double amount;
  private String label;
  private LocalDate effectDate;
}
