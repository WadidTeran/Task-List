package org.kodigo.tasklist.models;

import lombok.Data;

@Data
public class User {
  private Long userId;
  private String email;
  private String password;

  public User(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
