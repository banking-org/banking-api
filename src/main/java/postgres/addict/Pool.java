package postgres.addict;

import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class Pool implements ConnectionPool {
  @Getter
  static Pool registry;

  private final List<Connection> connectionPool;
  private final List<Connection> usedConnections = new ArrayList<>();

  private final String url;
  private final String user;
  private final String password;

  private Pool(String url, String user, String password, List<Connection> pool) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.connectionPool = pool;
  }

  /**
   * create a pool connection for all the app globally
   */
  @SneakyThrows
  public static Pool create(
      String url,
      String user,
      String password
  ){
    final int INITIAL_POOL_SIZE = 10;
    List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
    for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
      pool.add(createConnection(url, user, password));
    }
    Pool pooled = new Pool(url, user, password, pool);
    if(registry == null){
      registry = pooled;
    }else {
      throw new Exception("a pool configuration is already created!");
    }
    return pooled;
  }

  @SneakyThrows
  private static Connection createConnection(String url, String user, String password ){
    return DriverManager.getConnection(url, user, password);
  }

  @Override
  public Connection getConnection() {
    Connection connection = connectionPool
        .remove(connectionPool.size() - 1);
    usedConnections.add(connection);
    return connection;
  }

  /**
   * Transit a used connection to a free connection that could be used later
   *
   * @param connection connection to be released
   */
  @Override
  @SneakyThrows
  public boolean releaseConnection(Connection connection) {
    connection.clearWarnings();
    if(!connection.isClosed()){
      connectionPool.add(connection);
    }else {
      connectionPool.add(createConnection(url, user, password));
    }
    return usedConnections.remove(connection);
  }

  public int getSize() {
    return connectionPool.size() + usedConnections.size();
  }
}
