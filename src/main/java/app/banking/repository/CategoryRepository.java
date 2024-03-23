package app.banking.repository;

import app.banking.models.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository extends CommonCrud<Category, Long> {}
