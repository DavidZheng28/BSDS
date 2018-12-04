package edu.neu.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionManager {

  private static ComboPooledDataSource DataSource = new ComboPooledDataSource();

  private final static String HOSTNAME = "davidzhengdb.cclcto4wmlup.us-west-2.rds.amazonaws.com";
  private final static int PORT = 3306;
  private final static int MINPOOL = 1;
  private final static int MAXPOOL = 100;
  private final static String USER = "davidzheng";
  private final static String MYPASSWORD = "davidzhengdb";
  private final static String PATH = "StepApp";

  static {
    try {
      DataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }
    DataSource.setJdbcUrl("jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + PATH);
    DataSource.setUser(USER);
    DataSource.setPassword(MYPASSWORD);
    DataSource.setMinPoolSize(MINPOOL);
    DataSource.setMaxPoolSize(MAXPOOL);
    DataSource.setAcquireIncrement(1);
  }

  public static Connection getConnection() throws SQLException {
    return DataSource.getConnection();
  }

}
