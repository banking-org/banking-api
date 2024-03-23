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
@Table
public class TransactionGroup {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column(required=true)
  private String label;

  @Column(name="id_account", references=true, required=true)
  private Account idAccount;

  @Column(name="updated_at", defaultValue="now()")
  private Instant updatedAt;
}
