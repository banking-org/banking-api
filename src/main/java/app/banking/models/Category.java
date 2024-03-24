package app.banking.models;

import lombok.*;
import postgres.addict.Column;
import postgres.addict.ColumnType;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table
public class Category {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column(required=true, unique=true)
  private String name;

  @Column(columnType=ColumnType.TEXT)
  private String comments;

  @Column(name="only_on", required=true)
  private TransactionType onlyOn;
}
