package repositories;

public class DataBaseConnetion {
  private DataBase database;

  private DataBaseConnetion() {};

  public DataBase getConnection() {
    if (database == null) {
      database = new DataBase();
    }
    return database;
  }
}
