import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "system",
                    "nikhila");   // your password

            System.out.println("Database Connected");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;   // ⚠ return without closing
    }
}