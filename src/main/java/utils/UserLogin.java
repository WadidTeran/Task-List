package utils;

import lombok.Getter;
import models.User;
import services.CRUDServiceImpl;
import views.View;

public class UserLogin {

  @Getter private static User user;

  private UserLogin() {}

  public static void logInUser(String email, String password, CRUDServiceImpl crudService) {
    if (!crudService.checkUserEmail(email)) {
      View.message("This user doesn't exist.");
    } else if (!crudService.validateUserPassword(email, password)) {
      View.message("Invalid password");
    } else {
      user = crudService.getUserByEmail(email);
      View.message("Welcome!");
    }
  }

  public static void logOutUser() {
    user = null;
  }

  public static boolean isUserLogged() {
    return user != null;
  }
}
