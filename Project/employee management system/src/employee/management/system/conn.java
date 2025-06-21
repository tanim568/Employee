package employee.management.system;

import javax.swing.*;
import java.sql.*;

public class conn {
    Connection connection;
    Statement statement;

    public conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/employeemanagement",
                    "root",
                    "password"
            );

            statement = connection.createStatement();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed! Check your credentials and database.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
