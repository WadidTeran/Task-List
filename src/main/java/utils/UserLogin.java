package utils;

import lombok.Getter;
import models.User;
import services.CRUDServiceImpl;
import views.View;

public class UserLogin {

  @Getter private static User user;

  private UserLogin() {}

  public static boolean logInUser(String email, String password) {
    CRUDServiceImpl crudService = new CRUDServiceImpl();
    if (crudService.checkUserEmail(email)) {
      if (crudService.validateUserPassword(email, password)) {
        user = crudService.getUserByEmail(email);
        return true;
      }
      View.display("Invalid password");
      return false;
    }

    View.display("This User doesn't exist.");
    return false;
  }

  public static void logOutUser() {
    user = null;
  }

  public static boolean isUserLogged() {
    return user != null;
  }
}
