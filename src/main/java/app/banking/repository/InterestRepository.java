package app.banking.repository;

import app.banking.models.Interest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class InterestRepository extends CommonCrud<Interest, Long> {

  @SneakyThrows
  public Optional<Interest> findByAccountId(Long id){
    PreparedStatement statement = createStatement("SELECT * FROM @table WHERE id_account = ?");
    statement.setObject(1, id);
    return resultSetOptional(statement);
  }
}
