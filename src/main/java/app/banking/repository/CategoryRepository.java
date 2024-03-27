package app.banking.repository;

import app.banking.models.Category;
import app.banking.models.TransactionType;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import postgres.addict.Queries;

import java.util.List;

@Repository
public class CategoryRepository extends CommonCrud<Category, Long> {
  @SneakyThrows
  public Category findByNameAndType(String name, TransactionType type) {
    return objectFromQueries(Queries
      .select()
      .where()
        .equals("name", name)
        .and()
        .equals("only_on", type)
      .end()
    );
  }

  @SneakyThrows
  public List<Category> findAllByType(TransactionType type) {
    return listFromQueries(Queries
      .select()
      .where()
        .equals("only_on", type)
      .end()
    );
  }
}
