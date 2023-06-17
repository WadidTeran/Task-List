package utils;

import models.User;

public class UserLogin {

  private static User loggedUser;

  private UserLogin() {}

  public static void logInUser(User user) {
    loggedUser = user;
  }

  public static void logOutUser() {
    loggedUser = null;
  }

  public static User getLoggedUser() {
    return loggedUser;
  }
}
