package edu.neu.server.model;

public class Record {

  private int userID;
  private int day;
  private int hour;
  private int stepCount;

  public Record(int userID, int day, int hour, int stepCount) {
    this.userID = userID;
    this.day = day;
    this.hour = hour;
    this.stepCount = stepCount;
  }

  public int getUserID() {
    return userID;
  }

  public int getDay() {
    return day;
  }

  public int getHour() {
    return hour;
  }

  public int getStepCount() {
    return stepCount;
  }

  @Override
  public String toString() {
    return "Record{" + "userID=" + userID + ", day=" + day
        + ", hour=" + hour + ", stepCount=" + stepCount + '}';
  }
}
