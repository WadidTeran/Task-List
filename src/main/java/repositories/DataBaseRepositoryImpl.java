package repositories;

import lombok.Getter;
import utils.DataBase;

@Getter
public abstract class DataBaseRepositoryImpl {
  DataBase connection;

  public void setConnection(DataBase connection) {
    this.connection = connection;
  }
}
