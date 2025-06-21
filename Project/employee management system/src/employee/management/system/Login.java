package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;

public class Login extends JFrame implements ActionListener {

    JTextField tusername;
    JPasswordField tpassword;
    JButton login, back;

    Login() {
        setTitle("Employee Management System - Admin Login");

        // Username Label
        JLabel username = new JLabel("Username");
        username.setBounds(40, 20, 100, 30);
        add(username);

        tusername = new JTextField();
        tusername.setBounds(150, 20, 150, 30);
        add(tusername);

        // Password Label
        JLabel password = new JLabel("Password");
        password.setBounds(40, 70, 100, 30);
        add(password);

        tpassword = new JPasswordField();
        tpassword.setBounds(150, 70, 150, 30);
        add(tpassword);

        // Login Button
        login = new JButton("LOGIN");
        login.setBounds(150, 140, 150, 30);
        login.setBackground(Color.BLACK);
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        add(login);

        // Back Button
        back = new JButton("BACK");
        back.setBounds(150, 180, 150, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        // Right-side Icon
        try {
            ImageIcon icon1 = new ImageIcon(getClass().getResource("/icons/second.jpg"));
            Image img1 = icon1.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel imgg = new JLabel(new ImageIcon(img1));
            imgg.setBounds(350, 12, 200, 200);
            add(imgg);
        } catch (Exception ex) {
            System.out.println("Image not found: second.jpg");
        }

        // Background Image
        try {
            ImageIcon icon2 = new ImageIcon(getClass().getResource("/icons/LoginB.jpg"));
            Image img2 = icon2.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
            JLabel img = new JLabel(new ImageIcon(img2));
            img.setBounds(0, 0, 600, 300);
            add(img);
        } catch (Exception ex) {
            System.out.println("Image not found: LoginB.jpg");
        }

        setSize(600, 300);
        setLocation(450, 200);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            loginUser();
        } else if (e.getSource() == back) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginSelection().setVisible(true));
        }
    }

    private void loginUser() {
        String username = tusername.getText().trim();
        String password = new String(tpassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Username and Password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn c = new conn();
            connection = c.getConnection();

            // Updated query to use admin_login table with additional checks
            String query = "SELECT id, username, full_name, email, role, is_active FROM admin_login WHERE username = ? AND password = ? AND is_active = TRUE";

            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Get admin details
                int adminId = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String role = rs.getString("role");

                // Update last login time
                updateLastLogin(connection, adminId);

                // Show success message with admin details
                String welcomeMessage = "Login Successful!\n" +
                        "Welcome, " + (fullName != null ? fullName : username) + "\n" +
                        "Role: " + role;

                JOptionPane.showMessageDialog(this, welcomeMessage, "Login Success", JOptionPane.INFORMATION_MESSAGE);

                // Hide login window and open main application
                setVisible(false);
                new Main_class().setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password, or account is inactive.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

                // Clear password field for security
                tpassword.setText("");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Connection error. Please check your database connection.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close all database resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to update last login timestamp
    private void updateLastLogin(Connection connection, int adminId) {
        PreparedStatement updateStmt = null;
        try {
            String updateQuery = "UPDATE admin_login SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
            updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, adminId);
            updateStmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Could not update last login time: " + ex.getMessage());
        } finally {
            try {
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}