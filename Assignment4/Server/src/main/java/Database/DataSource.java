package Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

  private static HikariConfig hikariConfig = new HikariConfig();
  private static HikariDataSource hds;
  private static final int MAX_POOL_SIZE = 30;

  private DataSource() {
  }

  static {
    final String HOST_NAME = "db-instance-bsds.cfbxetwhee1m.us-east-1.rds.amazonaws.com";
    final String PORT = "3306";
    final String DATABASE = "bsdsdb";
    final String USERNAME = "admin";
    final String PASSWORD = "apeksha20";
    final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);

    hikariConfig.setDriverClassName(JDBC_DRIVER);
    hikariConfig.setJdbcUrl(url);
    hikariConfig.setUsername(USERNAME);
    hikariConfig.setPassword(PASSWORD);
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);
    hds = new HikariDataSource(hikariConfig);
    //hds.setMaximumPoolSize(MAX_POOL_SIZE);
  }

  public static HikariDataSource getDataSource() {
    return hds;
  }

  public static Connection getConnection() throws SQLException {
    return hds.getConnection();
  }

}
