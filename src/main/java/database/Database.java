package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for creating a connection to the Yelp business sqlite3 database.
 */
public final class Database {

  private Database() {

  }

  private static Connection yelpConn;


  /**
   * Establishes a connection to the SQLite database at the given filename
   * and returns the Connection object.
   *
   * @param filename file name of SQLite3 database to open.
   * @return connection object
   * @throws SQLException if an error occurs in any SQL query.
   * @throws ClassNotFoundException if getting the "org.sqlite.JDBC? class object
   *                                cannot be retrieved
   */
  public static Connection connectToDatabase(String filename)
      throws SQLException, ClassNotFoundException {
    File tempFile = new File(filename);
    if (!tempFile.exists()) {
      throw new IllegalArgumentException("ERROR: No database could be found at the given filepath");
    }
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    Connection conn = DriverManager.getConnection(urlToDB);
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
    return conn;
  }

  /**
   * Establishes a connection to the Yelp Database and sets yelpConn
   * to be this connection.
   */
  public static void setYelpDatabaseConnection() {
    try {
      yelpConn = connectToDatabase("data/yelp_business_database_w_attributes.sqlite3");
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println("ERROR: Connection to Yelp Database could not be established");
    }
  }

  /**
   * An accessor method for the Yelp Database Connection.
   * @return the Connection ton the Yelp Database.
   */
  public static Connection getYelpDatabaseConnection() {
    return yelpConn;
  }

}
