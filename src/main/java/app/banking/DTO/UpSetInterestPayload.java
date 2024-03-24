package app.banking.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@Builder
public class UpSetInterestPayload {
  private Long idAccount;
  private Integer firstInterest;
  private Integer effectiveInterest;
}
