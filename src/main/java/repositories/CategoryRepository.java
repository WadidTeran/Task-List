package repositories;

import java.util.ArrayList;
import java.util.Optional;
import models.Category;

public class CategoryRepository extends DataBaseRepositoryImpl implements IRepository<Category> {

  @Override
  public ArrayList<Category> findAll() {
    return this.connection.getCategories();
  }

  @Override
  public Optional<Category> getById(Long id) {
    return findAll().stream().filter(c -> c.getCategoryId().equals(id)).findFirst();
  }

  @Override
  public Category save(Category category) {
    if (category.getCategoryId() != null && category.getCategoryId() > 0L) {
      this.connection.updateCategory(category);
    } else {
      this.connection.insertCategory(category);
    }

    return category;
  }

  @Override
  public void delete(Category category) {
    this.connection.deleteCategory(category);
  }
}
