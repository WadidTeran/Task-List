package org.kodigo.tasklist.models;

import lombok.Data;

@Data
public class Category {
  private Long categoryId;
  private String name;
  private User user;

  public Category(String name, User user) {
    this.name = name;
    this.user = user;
  }
}
