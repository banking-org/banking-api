package app.banking.models;

import postgres.addict.SqlEnumType;

@SqlEnumType
public enum AccountType {
  NORMAL,
  DEBIT
}
