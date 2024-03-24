package app.banking.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DepositPayload {
  private Long accountId;
  private Double amount;
  private String label;
  private LocalDate effectDate;
}
