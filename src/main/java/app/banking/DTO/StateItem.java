package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode
public class StateItem {
  private LocalDate date;
  private String reference;
  private String label;
  private Double credit;
  private Double debit;
  private Double balance;
}
