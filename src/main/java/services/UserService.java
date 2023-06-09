package services;

import models.User;
import utils.UserLogin;
import views.View;

public class UserService {
  private final CRUDServiceImpl crudService;

  public UserService(CRUDServiceImpl crudService) {
    this.crudService = crudService;
  }

  public void signIn() {
    String email = View.input("Email: ");
    String password = View.input("Password: ");

    UserLogin.logInUser(email, password, crudService);
  }

  public void signUp() {
    String email = View.input("Email: ");
    String password = View.input("Password: ");

    if (!crudService.checkUserEmail(email)) {
      User user = new User(email, password);
      crudService.saveUser(user);
      View.message("User registered successfully!");
    } else {
      View.message("An user with this email already exists!");
    }
  }

  public void deleteUser() {
    String password = View.input("If you want to delete your account, insert your password: ");

    if (UserLogin.getUser().getPassword().equals(password)
        && View.confirm("Are you sure you want to delete your account?")) {
      signOut();
      crudService.deleteUser(UserLogin.getUser());
      View.message("You have deleted your account succesfully...");
    } else {
      View.message("Authentication failed!");
    }
  }

  public void signOut() {
    UserLogin.logOutUser();
  }
}
