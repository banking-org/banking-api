package postgres.addict;

import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

public class CommonRepository<T, R> extends AddictRepository<T> {
  public Optional<T> save(T value){
    Queries.Insertion insertion = Queries.insertion();
    getInsertedValues(value, insertion::value);
    Queries queries = insertion.end().returns();
    return optionalFromQueries(queries);
  }

  @SneakyThrows
  public Optional<T> findById(R id){
    return optionalFromQueries(Queries
      .select()
      .where()
        .equals(idColumnName, id)
      .end()
    );
  }

  public List<T> findAll(){
    return listFromQueries(Queries.select());
  }

  @SneakyThrows
  public Optional<T> updateById(R id, T newValue){
    Queries.Updates updates = Queries.update();
    getInsertedValues(newValue, updates::setUpdate);
    Queries query = updates
      .end()
      .where()
        .equals(idColumnName, id)
      .end()
      .returns();
    return optionalFromQueries(query);
  }

  @SneakyThrows
  public Optional<T> deleteById(R id){
    return optionalFromQueries(Queries
      .delete()
      .where()
        .equals(idColumnName, id)
      .end()
      .returns()
    );
  }
}
