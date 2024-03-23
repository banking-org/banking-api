package app.banking.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode
public class UpSetAccountPayload {
  private String lastname;
  private String firstname;
  private LocalDate birthdate;
  private Double salary;
  private String type;
  private String accountNumber;
  private String bank;
}
