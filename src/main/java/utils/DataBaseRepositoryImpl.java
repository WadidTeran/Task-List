package utils;

public abstract class DataBaseRepositoryImpl {
  DataBase connection;

  public void setConnection(DataBase connection) {
    this.connection = connection;
  }
}
