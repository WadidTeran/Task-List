package models;

import lombok.Data;

@Data
public class User {
  private Long userId;
  private String email;
  private String password;
}
