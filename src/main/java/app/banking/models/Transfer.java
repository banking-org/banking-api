package app.banking.models;

import lombok.Data;
import postgres.addict.Column;
import postgres.addict.GenerativeValue;
import postgres.addict.Table;

import java.time.LocalDate;

@Table
@Data
public class Transfer {
  @Column(identity=true, generative= GenerativeValue.SEQUENCE)
  private Long id;

  @Column(references=true, required=true)
  private Account idDebited;

  @Column(references=true)
  private Account idCredited;

  @Column
  private LocalDate date;

  @Column
  private String ref;

  @Column String bank;
}
