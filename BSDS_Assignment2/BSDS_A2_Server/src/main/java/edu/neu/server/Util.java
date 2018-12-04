package edu.neu.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Util {

  public static void resetDB() throws SQLException {
    Connection connection = null;
    PreparedStatement deleteStmt = null;
    try {
      connection = ConnectionManager.getConnection();
      deleteStmt = connection.prepareStatement("TRUNCATE TABLE StepData;");
      deleteStmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
      if (deleteStmt != null) {
        deleteStmt.close();
      }
    }
  }
}
