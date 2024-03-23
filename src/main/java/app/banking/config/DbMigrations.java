package app.banking.config;

import app.banking.models.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import postgres.addict.Pool;
import postgres.addict.migrations.Migration;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbMigrations {
  private final Pool pool;

  @PostConstruct
  public void migrate(){
    Migration migration = new Migration(
      "src/main/resources/migrations",
      pool
    );
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
