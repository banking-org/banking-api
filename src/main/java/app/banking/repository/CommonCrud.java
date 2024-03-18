package app.banking.repository;

import lombok.SneakyThrows;
import postgres.addict.AddictRepository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CommonCrud<T, R> extends AddictRepository<T> {
  public List<T> findAll(){
    return listOfStatement("SELECT * FROM @table");
  }

  @SneakyThrows
  public Optional<T> findById(R id){
    PreparedStatement statement = createStatement("SELECT * FROM @table WHERE @id = ?");
    statement.setObject(1, id);
    return resultSetOptional(statement);
  }

  @SneakyThrows
  public Optional<T> deleteById(R id){
    PreparedStatement statement = createStatement("DELETE FROM @table WHERE @id = ?");
    statement.setObject(1, id);
    return resultSetOptional(statement);
  }

  @SneakyThrows
  public Optional<T> updateById(R id, T newValue){
    HashMap<String, Object> values = getColumnValues(newValue);
    String params = String.join(",", values.keySet()
        .stream().map(key -> key + " = ?").toList()
    );
    String query = "UPDATE @table SET @params WHERE @id = ? RETURNING *"
        .replace("@params", params);
    int valueSize = values.size();
    PreparedStatement statement = createStatement(query);
    List<Object> objectValues = values.values().stream().toList();

    for (int i = 0; i < objectValues.size(); i++) {
      Object value = objectValues.get(i);
      if(value instanceof Enum<?>){
        statement.setObject(i+1, ((Enum<?>) value).name());
      }else {
        statement.setObject(i+1, value);
      }
    }
    statement.setObject(valueSize+1, id);
    return resultSetOptional(statement);
  }
}
