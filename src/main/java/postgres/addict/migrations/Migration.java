package postgres.addict.migrations;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import postgres.addict.Pool;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

@RequiredArgsConstructor
public class Migration {
  private final String path;
  private final Pool pool;

  @SneakyThrows
  public void init(Class<?>... entities){
    Connection connection = pool.getConnection();

    for (Class<?> entity : entities) {
      TableDefinition<?> tableDefinition = new TableDefinition<>(entity);
      String tableName = tableDefinition.getName();
      tableDefinition.getColumns()
          .stream()
          .filter(ColumnDefinition::isReferences)
          .forEach(columnDefinition -> {
            Class<?> clazz = columnDefinition.getRefTableDefinition().getClazz();
            new Migration(path, pool).init(clazz);
          });

      try  {

        String sqlCreateTable = new MigrationQuery<>(entity).createTable();
        Statement statement = connection.createStatement();
        statement.execute(sqlCreateTable);
        saveQueryToFile(sqlCreateTable, tableName + "_v1.sql");
      }finally {
        pool.releaseConnection(connection);
      }
    }
  }

  @SneakyThrows
  public void readAllIn(String subPath){
    Path root = Path.of(this.path, subPath);
    Files.list(root).forEach(this::readFile);
  }

  @SneakyThrows
  public void readFile(Path path){
    Path src = path.toAbsolutePath();
    File file = new File(src.toUri());

    FileReader reader = new FileReader(file);
    StringWriter stringWriter = new StringWriter();
    reader.transferTo(stringWriter);
    reader.close();
    stringWriter.close();

    Connection connection = pool.getConnection();
    try {
      String query = String.valueOf(stringWriter.getBuffer());
      Statement statement = connection.createStatement();
      statement.execute(query);
    }finally {
      pool.releaseConnection(connection);
    }
  }

  @SneakyThrows
  private void saveQueryToFile(String sql, String fileName){
    Path filePath = Path.of(path, fileName);
    File file = new File(filePath.toAbsolutePath().toString());
    FileWriter writer = new FileWriter(file);
    writer.write(sql);
    writer.close();
  }
}
