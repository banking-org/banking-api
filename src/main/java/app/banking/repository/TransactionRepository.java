package app.banking.repository;

import app.banking.DTO.StateItem;
import app.banking.models.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import postgres.addict.Queries;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static app.banking.models.TransactionStatus.NO_PAYED;

@Repository
public class TransactionRepository extends CommonCrud<Transaction, Long> {
  @SneakyThrows
  public List<Transaction> findAllById(Long id){
    return listFromQueries(Queries
      .selectDistinct()
      .innerJoin(Transaction.class, Account.class, "id_account")
      .leftJoin(Transaction.class, Account.class, "receiver")
      .leftJoin(Transaction.class, Category.class)
      .leftJoin(Transaction.class, TransactionGroup.class)
      .where()
        .equals("\"@table\".id_account", id)
      .end()
    );
  }

  @SneakyThrows
  public Double findSumOfNoPayedByAccountId(Long id){
    Queries queries = Queries
      .select(Queries
        .aliasColumns()
        .add("sum(amount)", "total")
        .toQuery()
      )
      .where()
      .equals("id_account", id)
      .and()
      .equals("status", NO_PAYED)
      .end();
    try (ResultSet resultSet = executeQueries(queries)) {
      if (resultSet.next()) {
        return resultSet.getDouble("total");
      }
    }
    return 0D;
  }

  @SneakyThrows
  public Double findSumOfNoPayedByAccountIdAtDate(Long id, LocalDate date){
    Queries query = Queries
      .select(Queries
        .aliasColumns()
          .add("sum(amount)", "total")
        .toQuery()
      )
      .where()
        .equals("id_account", id)
        .and()
        .equals("status", NO_PAYED)
        .and()
        .equals("created_at", date)
      .end();
    try (ResultSet resultSet = executeQueries(query)){
      if (resultSet.next()) {
        return resultSet.getDouble("total");
      }
    }
    return 0D;
  }

  @SneakyThrows
  public Optional<Transaction> findLastNotPaidByAccountId(Long id){
    return optionalFromQueries(Queries
      .select()
      .where()
        .equals("id_account", id)
        .and()
        .equals("status", NO_PAYED)
      .end()
        .orderBy()
        .addDesc("created_at")
      .end()
      .limit(1)
    );
  }

  @SneakyThrows
  public List<StateItem> findAllByIntervalAndAccountId(
    Long accountId,
    LocalDate start,
    LocalDate end
  ){
    Queries query = Queries
      .select(
        Queries
        .aliasColumns()
          .add("effect_date", "date")
          .add("concat('VIR_', upper(\"effect_date\"::text))", "reference")
          .noAlias("label")
          .add("sum(CASE type WHEN 'CREDIT' THEN abs(amount) ELSE 0 END)", "credit")
          .add("sum(CASE type WHEN 'DEBIT' THEN abs(amount) ELSE 0 END)", "debit")
          .add("sum(current_balance)", "balance")
        .toQuery()
      )
      .leftJoin(Transaction.class, AccountBalance.class, "id_account")
      .where()
        .equals("\"transaction\".id_account", accountId)
        .and()
        .between("created_at", start, end)
      .end()
      .groupBy("label", "current_balance", "effect_date")
    ;
    List<StateItem> items = new ArrayList<>();
    try(ResultSet resultSet = executeQueries(query)){
      while (resultSet.next()) {
        StateItem item = new StateItem();
        item.setDate(resultSet.getDate("date").toLocalDate());
        item.setReference(resultSet.getString("reference"));
        item.setLabel(resultSet.getString("label"));
        item.setCredit(resultSet.getDouble("credit"));
        item.setDebit(resultSet.getDouble("debit"));
        item.setBalance(resultSet.getDouble("balance"));
        items.add(item);
      }
    }
    return items;
  }
}
