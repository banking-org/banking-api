package app.banking.models;

public enum TransactionStatus {
  PENDING,
  SUCCESS,
  CANCELED,
  NO_PAYED, // for negative amount not paid
  PAYED // for negative amount paid
}
