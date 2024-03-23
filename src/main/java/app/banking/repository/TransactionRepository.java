package app.banking.repository;

import app.banking.DTO.StateItem;
import app.banking.models.Transaction;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository extends CommonCrud<Transaction, Long> {
  @SneakyThrows
  public List<Transaction> findAllById(Long id){
    PreparedStatement statement = createStatement("SELECT * FROM @table WHERE @id = ?");
    statement.setObject(1, id);
    return resultSetList(statement);
  }

  @SneakyThrows
  public Double findSumOfNoPayedByAccountId(Long id){
    String sql = "SELECT sum(amount) as total FROM @table WHERE id_account = ? AND status = 'NO_PAYED'";
    try(PreparedStatement statement = createStatement(sql)){
      statement.setObject(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getDouble("total");
      }
    }
    return 0D;
  }

  @SneakyThrows
  public Double findSumOfNoPayedByAccountIdAtDate(Long id, LocalDate date){
    String sql = "SELECT sum(amount) as total FROM @table WHERE id_account = ? AND status = 'NO_PAYED' AND created_at = ?";
    try(PreparedStatement statement = createStatement(sql)){
      statement.setObject(1, id);
      statement.setObject(2, date);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getDouble("total");
      }
    }
    return 0D;
  }

  @SneakyThrows
  public Optional<Transaction> findLastNotPaidByAccountId(Long id){
    PreparedStatement statement = createStatement(
      "SELECT * FROM @table WHERE id_account = ? AND status = 'NO_PAYED' ORDER BY created_at DESC LIMIT 1"
    );
    statement.setObject(1, id);
    return resultSetOptional(statement);
  }

  @SneakyThrows
  public List<StateItem> findAllByIntervalAndAccountId(
    Long accountId,
    LocalDate start,
    LocalDate end
  ){
    List<StateItem> items = new ArrayList<>();
    String sql = """
      SELECT
          "effect_date" as date,
          concat('VIR_', upper("effect_date"::text)) as reference,
          label,
          sum(CASE type WHEN 'CREDIT' THEN abs(amount) ELSE 0 END) as credit,
          sum(CASE type WHEN 'DEBIT' THEN abs(amount) ELSE 0 END) as debit,
          sum("current_balance") as balance
      FROM "transaction"
      left join public.account_balance ab on transaction."id_account" = ab."id_account"
      WHERE "transaction"."id_account" = ? AND "created_at" BETWEEN ? AND ?
      group by label, "current_balance", "effect_date"
      """;

    try(PreparedStatement statement = createStatement(sql)){
      statement.setObject(1, accountId);
      statement.setObject(2, start);
      statement.setObject(3, end);

      ResultSet resultSet = statement.executeQuery();
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
