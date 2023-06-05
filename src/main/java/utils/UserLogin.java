package utils;

import lombok.Getter;
import models.User;
import services.CRUDServiceImpl;

public class UserLogin {

  @Getter private User user;

  public boolean logInUser(String email, String password) {

    CRUDServiceImpl crudService = new CRUDServiceImpl();
    if (crudService.checkUserEmail(email)) {
      if (crudService.validateUserPassword(email, password)) {
        this.user = crudService.getUserByEmail(email);
        return true;
      }
      // View.display("Invalid password");
      return false;
    }

    // View.display("This User doesn't exist.");
    return false;
  }

  public void logOutUser() {
    this.user = null;
  }

  public boolean isUserLogged() {
    return this.user != null;
  }
}
