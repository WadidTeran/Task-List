package repositories;

import java.util.List;
import java.util.stream.Collectors;

import models.Category;
import utils.DataBaseConnection;
import utils.UserLogin;

public class CategoryRepository extends DataBaseRepositoryImpl implements IRepository<Category> {

  public CategoryRepository() {
    setConnection(DataBaseConnection.getConnection());
  }

  @Override
  public List<Category> findAll() {
    return this.connection.getCategories().stream()
        .filter(c -> c.getUser().equals(UserLogin.getLoggedUser()))
        .collect(Collectors.toList());
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
    if (category.getUser().equals(UserLogin.getLoggedUser())) {
      this.connection.deleteCategory(category);
    }
  }
}
