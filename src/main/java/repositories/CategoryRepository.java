package repositories;

import java.util.ArrayList;
import java.util.Optional;
import lombok.Data;
import models.Category;

@Data
public class CategoryRepository extends DataBaseRepositoryImpl implements IRepository<Category> {

  @Override
  public ArrayList<Category> findAll() {
    return this.connection.getCategories();
  }

  @Override
  public Optional<Category> getById(Long id) {
    Optional<Category> optionalCategory =
        findAll().stream().filter(p -> p.getCategoryId().equals(id)).findFirst();
    return optionalCategory;
  }

  @Override
  public Category save(Category category) {
    this.connection.insertCategory(category);
  }

  @Override
  public void delete(Category category) {
    this.connection.deleteCategory(category);
  }
}
