package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode
public class AccountData {
  private Long id;
  private String lastname;
  private String firstname;
  private LocalDate birthdate;
  private Double salary;
  private String accountNumber;
  private String accountType;
  private String bank;
  private Instant updatedAt;
}
