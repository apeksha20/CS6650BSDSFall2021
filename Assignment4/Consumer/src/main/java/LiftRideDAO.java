import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class LiftRideDAO {

  //private static BasicDataSource dataSource;
  //using Hikari datasource
  private static HikariDataSource dataSource = DataSource.getDataSource();
  public LiftRideDAO() {
    //dataSource = DBCPDataSource.getDataSource();
  }

  public void createLiftRide(LiftRide newLiftRide) {
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
