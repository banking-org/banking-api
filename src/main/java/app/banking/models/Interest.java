package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

@Data
@ToString
@EqualsAndHashCode
@Table
public class Interest {
  @Column(identity=true, generative=GenerativeValue.SEQUENCE)
  private Long id;

  @Column(name="id_account", references=true, required=true)
  private Account idAccount;

  @Column(name="first_interest", required=true)
  private Integer firstInterest;

  @Column(name="effective_interest")
  private Integer effectiveInterest;

  @Column(name="deadline")
  private Long maxDaysPayment;
}
