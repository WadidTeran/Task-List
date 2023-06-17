package org.kodigo.tasklist.utils;

public class DataBaseConnection {
  private static DataBase database;

  private DataBaseConnection() {}

  public static DataBase getConnection() {
    if (database == null) {
      database = new DataBase();
    }
    return database;
  }
}
