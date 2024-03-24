package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class GroupTransferPayload {
  private String label;
  private Long accountId;
  private List<Long> targetsId;
  private double amount; // for each target
  private LocalDate effectDate;
}
