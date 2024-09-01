package Models;
import java.sql.Connection;
import java.sql.DriverManager;

public class AppDbContext {

	private static String host = "jdbc:mysql://MYSQL8003.site4now.net:3306/db_aa55fb_testdb";    
	private static String user = "aa55fb_testdb";
	private static String password = "Abcd-123";
	public static Connection conn;

  public AppDbContext() {
      createConnection();
  }

  public static void createConnection() {
      try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          conn = DriverManager.getConnection(host, user, password);
          if (conn != null) {
              //System.out.println("Connect Success");
          }
      } catch (Exception ex) {
          //System.out.println("Connection Failed!");
      }
  }
  public static void disconnect() {
      if (conn != null) {
          try {
              conn.close();
          } catch (Exception ex) {
              System.out.println(ex.getMessage());
          }
      }
  }
}
