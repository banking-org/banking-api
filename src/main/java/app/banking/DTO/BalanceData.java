package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class BalanceData {
  public AccountData account;
  public Double currentBalance;
  public Double withdraws;
  private Double interests;
}
