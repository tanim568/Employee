package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterPage extends JFrame implements ActionListener {

    JTextField nameField, emailField, usernameField;
    JPasswordField passwordField, confirmPasswordField;
    JButton registerButton;
    String generatedOtp;
    private final Color PRIMARY_COLOR = new Color(40, 58, 90);
    private final Color SECONDARY_COLOR = new Color(255, 140, 0);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);

    public RegisterPage() {
        JFrame frame = new JFrame("Employee Registration");
        frame.setSize(800, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Header Panel with Back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(BACKGROUND_COLOR);
        backButton.setForeground(PRIMARY_COLOR);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                // Sends user to LoginSelection class
                LoginSelection.main(new String[]{});
            }
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel headerLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension fieldSize = new Dimension(300, 35);

        addFormField(formPanel, gbc, "Full Name:", labelFont, 0);
        nameField = createStyledTextField(fieldFont, fieldSize);
        formPanel.add(nameField, gbc);

        addFormField(formPanel, gbc, "Email:", labelFont, 1);
        emailField = createStyledTextField(fieldFont, fieldSize);
        formPanel.add(emailField, gbc);

        addFormField(formPanel, gbc, "Username:", labelFont, 2);
        usernameField = createStyledTextField(fieldFont, fieldSize);
        formPanel.add(usernameField, gbc);

        addFormField(formPanel, gbc, "Password:", labelFont, 3);
        passwordField = createStyledPasswordField(fieldFont, fieldSize);
        formPanel.add(passwordField, gbc);

        addFormField(formPanel, gbc, "Confirm Password:", labelFont, 4);
        confirmPasswordField = createStyledPasswordField(fieldFont, fieldSize);
        formPanel.add(confirmPasswordField, gbc);

        // Register Button
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        registerButton = new JButton("Register Now");
        styleButton(registerButton, SECONDARY_COLOR, Color.WHITE, 16);
        registerButton.setPreferredSize(new Dimension(200, 45));
        registerButton.addActionListener(this);
        formPanel.add(registerButton, gbc);

        mainPanel.add(formPanel);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel footerLabel = new JLabel("Already have an account? ");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel signInLabel = new JLabel("Sign In Here");
        styleLinkLabel(signInLabel);
        footerPanel.add(footerLabel);
        footerPanel.add(signInLabel);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        signInLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                Login.main(new String[]{});
            }
            public void mouseEntered(MouseEvent e) {
                signInLabel.setForeground(new Color(200, 200, 200));
            }
            public void mouseExited(MouseEvent e) {
                signInLabel.setForeground(Color.WHITE);
            }
        });

        frame.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, Font font, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 10, 20);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(font);
        jLabel.setForeground(PRIMARY_COLOR);
        panel.add(jLabel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
    }

    private JTextField createStyledTextField(Font font, Dimension size) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setPreferredSize(size);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField(Font font, Dimension size) {
        JPasswordField field = new JPasswordField();
        field.setFont(font);
        field.setPreferredSize(size);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor, int fontSize) {
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void styleLinkLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
    }

    // Database method to save user data (password stored as plain text)
    private boolean saveUserToDatabase(String name, String email, String username, String password) {
        conn dbConn = new conn();
        try {
            // Check if username or email already exists
            String checkQuery = "SELECT COUNT(*) FROM login WHERE username = ? OR email = ?";
            PreparedStatement checkStmt = dbConn.getConnection().prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Username or Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                rs.close();
                checkStmt.close();
                dbConn.closeConnection();
                return false;
            }

            // Insert new user with plain text password
            String insertQuery = "INSERT INTO login (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = dbConn.getConnection().prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password); // Save password as plain text
            insertStmt.setString(3, email);

            int rowsAffected = insertStmt.executeUpdate();

            rs.close();
            checkStmt.close();
            insertStmt.close();
            dbConn.closeConnection();

            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dbConn.closeConnection();
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            // Email validation using regex
            String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!email.matches(emailRegex)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Check if all fields are filled
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Check if password is at least 6 characters long
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Generate and send OTP
            generatedOtp = generateOtp();
            try {
                sendEmail(email, generatedOtp);
                JOptionPane.showMessageDialog(this, "OTP sent to: " + email);
                showOtpVerificationDialog(email);
            } catch (MessagingException ex) {
                JOptionPane.showMessageDialog(this, "Error sending OTP: " + ex.getMessage());
            }
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }

    private void sendEmail(String recipient, String otp) throws MessagingException {
        final String senderEmail = "dummyworker44@gmail.com"; // Replace with your email
        final String senderPassword = "udaq gbas lond nvdq";     // Replace with your app password

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);
        Transport.send(message);
    }

    private void showOtpVerificationDialog(String email) {
        JFrame otpFrame = new JFrame("OTP Verification");
        otpFrame.setSize(450, 300);
        otpFrame.setLocationRelativeTo(null);
        otpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel otpPanel = new JPanel(new BorderLayout(10, 10));
        otpPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel headerLabel = new JLabel("OTP Verification", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 102, 204));

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JLabel otpLabel = new JLabel("Enter OTP:");
        otpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField otpField = new JTextField();
        otpField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(otpLabel);
        inputPanel.add(otpField);

        JButton verifyButton = new JButton("Verify OTP");
        verifyButton.setFont(new Font("Arial", Font.BOLD, 14));
        verifyButton.setBackground(new Color(34, 139, 34));
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setFocusPainted(false);

        otpPanel.add(headerLabel, BorderLayout.NORTH);
        otpPanel.add(inputPanel, BorderLayout.CENTER);
        otpPanel.add(verifyButton, BorderLayout.SOUTH);
        otpFrame.add(otpPanel);

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredOtp = otpField.getText().trim();
                if (enteredOtp.equals(generatedOtp)) {
                    // OTP verified successfully, now save to database
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String username = usernameField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();

                    if (saveUserToDatabase(name, email, username, password)) {
                        JOptionPane.showMessageDialog(otpFrame,
                                "Registration completed successfully!\nWelcome " + name + "!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        otpFrame.dispose();

                        // Close the registration window and go to login
                        dispose();
                        Login.main(new String[]{});
                    } else {
                        JOptionPane.showMessageDialog(otpFrame,
                                "Failed to save user data. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(otpFrame,
                            "Invalid OTP. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        otpFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterPage());
    }
}