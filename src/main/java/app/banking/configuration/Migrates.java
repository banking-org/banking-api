package app.banking.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import postgres.addict.Pool;
import postgres.addict.migrations.Migration;

@Slf4j
@Component
@RequiredArgsConstructor
public class Migrates {
  private final Pool pool;

  @PostConstruct
  public void doMigrates(){
    Migration migration = new Migration(
        "src/main/resources/migrations",
        pool
    );
    migration.readAllIn("alter");
    log.info("migration done !");
  }
}
