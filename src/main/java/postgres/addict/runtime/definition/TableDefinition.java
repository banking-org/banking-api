package postgres.addict.runtime.definition;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import postgres.addict.Table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import static postgres.addict.Utils.toSnakeCase;
import static postgres.addict.Utils.stringAorB;


@ToString
@EqualsAndHashCode
public class TableDefinition<T> {
  @Getter
  private final String name;
  @Getter
  private final String schema;
  @Getter
  private final Class<T> clazz;
  private final HashMap<String, ColumnDefinition> columns = new HashMap<>();

  public TableDefinition(Class<T> clazz) throws Exception {
    if (clazz.isAnnotationPresent(Table.class)) {
      this.clazz = clazz;
      Table table = clazz.getAnnotation(Table.class);
      this.name = toSnakeCase(
        stringAorB(
          table.name(),
          clazz.getSimpleName()
        )
        .toLowerCase()
      );
      this.schema = stringAorB(table.schema(), "public");

      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
        ColumnDefinition columnDef = new ColumnDefinition(field);
        if (columnDef.isColumn()) {
          if (columnDef.isIdentity()) {
            ColumnDefinition columnIdentity = this.columns.get("0");
            if (columnIdentity != null) {
              throw new Exception("Table: " + this.name + " has already one column used for primary key");
            }
            this.columns.put("0", columnDef);
          } else {
            this.columns.put(columnDef.getName(), columnDef);
          }
        }
      }
      if (this.columns.get("0") == null) {
        throw new Exception("Table: " + this.name + " doesn't contain column used for primary key");
      }
      return;
    }
    throw new Exception("class: " + clazz.getName() + " doesn't have annotation @Table");
  }

  public ColumnDefinition getColumnIdentity(){
    return this.columns.get("0");
  }

  public List<ColumnDefinition> getColumns(){
    return List.copyOf(this.columns.values());
  }

  public ColumnDefinition getColumn(String name){
    if(this.columns.get("0").getName().equals(name)){
      return this.columns.get("0");
    }
    return this.columns.get(name);
  }

  public boolean hasColumn(String name){
    return
      this.columns.get(name) != null ||
      this.columns.get("0").getName().equals(name);
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
