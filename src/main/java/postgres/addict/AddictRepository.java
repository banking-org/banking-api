package postgres.addict;

import lombok.SneakyThrows;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;
import postgres.addict.runtime.utils.MapResultSet;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Do manage queries between java and postgresql. <br>
 * @param <T> the entity class that is used in the repository
 */
public class AddictRepository<T> {
  private final TableDefinition<T> tableDefinition;
  private final MapResultSet<T> resultSetMapper;
  private final String tableName;
  private final String idColumnName;

  private Class<?> getEntityClass(Class<?> clazz){
    ParameterizedType d = (ParameterizedType) clazz.getGenericSuperclass();
    return (Class<?>) d.getActualTypeArguments()[0];
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  public AddictRepository(){
    Class<?> childClass = this.getClass();
    Class<?> entityClass = this.getEntityClass(childClass);
    TableDefinition<T> tableDefinition = (TableDefinition<T>) new TableDefinition<>(entityClass);
    this.tableDefinition = tableDefinition;
    this.resultSetMapper = new MapResultSet<>(tableDefinition);

    this.tableName = tableDefinition.getName();
    this.idColumnName = tableDefinition.getColumnIdentity().getName();
  }

  protected List<T> listOfStatement(String sql){
    return resultSetList(createStatement(sql));
  }

  @SneakyThrows
  private HashMap<String, Object> getInsertedValues(T value){
    HashMap<String, Object> entries = new HashMap<>();

    Object idValue = this.getIdValue(value);
    if(idValue != null){
      entries.put(this.idColumnName, idValue);
    }

    for (ColumnDefinition column : this.tableDefinition.getColumns()) {
      Object columnValue = column.getField().get(value);
      if(columnValue != null){
        if(column.isReferences()){
          Object v = column
            .getRefTableDefinition()
            .getColumnIdentity()
            .getField()
            .get(columnValue);
          if(v instanceof Enum<?>){
            v = ((Enum<?>) v).name();
          }
          entries.put(
            column.getName(),
            v
          );
        }else {
          if(columnValue instanceof Enum<?>){
            columnValue = ((Enum<?>) columnValue).name();
          }
          entries.put(column.getName(), columnValue);
        }
      }
    }
    return entries;
  }

  @SneakyThrows
  protected HashMap<String, Object> getColumnValues(T value){
    HashMap<String, Object> entries = new HashMap<>();
    List<ColumnDefinition> columnDefinitions = this.tableDefinition.getColumns();

    for (ColumnDefinition column : columnDefinitions) {
      Object columnValue = column.getField().get(value);
      if(columnValue != null){
        if(column.isReferences()){
          entries.put(
            column.getName(),
            column
              .getRefTableDefinition()
              .getColumnIdentity()
              .getField()
              .get(column)
          );
        }else {
          entries.put(column.getName(), columnValue);
        }
      }
    }
    return entries;
  }

  private List<String> createParameters(int size){
    return Collections.nCopies(size, "?");
  }

  @SneakyThrows
  protected Object getIdValue(T value) {
    return this.tableDefinition.getColumnIdentity().getField().get(value);
  }

  @SneakyThrows
  private PreparedStatement createSaveStatement(T value){
    HashMap<String, Object> inserted = this.getInsertedValues(value);

    String columns = String.join(",", inserted.keySet());
    String params = String.join(",", createParameters(inserted.size()));
    String query = "INSERT INTO \"@table\" (" + columns + ") values (" + params + ") RETURNING *";
    PreparedStatement statement = createStatement(query);

    List<Object> values = inserted.values().stream().toList();
    for (int i = 0; i < values.size(); i++) {
      int paramIndex = i+1;
      Object columnValue = values.get(i);
      statement.setObject(paramIndex, columnValue);
    }
    return statement;
  }

  public Optional<T> save(T value){
    return resultSetOptional(createSaveStatement(value));
  }

  @SneakyThrows
  protected PreparedStatement createStatement(String sql) {
    Connection connection = Pool.registry.getConnection();
    String query = sql
      .replaceAll("@table", this.tableName)
      .replaceAll("@id", this.idColumnName)
      ;
    return connection.prepareStatement(query);
  }

  @SneakyThrows
  protected PreparedStatement createStatement(String sql, T value) {
    String query = statementWrapper(sql, value);
    return createStatement(query);
  }

  private String mapper(Object value){
    String type = value.getClass().getSimpleName().toLowerCase();
    System.out.println(value.getClass());
    return "'" + value + "'::" + type;
  }

  private String statementWrapper(String sql, T value){
    String query = sql;
    HashMap<String, Object> values = getInsertedValues(value);
    for (String key : values.keySet()) {
      Object val = values.get(key);
      query = query.replace(":" + key, mapper(val));
    }
    return query;
  }

  @SneakyThrows
  protected List<T> resultSetList(PreparedStatement statement){
    final List<T> result = this
      .resultSetMapper
      .enlistResultSet(statement.executeQuery());
    Pool.registry.releaseConnection(statement.getConnection());
    return result;
  }

  @SneakyThrows
  protected Optional<T> resultSetOptional(PreparedStatement statement){
    final Optional<T> result = this
      .resultSetMapper
      .optionalResultSet(statement.executeQuery());
    Pool.registry.releaseConnection(statement.getConnection());
    return result;
  }

  @SneakyThrows
  protected T forceResultSet(PreparedStatement statement){
    final T result = this
      .resultSetMapper
      .mustBeOneResultSet(statement.executeQuery());
    Pool.registry.releaseConnection(statement.getConnection());
    return result;
  }
}
