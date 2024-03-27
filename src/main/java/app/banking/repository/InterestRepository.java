package app.banking.repository;

import app.banking.models.Interest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import postgres.addict.Queries;

import java.util.Optional;

@Repository
public class InterestRepository extends CommonCrud<Interest, Long> {
  @SneakyThrows
  public Optional<Interest> findByAccountId(Long id){
    return optionalFromQueries(Queries
      .select()
      .where()
        .equals("id_account", id)
      .end()
    );
  }
}
