package app.banking.repository;

import app.banking.models.Account;
import app.banking.models.AccountBalance;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import postgres.addict.Queries;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class BalanceRepository extends CommonCrud<AccountBalance, Long> {
  @SneakyThrows
  public Optional<AccountBalance> findCurrentByAccountId(Long id){
    return optionalFromQueries(Queries
      .select()
      .innerJoin(AccountBalance.class, Account.class)
      .where()
        .equals("id_account", id)
      .end()
      .orderBy()
        .addDesc("checked_at")
      .end()
    );
  }

  @SneakyThrows
  public Optional<AccountBalance> findAtDateByAccountId(Long id, LocalDate date){
    return optionalFromQueries(Queries
      .select()
      .innerJoin(AccountBalance.class, Account.class)
      .where()
        .equals("id_account", id)
        .and()
        .equals("checked_at", date)
      .end()
    );
  }
}
