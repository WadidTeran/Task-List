package utils;

public class DataBaseConnection {
  private static DataBase database;

  private DataBaseConnection() {}

  public static DataBase getConnection() {
    return (database == null) ? new DataBase() : database;
  }
}
