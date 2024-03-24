package app.banking.config;

import app.banking.models.Category;
import app.banking.models.TransactionType;
import app.banking.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static app.banking.models.TransactionType.CREDIT;
import static app.banking.models.TransactionType.DEBIT;

@Component
@Slf4j
@RequiredArgsConstructor
public class MockSeed {
  private final CategoryRepository repository;

  @PostConstruct
  public void seed(){
    Object[][] list = new Object[][]{
      { "Salary", CREDIT, "money from your boss" },
      { "Deposits", CREDIT, "your deposition from ATM" },
      { "Investments", CREDIT, "money from funders" },
      { "Other", CREDIT, "other category incomes" },

      { "Food & Drinks", DEBIT, "Your daily foods & drinks activities" },
      { "Electronics", DEBIT, "your electronics spends" },
      { "Shopping", DEBIT, "All your shop activities" },
      { "Housing", DEBIT, "Your housing spends" },
      { "Vehicle & Pieces", DEBIT, "All about your vehicles" },
      { "Other", DEBIT, "Other category outcomes" },
    };

    for (Object[] item : list) {
      createOrNot(item);
    }
    log.info("DB seed done !");
  }

  private void createOrNot(Object[] items){
    String name = (String) items[0];
    TransactionType type = (TransactionType) items[1];
    String label = (String) items[2];

    Category category = repository.findByNameAndType(name, type);
    if(category == null){
      repository.save(
        Category
          .builder()
          .name(name)
          .onlyOn(type)
          .comments(label)
          .build()
      );
    }
  }
}
