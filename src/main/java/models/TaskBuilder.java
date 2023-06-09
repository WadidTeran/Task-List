package models;

import java.time.LocalDate;
import java.time.LocalTime;
import utils.UserLogin;

public class TaskBuilder {
  private Task task;

  public TaskBuilder() {
    task = new Task();
    task.setUser(UserLogin.getUser());
    task.setRelevance(Relevance.NONE);
    task.setCompleted(false);
    task.setDescription("");
  }

  public TaskBuilder(String name) {
    this();
    task.setName(name);
  }

  public TaskBuilder(Task task) {
    this.task = task;
  }

  public static TaskBuilder taskBuilderWithClonedTask(Task task) {
    TaskBuilder taskBuilder = new TaskBuilder(task.getName());

    return taskBuilder
        .setDescription(task.getDescription())
        .setRelevance(task.getRelevance())
        .setCategory(task.getCategory())
        .setRepeatingConfig(task.getRepeatingConfig());
  }

  public TaskBuilder setName(String name) {
    this.task.setName(name);
    return this;
  }

  public TaskBuilder setCompleted(Boolean completed) {
    this.task.setCompleted(completed);
    return this;
  }

  public TaskBuilder setCompletedDate(LocalDate completedDate) {
    this.task.setCompletedDate(completedDate);
    return this;
  }

  public TaskBuilder setDueDate(LocalDate dueDate) {
    this.task.setDueDate(dueDate);
    return this;
  }

  public TaskBuilder setSpecifiedTime(LocalTime specifiedTime) {
    this.task.setSpecifiedTime(specifiedTime);
    return this;
  }

  public TaskBuilder setDescription(String description) {
    this.task.setDescription(description);
    return this;
  }

  public TaskBuilder setRelevance(Relevance relevance) {
    this.task.setRelevance(relevance);
    return this;
  }

  public TaskBuilder setCategory(Category category) {
    this.task.setCategory(category);
    return this;
  }

  public TaskBuilder setRepeatingConfig(RepeatTaskConfig repeatTaskConfig) {
    this.task.setRepeatingConfig(repeatTaskConfig);
    return this;
  }

  public void reset() {
    this.task = new Task();
  }

  public Task build() {
    return this.task;
  }
}
