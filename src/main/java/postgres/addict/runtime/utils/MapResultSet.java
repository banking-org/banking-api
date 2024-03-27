package postgres.addict.runtime.utils;

import lombok.SneakyThrows;
import postgres.addict.runtime.definition.ColumnDefinition;
import postgres.addict.runtime.definition.TableDefinition;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapResultSet<T> {
  private final String tableName;
  private final TableDefinition<T> definition;
  private final List<ColumnDefinition> orderedColumns = new ArrayList<>();

  public MapResultSet(TableDefinition<T> definition) {
    this.definition = definition;
    this.tableName = definition.getName();
    this.orderedColumns.add(definition.getColumnIdentity());
    this.orderedColumns.addAll(definition.getColumns());
  }

  public Object mapper(Object value, ColumnDefinition colDef, String column){
    if(colDef.getName().equals(column)){
      Class<?> type = colDef.getField().getType();
      if(value instanceof Date && type.equals(LocalDate.class)){
        return ((Date) value).toLocalDate();
      }

      if(value instanceof Timestamp && type.equals(Instant.class)){
        return ((Timestamp) value).toInstant();
      }
    }
    return value;
  }

  /**
   * resultSet.next() should be called before call this method
   */
  @SneakyThrows
  private T getInstance(ResultSet resultSet) {
    ResultSetMetaData resMeta = resultSet.getMetaData();
    int resLength = resMeta.getColumnCount();
    T instance = this.definition.createInstance();

    for (int i = 1; i <= resLength; i++) {
      String columnName = resMeta.getColumnName(i);
      String tableName = resMeta.getTableName(i);

      for (ColumnDefinition orderedColumn : this.orderedColumns) {
        Object columnValue = mapper(
          resultSet.getObject(i),
          orderedColumn,
          columnName
        );

        if(
          this.tableName.equals(tableName) &&
          orderedColumn.getName().equals(columnName)
        ){
          if(orderedColumn.isReferences() && columnValue != null){
            TableDefinition<?> refDefinition = orderedColumn.getRefTableDefinition();
            Object refInstance = new MapResultSet<>(refDefinition).getInstance(resultSet);
            if(refInstance != null){
              ColumnDefinition coldIdDef = refDefinition.getColumnIdentity();
              if(coldIdDef.getField().get(refInstance) == null){
                Field currentField = coldIdDef.getField();
                Class<?> currentType = currentField.getType();
                if(currentField.isEnumConstant() || currentType.isEnum()){
                  coldIdDef.getField().set(refInstance, this.parseEnum(currentType, columnValue));
                }else {
                  coldIdDef.getField().set(refInstance, columnValue);
                }
              }
              orderedColumn.getField().set(instance, refInstance);
            }
          }else {
            Field currentField = orderedColumn.getField();
            Class<?> currentType = currentField.getType();
            if(columnValue != null){
              if(
                currentField.isEnumConstant() ||
                currentType.isEnum()
              ){
                currentField.set(instance, this.parseEnum(currentType, columnValue));
              }else {
                currentField.set(instance, columnValue);
              }
            }
          }
        }
      }
    }

    return instance;
  }

  @SneakyThrows
  public List<T> enlistResultSet(ResultSet resultSet){
    List<T> list = new ArrayList<>();
    while (resultSet.next()){
      T i = this.getInstance(resultSet);
      if(i != null){
        list.add(i);
      }
    }
    return list;
  }

  @SneakyThrows
  public T mustBeOneResultSet(ResultSet resultSet){
    if(resultSet.next()){
      return this.getInstance(resultSet);
    }
    return null;
  }

  @SneakyThrows
  public Optional<T> optionalResultSet(ResultSet resultSet){
    if(resultSet.next()){
      return Optional.of(this.getInstance(resultSet));
    }
    return Optional.empty();
  }

  @SneakyThrows
  @SuppressWarnings({"unchecked", "rawtypes"})
  private Enum<?> parseEnum(Class<?> enumClass, Object enumConstant) {
    return Enum.valueOf((Class<Enum>) enumClass, (String) enumConstant);
  }
}
