package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

@Data
@EqualsAndHashCode
@Table
public class Interest {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column(name="first_interest", required=true)
  private int firstInterest;

  @Column(name="effective_interest", required=true)
  private int effectiveInterest;

  @Column(name="id_account", references=true, required=true)
  private Account IdAccount;
}
