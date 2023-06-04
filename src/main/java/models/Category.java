package models;

import lombok.Data;

@Data
public class Category {
  private Long categoryId;
  private String name;
  private User user;
}
