package app.banking.models;

import lombok.Data;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;

@Data
@Table
public class Transaction {
  @Column(identity=true, generative=GenerativeValue.SEQUENCE)
  private Long id;

  @Column(required=true)
  private double amount;

  @Column(required=true)
  private String label;

  @Column(required=true)
  private Instant date;

  @Column(required=true)
  private TransactionType type;

  @Column(name="id_account", references=true, required=true)
  private Account idAccount;

  @Column(name="id_transaction_group", references=true)
  private TransactionGroup idTransactionGroup;
}
