package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.LocalDate;

@Table
@Data
@ToString
@EqualsAndHashCode
public class Account {
  @Column(identity=true, generative=GenerativeValue.SEQUENCE)
  private Long id;

  @Column(name="first_name", required=true)
  private String firstname;

  @Column(name="last_name", required=true)
  private String lastname;

  @Column(required=true)
  private LocalDate birthdate;

  @Column(name="net_salary", required=true)
  private Double salary;

  @Column(name="account_number", required=true)
  private String accountNumber;

  @Column(defaultValue="'NORMAL'")
  private AccountType type;
}
