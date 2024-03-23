package app.banking.repository;

import app.banking.models.Account;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository extends CommonCrud<Account, Long> {}
