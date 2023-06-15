package services;

import models.User;
import utils.CredentialsValidator;
import utils.UserLogin;
import views.View;

public class UserService {
  private static final String EMPTY_MESSAGE_WARNING = "No empty entries allowed. Please, try again";
  private final CRUDService crudService;

  public UserService(CRUDService crudService) {
    this.crudService = crudService;
  }

  public boolean signIn() {
    String email = View.input("Email");
    if (email == null) return false;
    else if (email.isBlank() || email.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return false;
    }
    email = email.toLowerCase().trim();
    if (!checkUserEmail(email)) {
      View.message("This user doesn't exist.");
      return false;
    }

    String password = View.input("Password");
    if (password == null) return false;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return false;
    }
    password = password.trim();

    if (!validateUserPassword(email, password)) {
      View.message("Invalid password.");
      return false;
    } else {
      User user = getUserByEmail(email);
      UserLogin.logInUser(user);
      View.message("Welcome!");
      return true;
    }
  }

  public void signUp() {
    String email = View.input("Email");
    if (email == null) return;
    else if (email.isBlank() || email.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    email = email.toLowerCase().trim();
    if (checkUserEmail(email)) {
      View.message("An user with this email already exists!");
      return;
    } else if (!CredentialsValidator.isValidEmail(email)) {
      View.message("Not a valid email address!");
      return;
    }

    String password = View.input("Password");
    if (password == null) return;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    password = password.trim();
    if (!CredentialsValidator.isValidPassword(password)) {
      View.message(CredentialsValidator.PASSWORD_MESSAGE);
      return;
    }

    User user = new User(email, password);
    crudService.saveUser(user);
    View.message("User registered successfully!");
  }

  public boolean deleteUser() {
    String password = View.input("If you want to delete your account, insert your password.");
    if (password == null) return false;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return false;
    }
    password = password.trim();

    if (checkPassword(password)) {
      if (View.confirm("Are you sure you want to delete your account?")) {
        crudService.deleteUser(UserLogin.getLoggedUser());
        signOut();
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
    if (password == null) return;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    password = password.trim();

    if (checkPassword(password)) {
      String newEmail = View.input("Insert the new email.");
      if (newEmail == null) return;
      else if (newEmail.isBlank() || newEmail.isEmpty()) {
        View.message(EMPTY_MESSAGE_WARNING);
        return;
      }
      newEmail = newEmail.toLowerCase().trim();

      if (checkUserEmail(newEmail)) {
        View.message("An user with this email already exists!");
      } else if (!CredentialsValidator.isValidEmail(newEmail)) {
        View.message("Not a valid email address!");
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
    if (password == null) return;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    password = password.trim();

    if (checkPassword(password)) {
      String newPassword = View.input("Insert the new password.");
      if (newPassword == null) return;
      else if (newPassword.isBlank() || newPassword.isEmpty()) {
        View.message(EMPTY_MESSAGE_WARNING);
        return;
      }
      newPassword = newPassword.trim();

      if (!CredentialsValidator.isValidPassword(newPassword)) {
        View.message(CredentialsValidator.PASSWORD_MESSAGE);
        return;
      }

      String newPasswordConfirm = View.input("Confirm the new password.");
      if (newPasswordConfirm == null) return;
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
