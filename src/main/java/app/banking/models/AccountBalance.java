package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;

@Data
@ToString
@EqualsAndHashCode
@Table(name="account_balance")
public class AccountBalance {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  public Long id;

  @Column(name="current_balance")
  private Double currentBalance;

  @Column(name="id_account", references=true, required=true)
  public Account idAccount;

  @Column(name="checked_at", defaultValue="now()")
  private Instant checkedAt;
}
