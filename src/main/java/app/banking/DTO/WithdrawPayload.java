package app.banking.DTO;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WithdrawPayload {
  private Long accountId;
  private Double amount;
}
