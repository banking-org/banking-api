package app.banking.DTO;

import app.banking.models.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GetAccountDto extends UpSetAccountDto {
  private Long id;

  public static GetAccountDto getFromAccount(Account account){
    GetAccountDto accountDto = new GetAccountDto();
    accountDto.setId(account.getId());
    accountDto.setLastname(account.getLastname());
    accountDto.setFirstname(account.getFirstname());
    accountDto.setSalary(account.getSalary());
    accountDto.setBirthdate(account.getBirthdate());
    accountDto.setAccountNumber(account.getAccountNumber());
    accountDto.setType(account.getType().name());
    return accountDto;
  }
}
