package postgres.addict.runtime.definition;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import postgres.addict.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static postgres.addict.Utils.spaceToSnakeCase;


@Getter
@ToString
@EqualsAndHashCode
public class TableDefinition<T> {
  private final String name;
  private final String schema;
  private final List<ColumnDefinition> columns = new ArrayList<>();
  private final Class<T> clazz;
  private ColumnDefinition columnIdentity;

  public TableDefinition(Class<T> clazz) throws Exception {
    Table table = clazz.getAnnotation(Table.class);
    if (table != null) {
      this.clazz = clazz;
      String name = spaceToSnakeCase(table.name());

      if (name.isEmpty()) {
        name = clazz.getSimpleName().toLowerCase();
      }

      this.name = name;
      this.schema = table.schema().isEmpty() ? "public" : table.schema();
      Field[] fields = clazz.getDeclaredFields();

      for (Field field : fields) {
        ColumnDefinition columnDef = new ColumnDefinition(field);
        if (columnDef.isColumn()) {
          if (columnDef.isIdentity()) {
            if (this.columnIdentity != null) {
              throw new Exception("Table: " + this.name + " has already one column used for primary key");
            }
            this.columnIdentity = columnDef;
          } else {
            this.columns.add(columnDef);
          }
        }
      }
      if (this.columnIdentity == null) {
        throw new Exception("Table: " + this.name + " doesn't contain column used for primary key");
      }
      return;
    }

    throw new Exception("class: " + clazz.getName() + " doesn't have annotation @Table");
  }

  @SuppressWarnings("all")
  public T createInstance() {
    try {
      return this.clazz.newInstance();
    }catch(Exception ignored){
      try {
        return this.clazz.getDeclaredConstructor().newInstance();
      }catch (Exception exception){
        try {
          return (T) this.clazz.getEnclosingConstructor().newInstance();
        }catch (Exception e){}
      }
    }
    return null;
  }
}
