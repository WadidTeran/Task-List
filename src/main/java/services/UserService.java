package services;

import models.User;
import utils.UserLogin;
import views.View;

public class UserService {
  private static final String NULL_MESSAGE_WARNING = "No invalid entries allowed. Please, try again";
  private final ICRUDService crudService;

  public UserService(ICRUDService crudService) {
    this.crudService = crudService;
  }

  public boolean signIn() {
    String email = View.input("Email");
    if (!email.isBlank()) email = email.toLowerCase();
    else {
      View.message(NULL_MESSAGE_WARNING);
      return false;
    }
    String password = View.input("Password");
    if (password.isBlank()) {
      View.message(NULL_MESSAGE_WARNING);
      return false;
    }

    if (!checkUserEmail(email)) {
      View.message("This user doesn't exist.");
    } else if (!validateUserPassword(email, password)) {
      View.message("Invalid password.");
    } else {
      User user = getUserByEmail(email);
      View.message("Welcome!");

      UserLogin.logInUser(user);
      return true;
    }
    return false;
  }

  public void signUp() {
    String email = View.input("Email");
    if (!email.isBlank()) email = email.toLowerCase();
    else {
      View.message(NULL_MESSAGE_WARNING);
      return;
    }
    String password = View.input("Password");
    if (password.isBlank()) {
      View.message(NULL_MESSAGE_WARNING);
      return;
    }

    if (!checkUserEmail(email)) {
      User user = new User(email, password);
      crudService.saveUser(user);
      View.message("User registered successfully!");
    } else {
      View.message("An user with this email already exists!");
    }
  }

  public boolean deleteUser() {
    String password = View.input("If you want to delete your account, insert your password.");
    if (password.isBlank()) {
      View.message(NULL_MESSAGE_WARNING);
      return false;
    }

    if (checkPassword(password)) {
      if (View.confirm("Are you sure you want to delete your account?")) {
        signOut();
        crudService.deleteUser(UserLogin.getLoggedUser());
        View.message("You have deleted your account succesfully...");
        return true;
      }
    } else {
      View.message("Authentication failed!");
    }
    return false;
  }

  public void signOut() {
    UserLogin.logOutUser();
  }

  public void changeEmail() {
    String password = View.input("If you want to change your email, insert your password.");
    
    if(password.equals("")){
      View.message(NULL_MESSAGE_WARNING);
      return;
    }
    if (checkPassword(password)) {
      String newEmail = View.input("Insert the new email.").toLowerCase();
      if(newEmail.equals("")){
        View.message(NULL_MESSAGE_WARNING);
        return;
      }
      if (checkUserEmail(newEmail)) {
        View.message("An user with this email already exists!");
      } else if (View.confirm(
          "Are you sure you want to change your email from "
              + UserLogin.getLoggedUser().getEmail()
              + " to "
              + newEmail
              + "?")) {
        User currentUser = UserLogin.getLoggedUser();
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
    if(password.isBlank()){
      View.message(NULL_MESSAGE_WARNING);
      return;
    }

    if (checkPassword(password)) {
      String newPassword = View.input("Insert the new password.");
      if(newPassword.equals("")){
        View.message(NULL_MESSAGE_WARNING);
        return;
      }
      String newPasswordConfirm = View.input("Confirm the new password.");
      if (newPassword.equals(newPasswordConfirm)) {
        if (View.confirm("Are you sure you want to change your password?")) {
          User currentUser = UserLogin.getLoggedUser();
          currentUser.setPassword(newPassword);
          crudService.saveUser(currentUser);
          View.message("Password changed successfully.");
        }
      } else {
        View.message("Password confirmation failed!");
      }
    } else {
      View.message("Incorrect password.");
    }
  }

  private boolean checkPassword(String password) {
    return UserLogin.getLoggedUser().getPassword().equals(password);
  }

  private boolean checkUserEmail(String email) {
    return crudService.findAllUsers().stream().anyMatch(u -> u.getEmail().equals(email));
  }

  private boolean validateUserPassword(String email, String password) {
    return getUserByEmail(email).getPassword().equals(password);
  }

  private User getUserByEmail(String email) {
    return crudService.findAllUsers().stream()
        .filter(u -> u.getEmail().equals(email))
        .findFirst()
        .orElseThrow();
  }
}
