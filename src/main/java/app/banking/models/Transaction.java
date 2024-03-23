package app.banking.models;

import lombok.*;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;
import java.time.LocalDate;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table
public class Transaction {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column(required=true)
  private Double amount;

  @Column
  private String label;

  @Column(name="id_account", references=true, required=true)
  private Account idAccount;

  @Column
  private TransactionType type;

  @Column(name="effect_date", defaultValue="now()")
  private LocalDate effectDate;

  @Column(references=true)
  private Category category;

  @Column
  private TransactionStatus status;

  @Column(name="id_group", references=true)
  private TransactionGroup idGrouping; // for grouped transfer only

  @Column(unique=true)
  private String reference; // for transfer only

  @Column(references=true)
  private Account receiver; // for transfer only

  @Column(name="planed_at")
  private Instant planedAt;

  @Column(name="created_at", defaultValue="now()")
  private Instant createdAt;
}
