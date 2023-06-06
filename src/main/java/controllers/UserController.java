package controllers;

import java.util.Scanner;
import models.User;
import services.CRUDServiceImpl;
import utils.UserLogin;
import views.View;

public class UserController {
  private final Scanner scanner = new Scanner(System.in);

  public void signIn() {
    View.display("Email: ");
    String email = scanner.nextLine();

    View.display("Password: ");
    String password = scanner.nextLine();

    if (UserLogin.logInUser(email, password)) View.display("Welcome!");
  }

  public void signUp() {
    View.display("Email: ");
    String email = scanner.nextLine();

    View.display("Password: ");
    String password = scanner.nextLine();

    CRUDServiceImpl crudService = new CRUDServiceImpl();

    if (!crudService.checkUserEmail(email)) {
      User user = new User(email, password);
      crudService.saveUser(user);
      View.display("User registered successfully!");
    } else {
      View.display("An user with this email already exists!");
    }
  }

  public void deleteUser() {}

  public void signOut() {}
}
