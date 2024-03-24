package app.banking.repository;

import app.banking.models.Category;
import app.banking.models.TransactionType;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class CategoryRepository extends CommonCrud<Category, Long> {
  @SneakyThrows
  public Category findByNameAndType(String name, TransactionType type) {
    PreparedStatement statement = createStatement("SELECT name FROM @table WHERE name = ? AND only_on = ?");
    statement.setString(1, name);
    statement.setString(2, type.name());
    return forceResultSet(statement);
  }

  @SneakyThrows
  public List<Category> findAllByType(TransactionType type) {
    PreparedStatement statement = createStatement("SELECT * FROM @table WHERE only_on = ?");
    statement.setString(1, type.name());
    return resultSetList(statement);
  }
}
