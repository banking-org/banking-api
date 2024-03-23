package app.banking.repository;

import app.banking.models.AccountBalance;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class BalanceRepository extends CommonCrud<AccountBalance, Long> {
  @SneakyThrows
  public Optional<AccountBalance> findCurrentByAccountId(Long id){
    PreparedStatement statement = createStatement(
      "SELECT * FROM @table WHERE id_account = ? ORDER BY checked_at DESC"
    );
    statement.setObject(1, id);
    return resultSetOptional(statement);
  }

  @SneakyThrows
  public Optional<AccountBalance> findAtDateByAccountId(Long id, LocalDate date){
    PreparedStatement statement = createStatement("SELECT * FROM @table WHERE id_account = ? AND checked_at = ?");
    statement.setObject(1, id);
    statement.setObject(2, date);
    return resultSetOptional(statement);
  }
}
