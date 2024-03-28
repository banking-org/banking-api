package postgres.addict.migrations;

import lombok.SneakyThrows;
import postgres.addict.GenerativeValue;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.util.ArrayList;
import java.util.List;

import static postgres.addict.TypeMapper.typeLimiter;

public class MigrationQuery<T> {
  private final String tableName;
  private final List<ColumnDefinition> columns;

  public MigrationQuery(TableDefinition<T> table){
    this.tableName = table.getName();
    this.columns = table.getColumns();
  }

  @SneakyThrows
  public MigrationQuery(Class<?> entity){
    TableDefinition<?> table = new TableDefinition<>(entity);
    this.tableName = table.getName();
    this.columns = table.getColumns();
  }

  public String dropTable(){
    return "DROP TABLE IF EXISTS \"" + tableName + "\" CASCADE";
  }

  public String createTable(){
    List<String> columns = new ArrayList<>();
    for (ColumnDefinition column: this.columns){
      columns.add(
        this.lineColumnOnCreate(column)
      );
    }
    return "CREATE TABLE IF NOT EXISTS \"" +
      tableName +
      "\" (\n" +
      String.join(",\n", columns) +
      "\n);\n";
  }

  private String lineColumnOnCreate(ColumnDefinition column){
    return "  \"" + column.getName() + "\" " + typeLimiter(column) + (column.isRequired() ? " NOT NULL" : "") + commonAddColumn(column);
  }

  public static String commonAddColumn(ColumnDefinition column){
    StringBuilder builder = new StringBuilder(column.isUnique() ? " UNIQUE" : "");
    if(!column.getDefaultValue().isEmpty()){
      builder
        .append(" DEFAULT ")
        .append(column.getDefaultValue());
    }else if(column.getGenerative() == GenerativeValue.UUID){
      builder.append(" DEFAULT gen_random_uuid()");
    }
    if(column.isIdentity()){
      builder.append(" PRIMARY KEY");
    }
    if(column.isReferences()){
      builder
        .append(" REFERENCES \"")
        .append(column.getRefTableDefinition().getName())
        .append("\"(")
        .append(column.getRefTableDefinition().getColumnIdentity().getName())
        .append(") ON DELETE CASCADE");
    }
    return builder.toString();
  }
}
