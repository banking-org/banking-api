package app.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import postgres.addict.Pool;

@Configuration
public class Connection {
  @Bean
  public Pool createPool(){
    String url = System.getenv("DATABASE_URL");
    String user = System.getenv("DATABASE_USERNAME");
    String password = System.getenv("DATABASE_PASSWORD");
    return Pool.create(url, user, password);
  }
}
