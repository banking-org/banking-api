package app.banking.models;

import lombok.Data;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;

@Data
@Table(name="transaction_group")
public class TransactionGroup {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column
  private String label;

  @Column
  private Instant date;

  @Column(name="id_account", references=true)
  private Account idAccount;

  @Column
  private double amount;
}
