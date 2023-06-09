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
    String email = View.input("Email");
    String password = View.input("Password");

    UserLogin.logInUser(email, password, crudService);
  }

  public void signUp() {
    String email = View.input("Email");
    String password = View.input("Password");

    if (!crudService.checkUserEmail(email)) {
      User user = new User(email, password);
      crudService.saveUser(user);
      View.message("User registered successfully!");
    } else {
      View.message("An user with this email already exists!");
    }
  }

  public void deleteUser() {
    String password = View.input("If you want to delete your account, insert your password.");

    if (checkPassword(password)) {
      if (View.confirm("Are you sure you want to delete your account?")) {
        signOut();
        crudService.deleteUser(UserLogin.getUser());
        View.message("You have deleted your account succesfully...");
      }
    } else {
      View.message("Authentication failed!");
    }
  }

  public void signOut() {
    UserLogin.logOutUser();
  }

  public void changeEmail() {
    String password = View.input("If you want to change your email, insert your password.");

    if (checkPassword(password)) {
      String newEmail = View.input("Insert the new email.");
      if (crudService.checkUserEmail(newEmail)) {
        View.message("An user with this email already exists!");
      } else if (View.confirm(
          "Are you sure you want to change your email from "
              + UserLogin.getUser().getEmail()
              + " to "
              + newEmail
              + "?")) {
        User currentUser = UserLogin.getUser();
        currentUser.setEmail(newEmail);
        crudService.saveUser(currentUser);
        View.message("Email changed successfully.");
      }
    } else {
      View.message("Incorrect password.");
    }
  }

  public void changePassword() {
    String password = View.input("Insert your current password.");

    if (checkPassword(password)) {
      String newPassword = View.input("Insert the new password.");
      if (View.confirm("Are you sure you want to change your password?")) {
        User currentUser = UserLogin.getUser();
        currentUser.setPassword(newPassword);
        crudService.saveUser(currentUser);
        View.message("Password changed successfully.");
      }
    } else {
      View.message("Incorrect password.");
    }
  }

  private boolean checkPassword(String password) {
    return UserLogin.getUser().getPassword().equals(password);
  }
}
