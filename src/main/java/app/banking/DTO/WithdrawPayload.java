package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class WithdrawPayload {
  private Long accountId;
  private Double amount;
}
