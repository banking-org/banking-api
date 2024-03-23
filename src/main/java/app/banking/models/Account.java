package app.banking.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.Instant;
import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode
@Table
public class Account {
  @Column(identity=true, generative=GenerativeValue.SEQUENCE)
  private Long id;

  @Column
  private String lastname;

  @Column
  private String firstname;

  @Column
  private LocalDate birthdate;

  @Column(name="net_salary")
  private Double netMonthSalary;

  @Column(name="account_number", unique=true)
  private String accountNumber;

  @Column(name="account_type")
  private AccountType accountType;

  @Column
  private String bank;

  @Column(name="updated_at", defaultValue="now()")
  private Instant updatedAt;

  public static Account onlyId(Long id){
    Account account = new Account();
    account.setId(id);
    return account;
  }
}
