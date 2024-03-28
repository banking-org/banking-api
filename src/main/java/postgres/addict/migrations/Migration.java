package postgres.addict.migrations;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import postgres.addict.Pool;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

@Slf4j
@RequiredArgsConstructor
public class Migration {
  private final String path;
  private final Pool pool;

  private boolean checkMutation = false;

  public void mutate(boolean value){
    this.checkMutation = value;
  }

  @SneakyThrows
  public void init(Class<?>... entities){
    Connection connection = pool.getConnection();
    ResultSet tableSet = getTableSet(connection);

    for (Class<?> entity : entities) {
      TableDefinition<?> tableDefinition = new TableDefinition<>(entity);
      tableDefinition.getColumns()
        .stream()
        .filter(ColumnDefinition::isReferences)
        .forEach(def -> {
          Class<?> clazz = def.getRefTableDefinition().getClazz();
          new Migration(path, pool).init(clazz);
        });

      String tableName = tableDefinition.getName();
      boolean tableExists = false;
      if(tableSet.next()){
        String schema = tableDefinition.getSchema();
        if(
          tableName.equals(tableSet.getString("TABLE_NAME")) &&
          schema.equals(tableSet.getString("TABLE_SCHEM"))
        ){
          tableExists = true;
        }
      }
      if(!tableExists){
        String sqlCreateTable = new MigrationQuery<>(tableDefinition).createTable();
        Statement statement = connection.createStatement();
        statement.execute(sqlCreateTable);
        saveQueryToFile(sqlCreateTable, tableName + "_v1.sql");
      }else {
        if(checkMutation){
          log.info("Check schema mutations...");
          Mutation.check(tableDefinition);
        }
      }
    }
  }

  @SneakyThrows
  @SuppressWarnings("all")
  public void readAllIn(String subPath){
    Path root = Path.of(this.path, subPath);
    if (Files.exists(root)) {
      Files.list(root).forEach(this::readFile);
    }
  }

  @SneakyThrows
  @SuppressWarnings("all")
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

  @SneakyThrows
  private ResultSet getTableSet(Connection connection){
    DatabaseMetaData metadata = connection.getMetaData();
    return metadata
      .getTables(
        null,
        null,
        null,
        new String[]{ "TABLE" }
      );
  }
}
