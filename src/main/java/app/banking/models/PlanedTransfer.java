package app.banking.models;

import lombok.Data;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;
import java.time.LocalDate;

@Table
@Data
public class PlanedTransfer {
  @Column(identity=true, generative=GenerativeValue.SEQUENCE)
  private Long id;

  @Column(required=true)
  private String label;

  @Column
  private double amount;

  @Column(defaultValue="current_timestamp", required=true)
  private Instant added_at;

  @Column(name="effect_date")
  private LocalDate effectDate;

  @Column(required=true)
  private boolean canceled;

  @Column(references=true, required=true)
  private Account idAccount;
}
