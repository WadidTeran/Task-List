package controllers;

import models.User;
import services.CRUDServiceImpl;
import utils.UserLogin;
import views.View;

import javax.swing.JOptionPane;

public class UserController {
  private final CRUDServiceImpl crudService;

  public UserController(CRUDServiceImpl crudService) {
    this.crudService = crudService;
  }

  public void signIn() {
    String email = JOptionPane.showInputDialog("Email: ");
    String password = JOptionPane.showInputDialog("Password: ");

    UserLogin.logInUser(email, password, crudService);
  }

  public void signUp() {
    String email = JOptionPane.showInputDialog("Email: ");
    String password = JOptionPane.showInputDialog("Password: ");

    if (!crudService.checkUserEmail(email)) {
      User user = new User(email, password);
      crudService.saveUser(user);
      View.display("User registered successfully!");
    } else {
      View.display("An user with this email already exists!");
    }
  }

  public void deleteUser() {
    String password = JOptionPane.showInputDialog("If you want to delete your account, insert your password: ");

    if (UserLogin.getUser().getPassword().equals(password)) {
      signOut();
      crudService.deleteUser(UserLogin.getUser());
      View.display("You have deleted your account succesfully...");
    } else {
      View.display("Authentication failed!");
    }
  }

  public void signOut() {
    UserLogin.logOutUser();
  }
}
