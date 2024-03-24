package app.banking.controllers;

import app.banking.models.Category;
import app.banking.models.TransactionType;
import app.banking.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryRepository repo;

  @GetMapping("/categories")
  private List<Category> getCategoriesByType(
    @RequestParam TransactionType type
    ){
    return repo.findAllByType(type);
  }
}
