package utils;

import javax.swing.JOptionPane;
import lombok.Getter;
import models.User;
import services.CRUDServiceImpl;

public class UserLogin {

  @Getter private static User user;

  private UserLogin() {}

  public static void logInUser(String email, String password, CRUDServiceImpl crudService) {
    if (!crudService.checkUserEmail(email)) {
      JOptionPane.showMessageDialog(null, "This user doesn't exist.");
    } else if (!crudService.validateUserPassword(email, password)) {
      JOptionPane.showMessageDialog(null, "Invalid password");
    } else {
      user = crudService.getUserByEmail(email);
      JOptionPane.showMessageDialog(null, "Welcome!");
    }
  }

  public static void logOutUser() {
    user = null;
  }

  public static boolean isUserLogged() {
    return user != null;
  }
}
