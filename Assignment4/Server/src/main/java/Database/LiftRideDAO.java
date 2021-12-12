package Database;

import Models.LiftRideDetails;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class LiftRideDAO {

  //using Hikari datasource
  private static HikariDataSource dataSource = DataSource.getDataSource();
  public LiftRideDAO() {
    //dataSource = DBCPDataSource.getDataSource();
  }

  public static int getTotalVertical(LiftRideDetails liftRideDetails) {
    String getQuery = "SELECT COUNT(*) AS count FROM skiers_info WHERE " +
        "skierId=? AND resortId=? AND seasonId=? AND dayId=?;";
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      connection = dataSource.getConnection();
      preparedStatement = connection.prepareStatement(getQuery);
      preparedStatement.setInt(1, liftRideDetails.getSkierId());
      preparedStatement.setInt(2, liftRideDetails.getResortId());
      preparedStatement.setInt(3, liftRideDetails.getSeasonId());
      preparedStatement.setInt(4, liftRideDetails.getDayId());
      System.out.println(preparedStatement.toString());
      resultSet = preparedStatement.executeQuery();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    System.out.println("resultSet: " + resultSet);
    try {
      if(resultSet.next()){
        try {
          return resultSet.getInt(1);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      return 0;
    }
    return 0;
  }

  public void createLiftRide(LiftRideDetails newLiftRide) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement =
        "INSERT INTO skiers_info (skierId, resortId, seasonId, dayId, time, liftId) " +
            "VALUES (?,?,?,?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newLiftRide.getSkierId());
      preparedStatement.setInt(2, newLiftRide.getResortId());
      preparedStatement.setInt(3, newLiftRide.getSeasonId());
      preparedStatement.setInt(4, newLiftRide.getDayId());
      preparedStatement.setInt(5, newLiftRide.getTime());
      preparedStatement.setInt(6, newLiftRide.getLiftId());

      // execute insert SQL statement
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  public void createTable(String tableName) {
    //    Connection conn = null;
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();) {
      System.out.println("Remote connection is successful");

      System.out.println("Attempting to create table: " + tableName);
      String createQueryStatement = "CREATE TABLE " + tableName +
          "(SkierId int," +
          " ResortId int," +
          " SeasonId int," +
          " DayId int," +
          " Time int," +
          " LiftId int);";
      stmt.executeUpdate(createQueryStatement);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteTable(String tableName) {
    String deleteQuery = "DROP TABLE " + tableName;
    try {
      Connection conn = dataSource.getConnection();
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(deleteQuery);
      System.out.println("Table deleted from the database:" + tableName);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}
