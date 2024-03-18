package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;

@Data
@Table
@EqualsAndHashCode
public class Balance {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column(required=true)
  private double amount;

  @Column(required=true)
  private Instant date;

  @Column(name="id_account", references=true, required=true)
  private Account idAccount;
}
