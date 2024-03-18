package postgres.addict.migrations;

import lombok.SneakyThrows;
import postgres.addict.ColumnType;
import postgres.addict.GenerativeValue;
import postgres.addict.TypeMapper;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.util.ArrayList;
import java.util.List;

public class MigrationQuery<T> {
  private final String tableName;
  private final List<ColumnDefinition> columns = new ArrayList<>();

  public MigrationQuery(TableDefinition<T> table){
    this.tableName = table.getName();
    this.columns.add(table.getColumnIdentity());
    this.columns.addAll(table.getColumns());
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  public MigrationQuery(Class<?> entity){
    TableDefinition<T> table = (TableDefinition<T>) new TableDefinition<>(entity);
    this.tableName = table.getName();
    this.columns.add(table.getColumnIdentity());
    this.columns.addAll(table.getColumns());
  }

  public String createTable(){
    List<String> columns = new ArrayList<>();
    for (ColumnDefinition column: this.columns){
      columns.add(
          this.lineColumnOnCreate(column)
      );
    }
    return "CREATE TABLE IF NOT EXISTS \"" + this.tableName + "\" (\n" + String.join(",\n", columns) + "\n);\n";
  }

  public String dropTable(){
    return "DROP TABLE IF EXISTS \"" + this.tableName + "\" CASCADE";
  }

  private String lineColumnOnCreate(ColumnDefinition column){
    String columnName = column.getName();
    String bruteType = column.getColumnType();
    String baseType =
        (column.getGenerative() == GenerativeValue.SEQUENCE)
            ? TypeMapper.typeToSequenceIfPossible(bruteType)
            : bruteType;

    return "  \"" + columnName + "\" " +
        typeLimiter(column.isReferences() ? bruteType : baseType, column) +
        (column.isRequired() ? " NOT NULL" : "") +
        (column.isUnique() ? " UNIQUE" : "") +
        (
            !column.getDefaultValue().isEmpty()
                ? (" DEFAULT " + column.getDefaultValue())
                : (column.getGenerative() == GenerativeValue.UUID ? " DEFAULT gen_random_uuid()" : "")
        ) +
        (column.isIdentity() ? " PRIMARY KEY" : "") +
        (
            column.isReferences() ? " REFERENCES \"" +
            column.getRefTableDefinition().getName() + "\"(" +
            column.getRefTableDefinition().getColumnIdentity().getName() + ") ON DELETE CASCADE" : ""
        );
  }

  public static String typeLimiter(String type, ColumnDefinition definition){
    return switch (type){
      case ColumnType.VARCHAR,
          ColumnType.BPCHAR,
          ColumnType.CHAR,
          ColumnType.BIT -> type + "(" + definition.getSize() + ")";
      case ColumnType.NUMERIC -> type + "(" + definition.getPrecision() + ", " + definition.getScale() + ")";
      default -> type;
    };
  }
}
