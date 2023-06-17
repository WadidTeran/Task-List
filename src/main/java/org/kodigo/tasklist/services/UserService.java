package org.kodigo.tasklist.services;

import org.kodigo.tasklist.models.User;
import org.kodigo.tasklist.utils.CredentialsValidator;
import org.kodigo.tasklist.utils.UserLogin;
import org.kodigo.tasklist.utils.Warnings;
import org.kodigo.tasklist.views.View;

public class UserService {
  private final CRUDService crudService;

  public UserService(CRUDService crudService) {
    this.crudService = crudService;
  }

  public boolean signIn() {
    String email = View.input("Email");
    if (email == null) return false;
    else if (email.isBlank() || email.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return false;
    }
    email = email.toLowerCase().trim();
    if (!checkUserEmail(email)) {
      View.message(Warnings.NON_EXISTING_USER);
      return false;
    }

    String password = View.input("Password");
    if (password == null) return false;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return false;
    }
    password = password.trim();

    if (!validateUserPassword(email, password)) {
      View.message(Warnings.INCORRECT_PASSWORD);
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
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    email = email.toLowerCase().trim();
    if (checkUserEmail(email)) {
      View.message(Warnings.EXISTING_USER);
      return;
    } else if (!CredentialsValidator.isValidEmail(email)) {
      View.message(Warnings.NOT_VALID_EMAIL);
      return;
    }

    String password = View.input("Password");
    if (password == null) return;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    password = password.trim();
    if (!CredentialsValidator.isValidPassword(password)) {
      View.message(Warnings.PASSWORD_CONDITIONS);
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
      View.message(Warnings.EMPTY_INPUT);
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
      View.message(Warnings.INCORRECT_PASSWORD);
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
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    password = password.trim();

    if (checkPassword(password)) {
      String newEmail = View.input("Insert the new email.");
      if (newEmail == null) return;
      else if (newEmail.isBlank() || newEmail.isEmpty()) {
        View.message(Warnings.EMPTY_INPUT);
        return;
      }
      newEmail = newEmail.toLowerCase().trim();

      if (checkUserEmail(newEmail)) {
        View.message(Warnings.EXISTING_USER);
      } else if (!CredentialsValidator.isValidEmail(newEmail)) {
        View.message(Warnings.NOT_VALID_EMAIL);
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
      View.message(Warnings.INCORRECT_PASSWORD);
    }
  }

  public void changePassword() {
    String password = View.input("Insert your current password.");
    if (password == null) return;
    else if (password.isBlank() || password.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    password = password.trim();

    if (checkPassword(password)) {
      String newPassword = View.input("Insert the new password.");
      if (newPassword == null) return;
      else if (newPassword.isBlank() || newPassword.isEmpty()) {
        View.message(Warnings.EMPTY_INPUT);
        return;
      }
      newPassword = newPassword.trim();

      if (!CredentialsValidator.isValidPassword(newPassword)) {
        View.message(Warnings.PASSWORD_CONDITIONS);
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
      View.message(Warnings.INCORRECT_PASSWORD);
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
