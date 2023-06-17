package org.kodigo.tasklist.repositories;

import org.kodigo.tasklist.utils.DataBase;

public abstract class DataBaseRepositoryImpl {
  protected DataBase connection;

  public void setConnection(DataBase db) {
    this.connection = db;
  }
}
