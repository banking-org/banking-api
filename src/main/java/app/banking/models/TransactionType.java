package app.banking.models;

import postgres.addict.SqlEnumType;

@SqlEnumType
public enum TransactionType {
  DEBIT,
  CREDIT,
  LOCAL_TRANSFER,
  EXTERNAL_TRANSFER
}
