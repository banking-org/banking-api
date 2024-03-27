package postgres.addict;

import lombok.SneakyThrows;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.util.*;
import java.util.stream.Collectors;

import static postgres.addict.Utils.objectToPsqlValue;

public class Queries {
  public static Queries of(String query){
    return new Queries(query);
  }

  public static Insertion insertion(){
    return new Insertion();
  }

  public static Queries delete(){
    return new Queries("DELETE FROM \"@table\"");
  }

  public static Queries select(){
    return new Queries("SELECT * FROM \"@table\"");
  }

  public static Queries selectFromQuery(Queries queries){
    return new Queries("SELECT * FROM " + queries);
  }

  public static AliasColumn aliasColumns(){
    return new AliasColumn();
  }

  public static Queries select(String... columns){
    return new Queries("SELECT " + String.join(", ", columns) + " FROM \"@table\"");
  }

  public static Queries select(Queries query){
    return new Queries("SELECT " + query + " FROM \"@table\"");
  }

  public static Queries selectDistinct(){
    return new Queries("SELECT DISTINCT * FROM \"@table\"");
  }

  public static Queries selectDistinct(String... columns){
    return new Queries("SELECT DISTINCT " +  String.join(", ", columns)  + " FROM \"@table\"");
  }

  public static Queries selectDistinct(Queries query){
    return new Queries("SELECT " + query + " FROM \"@table\"");
  }

  public static Updates update(){
    return new Updates();
  }

  private String sql = "";

  private Queries(String baseSql){
    this.sql += baseSql;
  }

  public Queries as(String alias){
    this.sql += " as " + alias;
    return this;
  }

  public Queries call(String function, Object... args){
    String argJoined = Arrays
      .stream(args)
      .map(Utils::objectToPsqlValue)
      .collect(Collectors.joining(", "));
    this.sql += " " + function + "(" + argJoined + ")";
    return this;
  }

  public Queries groupBy(String... columns){
    this.sql += " GROUP BY " + String.join(", ", columns);
    return this;
  }

  public Orders orderBy(){
    return new Orders(this);
  }

  private String templateJoin(
    String prefix, String tableFrom,
    String colFrom, String tableTo, String colTo,
    int index
  ){
    String alias = colTo + "_" + colFrom + "_" + index;
    return  " " +
      prefix +
      " \"" +
      tableTo +
      "\" as " + alias + " ON \"" +
      alias +
      "\"." +
      colTo +
      " = \"" +
      tableFrom +
      "\"." +
      colFrom;
  }

  @SneakyThrows
  private Queries join(String prefix, Class<?> from, Class<?> target, String... columns){
    TableDefinition<?> fromDef = new TableDefinition<>(from);
    TableDefinition<?> targetDef = new TableDefinition<>(target);
    HashSet<String> joins = new HashSet<>();
    if(columns.length > 0){
      for (int i = 0; i < columns.length; i++) {
        String column = columns[i];
        ColumnDefinition colId = fromDef.getColumnIdentity();
        if (colId.getName().equals(column)) {
          joins.add(templateJoin(
            prefix,
            fromDef.getName(),
            colId.getName(),
            targetDef.getName(),
            targetDef.getColumnIdentity().getName(),
            i
          ));
        }

        for (ColumnDefinition fromDefColumn : fromDef.getColumns()) {
          if (fromDefColumn.getName().equals(column)) {
            joins.add(templateJoin(
              prefix,
              fromDef.getName(),
              fromDefColumn.getName(),
              targetDef.getName(),
              targetDef.getColumnIdentity().getName(),
              i
            ));
          }
        }
      }
    }else {
      List<ColumnDefinition> fromDefColumns = fromDef.getColumns();
      for (int i = 0; i < fromDefColumns.size(); i++) {
        ColumnDefinition column = fromDefColumns.get(i);
        if (
          column.isReferences() &&
          column.getRefTableDefinition().getClazz().equals(target)
        ) {
          TableDefinition<?> refDef = column.getRefTableDefinition();
          joins.add(templateJoin(
            prefix,
            fromDef.getName(),
            column.getName(),
            refDef.getName(),
            refDef.getColumnIdentity().getName(),
            i
          ));
        }
      }
    }
    this.sql += String.join(" ", joins);
    return this;
  }

  public Queries innerJoin(Class<?> from, Class<?> target, String... columns){
    return join("INNER JOIN", from, target, columns);
  }

  public Queries leftJoin(Class<?> from, Class<?> target, String... columns){
    return join("LEFT JOIN", from, target, columns);
  }

  public Queries rightJoin(Class<?> from, Class<?> target, String... columns){
    return join("RIGHT JOIN", from, target, columns);
  }

  public Queries fullJoin(Class<?> from, Class<?> target, String... columns){
    return join("FULL JOIN", from, target, columns);
  }

  public Queries leftOuterJoin(Class<?> from, Class<?> target, String... fromColumn){
    return join("LEFT OUTER JOIN", from, target, fromColumn);
  }

  public Queries rightOuterJoin(Class<?> from, Class<?> target, String... columns){
    return join("RIGHT OUTER JOIN", from, target, columns);
  }

  public Queries crossJoin(Class<?> from, Class<?> target, String... columns){
    return join("CROSS JOIN", from, target, columns);
  }

  public Queries limit(long size){
    this.sql += " LIMIT " + size;
    return null;
  }
  public Queries limit(int size){
    this.sql += " LIMIT " + size;
    return null;
  }
  public Queries offset(int size){
    this.sql += " OFFSET " + size;
    return null;
  }
  public Queries offset(long size){
    this.sql += " OFFSET " + size;
    return null;
  }

  public Queries returns(){
    this.sql += " RETURNING *";
    return this;
  }

  public WhereQuery where() {
    return new WhereQuery(this);
  }

  public static class AliasColumn {
    private final List<String> aliases = new ArrayList<>();
    public AliasColumn add(String query, String asColumn){
      aliases.add(query + " AS " + asColumn);
      return this;
    }
    public AliasColumn noAlias(String column){
      aliases.add(column);
      return this;
    }
    public Queries toQuery(){
      return new Queries(String.join(", ", aliases));
    }
  }

  public static class Updates {
    private final List<String> updates = new ArrayList<>();
    public Updates setUpdate(String column, Object value){
      updates.add(column + " = " + objectToPsqlValue(value));
      return this;
    }
    public Queries end(){
      return new Queries("UPDATE \"@table\" SET " + String.join(", ", updates));
    }
  }

  public static class Insertion {
    private final List<String> columns = new ArrayList<>();
    private final List<String> values = new ArrayList<>();
    public Insertion value(String column, Object value){
      columns.add(column);
      values.add(objectToPsqlValue(value));
      return this;
    }
    public Queries end(){
      return new Queries(
        "INSERT INTO \"@table\" (" +
          String.join(", ", columns) +
          ") VALUES (" +
          String.join(", ", values) +
          ")"
      );
    }
  }

  public static class Orders {
    private final Queries queries;
    private Orders(Queries queries){
      this.queries = queries;
    }
    private String sql = "ORDER BY ";
    public Orders add(String... columns){
      this.sql += String.join(", ", columns);
      return this;
    }
    public Orders add(String column){
      this.sql += column;
      return this;
    }
    public Orders addAsc(String column){
      this.sql += column + " ASC";
      return this;
    }
    public Orders addDesc(String column){
      this.sql += column + " DESC";
      return this;
    }
    public Queries end(){
      return new Queries(queries + " " + sql);
    }
  }

  public static class WhereQuery {
    private final Queries queries;
    private WhereQuery(Queries queries){
      this.queries = queries;
    }
    private String sql = "WHERE";
    public WhereQuery openWrap(){
      this.sql += " (";
      return this;
    }
    public WhereQuery closeWrap(){
      this.sql += " )";
      return this;
    }
    public WhereQuery equals(String column, Object value){
      this.sql += " " + column + " = " + objectToPsqlValue(value);
      return this;
    }
    public WhereQuery and(){
      this.sql += " AND";
      return this;
    }
    public WhereQuery or(){
      this.sql += " OR";
      return this;
    }
    public WhereQuery like(String column, String value){
      this.sql += " LIKE '" + value + "'";
      return this;
    }
    public WhereQuery iLike(String column, Object value){
      this.sql += " ILIKE '" + value + "'";
      return this;
    }
    public WhereQuery greater(String column, Object value){
      this.sql += " " + column + " > " + value;
      return this;
    }
    public WhereQuery less(String column, Object value){
      this.sql += " " + column + " < " + value;
      return this;
    }
    public WhereQuery greaterEquals(String column, Object value){
      this.sql += " " + column + " >= " + value;
      return this;
    }
    public WhereQuery lessEquals(String column, Object value){
      this.sql += " " + column + " <= " + value;
      return this;
    }
    public WhereQuery notNull(String column){
      this.sql += " IS NOT NULL";
      return this;
    }
    public WhereQuery isNull(String column){
      this.sql += " IS NULL";
      return this;
    }
    public WhereQuery isIn(String column, List<Object> list){
      this.sql += " " + column + " = ANY (" +
        String.join(", ",
          list
            .stream()
            .map(Utils::objectToPsqlValue)
            .toList()
        ) + ")";
      return this;
    }
    public WhereQuery between(String column, Object a, Object b){
      this.sql += " " + column + " BETWEEN " + objectToPsqlValue(a) + " AND " + objectToPsqlValue(b);
      return this;
    }
    public WhereQuery notBetween(String column, Object a, Object b){
      this.sql += " " + column + " NOT BETWEEN " + objectToPsqlValue(a) + " AND " + objectToPsqlValue(b);
      return this;
    }
    public Queries end(){
      return new Queries(queries + " " + sql);
    }
  }

  @Override
  public String toString() {
    return sql;
  }
}
