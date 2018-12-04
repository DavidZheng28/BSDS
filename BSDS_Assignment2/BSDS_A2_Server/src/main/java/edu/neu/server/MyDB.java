package edu.neu.server;

import edu.neu.server.model.Record;
import java.sql.*;

public class MyDB {

  private static MyDB db = new MyDB();

  public void insertData(Record record) throws SQLException {
    String insertStepCounts = "INSERT INTO StepData (UserId, recorddate, TimeInterval, StepCount) VALUES (?, ?, ?, ?);";
    Connection connection = null;
    PreparedStatement insertStmt = null;
    try {
      connection = ConnectionManager.getConnection();
      insertStmt = connection.prepareStatement(insertStepCounts);
      insertStmt.setInt(1, record.getUserID());
      insertStmt.setInt(2, record.getDay());
      insertStmt.setInt(3, record.getHour());
      insertStmt.setInt(4, record.getStepCount());
      insertStmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
      if (insertStmt != null) {
        insertStmt.close();
      }
    }
  }

  public int getSingleDay(int user_id, int day) throws SQLException {
    String selectStepCounts = "SELECT SUM(StepCount) FROM StepData WHERE UserId=? AND recorddate=?;";

    Connection connection = null;
    PreparedStatement selectStmt = null;
    ResultSet results = null;
    int sum = 0;
    try {
      connection = ConnectionManager.getConnection();
      selectStmt = connection.prepareStatement(selectStepCounts);
      selectStmt.setInt(1, user_id);
      selectStmt.setInt(2, day);
      results = selectStmt.executeQuery();
      while (results.next()) {
        sum = results.getInt("SUM(StepCount)");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
      if (selectStmt != null) {
        selectStmt.close();
      }
      if (results != null) {
        results.close();
      }
    }
    return sum;
  }

  public int getCurrentDay(int userId) throws SQLException {
    String selectStepCounts = "SELECT SUM(StepCount), recorddate FROM StepData " +
        "WHERE UserId=? " +
        "GROUP BY recorddate " +
        "ORDER BY recorddate DESC " +
        "LIMIT 1;";
    Connection connection = null;
    PreparedStatement selectStmt = null;
    ResultSet results = null;
    int sum = 0;
    try {
      connection = ConnectionManager.getConnection();
      selectStmt = connection.prepareStatement(selectStepCounts);
      selectStmt.setInt(1, userId);
      results = selectStmt.executeQuery();
      while (results.next()) {
        sum = results.getInt("SUM(StepCount)");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
      if (selectStmt != null) {
        selectStmt.close();
      }
      if (results != null) {
        results.close();
      }
    }
    return sum;
  }

  public int[] getIntervalDays(int userId, int startDay, int numOfDay) throws SQLException {
    String selectStepCounts = "SELECT SUM(StepCount), recorddate FROM StepData  " +
        "WHERE UserId=? AND recorddate>=? AND recorddate<=?" +
        "GROUP BY recorddate;";
    Connection connection = null;
    PreparedStatement selectStmt = null;
    ResultSet results = null;
    int[] steps = new int[numOfDay];
    try {
      connection = ConnectionManager.getConnection();
      selectStmt = connection.prepareStatement(selectStepCounts);
      selectStmt.setInt(1, userId);
      selectStmt.setInt(2, startDay);
      selectStmt.setInt(3, startDay + numOfDay - 1);
      results = selectStmt.executeQuery();
      while (results.next()) {
        int curDay = results.getInt("recorddate");
        steps[curDay - startDay] = results.getInt("SUM(StepCount)");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
      if (selectStmt != null) {
        selectStmt.close();
      }
      if (results != null) {
        results.close();
      }
    }
    return steps;
  }

  public static MyDB getDb() {
    if (db == null) {
      db = new MyDB();
    }
    return db;
  }
}
