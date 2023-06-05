package repositories;

import utils.DataBase;


public abstract class DataBaseRepositoryImpl {
  protected DataBase connection;

  public void setConnection(DataBase db) {
    this.connection = db;
  }
}
