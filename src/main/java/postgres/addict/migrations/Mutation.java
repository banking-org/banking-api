package postgres.addict.migrations;

import lombok.SneakyThrows;
import postgres.addict.ColumnType;
import postgres.addict.Pool;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import static postgres.addict.TypeMapper.typeLimiter;
import static postgres.addict.migrations.MigrationQuery.commonAddColumn;

public class Mutation {
  public static void check(TableDefinition<?> def){
    Mutation mutation = new Mutation(def);
    mutation.applyAll();
  }

  private final TableDefinition<?> def;
  private final String alterBase;
  public Mutation(TableDefinition<?> def){
    this.def = def;
    this.alterBase = "ALTER TABLE " +
      def.getSchema() +
      ".\"" +
      def.getName() +
      "\" ";
  }

  @SneakyThrows
  public void applyAll(){
    Pool pool = Pool.getRegistry();
    Connection connection = pool.getConnection();
    DatabaseMetaData metadata = connection.getMetaData();
    for (ColumnDefinition column : def.getColumns()) {
      if(hasColumn(metadata, column.getName())){
        execute(connection, alterType(column));
        execute(connection, alterDefaultValue(column));
        if(!column.isRequired()){
          execute(connection, dropNotNull(column));
        }
        if(column.isUnique()){
          execute(connection, alterUnique(column));
        }
        if(column.isReferences()){
          execute(connection, addRefs(column));
        }
      }else {
        execute(connection, addColumn(column));
      }
    }
    pool.releaseConnection(connection);
  }

  public void execute(Connection connection, String query){
    try {
      Statement statement = connection.createStatement();
      statement.execute(query);
    }catch (Exception ignored){}
  }

  public String dropNotNull(ColumnDefinition def) {
    return alterBase + "ALTER " + def.getName() + " DROP NOT NULL";
  }

  public String alterType(ColumnDefinition def){
    String type = def.getColumnType();
    String cleanType = type;
    switch (type) {
      case ColumnType.VARCHAR,
        ColumnType.BPCHAR,
        ColumnType.CHAR,
        ColumnType.BIT:
          cleanType = type + "(" + def.getSize() + ")";
      break;
      case ColumnType.NUMERIC:
        cleanType = type + "(" + def.getPrecision() + ", " + def.getScale() + ")";
        break;
      default:
        break;
    }
    return alterBase + "ALTER COLUMN " + def.getName() + " TYPE " + cleanType;
  }

  public String alterUnique(ColumnDefinition def){
    return alterBase + "ADD UNIQUE (" + def.getName() + ")";
  }

  public String alterDefaultValue(ColumnDefinition def){
    String base = alterBase + "ALTER " + def.getName() + " ";
    if(def.getDefaultValue().isEmpty()){
      return base + "DROP DEFAULT";
    }
    return base + "SET DEFAULT " + def.getDefaultValue();
  }

  public String addRefs(ColumnDefinition def){
    TableDefinition<?> refDef = def.getRefTableDefinition();
    String refColId = refDef.getColumnIdentity().getName();
    return alterBase + "ADD FOREIGN KEY (" + def.getName() + ") REFERENCES \"" + refDef.getName() + "\"." + refColId;
  }

  private String addColumn(ColumnDefinition def){
    return alterBase + "ADD COLUMN " + def.getName() + " " + typeLimiter(def)  + commonAddColumn(def);
  }

  private boolean hasColumn(DatabaseMetaData metadata, String name){
    try {
      ResultSet resultSet = metadata.getColumns(
        null,
        def.getSchema(),
        def.getName(),
        name
      );
      return resultSet.next();
    }catch (Exception ignored){}
    return false;
  }
}
