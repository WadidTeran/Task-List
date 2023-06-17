package org.kodigo.tasklist.utils;

public enum Warnings {
  PASSWORD_CONDITIONS(
      """
 The password must meet the following conditions:
           1. Minimum length: 8
           2. One uppercase
           3. One lowercase
           4. One special character
           5. One number
      """),
  EMPTY_INPUT("No empty entries allowed."),
  NON_EXISTING_USER("This user doesn't exist."),
  EXISTING_USER("An user with this email already exists!"),
  INCORRECT_PASSWORD("Incorrect password."),
  NOT_VALID_EMAIL("Not a valid email address!"),
  NO_EXISTING_CATEGORIES("You don't have any categories created yet."),
  EXISTING_CATEGORY("This category already exists!"),
  NO_PENDING_TASKS("You don't have any pending tasks."),
  NO_COMPLETED_TASKS("You don't have any completed tasks."),
  NOT_A_NUMBER("That is not a number."),
  NOT_VALID_TASK_ID("Not a valid task id."),
  INVALID_FORMAT("Invalid format."),
  SET_DUE_DATE_FIRST("You have to set a due date first!"),
  SET_A_REPEAT_TYPE_FIRST("You have to set a repeat type first!");

  final String warning;

  Warnings(String warning) {
    this.warning = warning;
  }

  @Override
  public String toString() {
    return warning;
  }
}
