package app.banking.config;

import app.banking.models.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import postgres.addict.Pool;
import postgres.addict.migrations.Migration;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbMigrations {
  private final Pool pool;

  @Value("${addict.mutation}")
  private boolean checkMutation;

  @PostConstruct
  public void migrate(){
    Migration migration = new Migration(
      "src/main/resources/migrations",
      pool
    );
    boolean envMutate = Boolean.parseBoolean(System.getenv("MUTATE_DB"));
    migration.mutate(checkMutation || envMutate);
    migration.init(
      Account.class,
      AccountBalance.class,
      Category.class,
      Interest.class,
      Transaction.class,
      TransactionGroup.class
    );
    migration.readAllIn("alter");
    log.info("migration done !");
  }
}
