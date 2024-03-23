package app.banking.repository;

import app.banking.models.TransactionGroup;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionGroupRepo extends CommonCrud<TransactionGroup, Long> {}
