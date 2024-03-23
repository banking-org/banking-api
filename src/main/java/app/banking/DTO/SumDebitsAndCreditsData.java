package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class SumDebitsAndCreditsData {
  private CommonData incomes;
  private CommonData outcomes;
}
