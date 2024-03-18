package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;

@Table
@Data
@EqualsAndHashCode
public class Credits {
  @Column(identity=true, generative=GenerativeValue.SEQUENCE)
  private Long id;

  @Column
  private double amount;

  @Column(defaultValue="now()")
  private Instant date;

  @Column(name="id_account", references=true)
  private Account accountId;
}
