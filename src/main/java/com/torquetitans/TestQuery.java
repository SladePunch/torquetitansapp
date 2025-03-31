package GarageApp;
import java.sql.*;

public class TestQuery {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@calvin.humber.ca:1521:grok"; 
        String user = "n01672852"; // Your database username
        String password = "oracle"; // Your database password

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Oracle DB successfully!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM dual"); // Với Oracle dùng FROM dual

            if (rs.next()) {
                System.out.println("Query successful! Result: " + rs.getInt(1));
            }

            conn.close();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}