import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentTracking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ZxcvAsdf!@3";

    private static Connection connection;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    // Method to close the connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to execute an update query (e.g., INSERT, UPDATE, DELETE)
    public static int executeUpdate(String query, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            //A PreparedStatement is a precompiled SQL statement that can execute queries with parameters safely and efficiently.
            setParameters(preparedStatement, params);
            return preparedStatement.executeUpdate();
        }
    }

    /*
     *  private static void saveVisitToDatabase(String studentId, String studentName, String topic, String className, String reason) {
        String insertSQL = "INSERT INTO StudentVisits (student_name, class_name, visit_date, goals, goals_met) VALUES (?, ?, CURDATE(), ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, studentName + " (WIN: " + studentId + ")");
            preparedStatement.setString(2, className);
            preparedStatement.setString(3, "Tutoring Topic: " + topic + "; Reason: " + reason);
            preparedStatement.setBoolean(4, false); // Goals not marked met initially

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Visit logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to log visit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     */

    // Method to execute a SELECT query
    public static ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        setParameters(preparedStatement, params);
        return preparedStatement.executeQuery();
    }

    // Helper method to set parameters for PreparedStatement
    private static void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }
}
