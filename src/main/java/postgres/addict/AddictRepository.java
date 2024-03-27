package postgres.addict;

import lombok.SneakyThrows;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;
import postgres.addict.runtime.utils.MapResultSet;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

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
    Class<?> entityClass = this.getEntityClass(this.getClass());
    TableDefinition<T> tableDefinition = (TableDefinition<T>) new TableDefinition<>(entityClass);
    this.tableName = tableDefinition.getName();
    this.idColumnName = tableDefinition.getColumnIdentity().getName();
    this.tableDefinition = tableDefinition;
    this.resultSetMapper = new MapResultSet<>(tableDefinition);
  }

  private void setValues(
    T values,
    ColumnDefinition colDef,
    TreadInsertValue<String, Object> tread
  ){
    try {
      Object v = colDef.getField().get(values);
      if(v != null){
        if(colDef.isReferences()){
          v = colDef
            .getRefTableDefinition()
            .getColumnIdentity()
            .getField()
            .get(v);
          tread.tread(colDef.getName(), v);
        }else {
          tread.tread(colDef.getName(), v);
        }
      }
    }catch (Exception ignored){}
  }

  public void getInsertedValues(T values, TreadInsertValue<String, Object> tread){
    ColumnDefinition columId = tableDefinition.getColumnIdentity();
    setValues(values, columId, tread);
    for (ColumnDefinition column : tableDefinition.getColumns()) {
      setValues(values, column, tread);
    }
  }

  @SuppressWarnings("all")
  protected ResultSet executeQueries(Queries queries) throws Exception {
    Connection connection = Pool.registry.getConnection();
    try {
      Statement statement = connection.createStatement();
      String sql =  queries
        .toString()
        .replaceAll("@table", this.tableName)
        .replaceAll("@id", this.idColumnName);
      return statement.executeQuery(sql);
    } finally {
      Pool.registry.releaseConnection(connection);
    }
  }

  protected Optional<T> optionalFromQueries(Queries queries){
    try {
      return resultSetMapper.optionalResultSet(executeQueries(queries));
    }catch (Exception ignored){
      return Optional.empty();
    }
  }

  protected T objectFromQueries(Queries queries){
    try {
      return resultSetMapper.mustBeOneResultSet(executeQueries(queries));
    }catch (Exception ignored){
      return null;
    }
  }

  protected List<T> listFromQueries(Queries queries){
    try {
      return resultSetMapper.enlistResultSet(executeQueries(queries));
    }catch (Exception ignored){
      return new ArrayList<>();
    }
  }
}
