package models;

import utils.UserLogin;

public class TaskBuilder {
  private Task task;

  public TaskBuilder(Task task) {
    this.task = task;
    this.task.setUser(UserLogin.getUser());
  }

  public TaskBuilder(String name) {
    this.task = new Task();
    this.task.setName(name);
    this.task.setUser(UserLogin.getUser());
  }

  public void reset() {
    this.task = new Task();
  }

  public Task build() {
    return this.task;
  }
}
