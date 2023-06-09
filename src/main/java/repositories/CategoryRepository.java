package repositories;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import models.Category;
import utils.DataBaseConnection;
import utils.UserLogin;

public class CategoryRepository extends DataBaseRepositoryImpl implements IRepository<Category> {

  public CategoryRepository() {
    setConnection(DataBaseConnection.getConnection());
  }

  @Override
  public ArrayList<Category> findAll() {
    return (ArrayList<Category>)
        this.connection.getCategories().stream()
            .filter(c -> c.getUser().equals(UserLogin.getUser()))
            .collect(Collectors.toList());
  }

  @Override
  public Optional<Category> getById(Long id) {
    return findAll().stream()
        .filter(c -> c.getCategoryId().equals(id) && c.getUser().equals(UserLogin.getUser()))
        .findFirst();
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
    if (category.getUser().equals(UserLogin.getUser())) {
      this.connection.deleteCategory(category);
    }
  }
}
