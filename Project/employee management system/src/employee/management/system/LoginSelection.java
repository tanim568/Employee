package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginSelection extends JFrame implements ActionListener {
    JButton employeeBtn, adminBtn, registerBtn;

    LoginSelection() {
        // Frame settings
        setSize(1170, 650);
        setLocation(200, 50);
        setUndecorated(true); // Remove default title bar
        setShape(new RoundRectangle2D.Double(0, 0, 1170, 650, 50, 50)); // Rounded corners
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245)); // Light background

        // Heading
        JLabel heading = new JLabel("Welcome to Employee Management");
        heading.setBounds(300, 50, 600, 50);
        heading.setFont(new Font("Poppins", Font.BOLD, 32));
        heading.setForeground(new Color(59, 89, 182)); // Dark blue
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        add(heading);

        // Subheading
        JLabel subheading = new JLabel("Choose your login type");
        subheading.setBounds(300, 100, 600, 30);
        subheading.setFont(new Font("Poppins", Font.PLAIN, 18));
        subheading.setForeground(new Color(128, 128, 128)); // Gray
        subheading.setHorizontalAlignment(SwingConstants.CENTER);
        add(subheading);

        // Employee Login Button
        employeeBtn = createStyledButton("Employee Login", 450, 200);
        employeeBtn.setBackground(new Color(59, 89, 182)); // Blue
        employeeBtn.setForeground(Color.WHITE);
        employeeBtn.addActionListener(this);
        add(employeeBtn);

        // Admin Login Button
        adminBtn = createStyledButton("Admin/HR Login", 450, 300);
        adminBtn.setBackground(new Color(46, 204, 113)); // Green
        adminBtn.setForeground(Color.WHITE);
        adminBtn.addActionListener(this);
        add(adminBtn);

        // Register Button
        registerBtn = createStyledButton("Register", 450, 400);
        registerBtn.setBackground(new Color(231, 76, 60)); // Red
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(this);
        add(registerBtn);

        // Close Button (Top-right corner)
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(1120, 10, 40, 40);
        closeBtn.setFont(new Font("Poppins", Font.BOLD, 16));
        closeBtn.setBackground(new Color(231, 76, 60)); // Red
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        add(closeBtn);

        setVisible(true);
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 250, 50);
        button.setFont(new Font("Poppins", Font.BOLD, 18));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == employeeBtn) {
            setVisible(false);
            new EmployeeLogin();
        } else if (ae.getSource() == adminBtn) {
            setVisible(false);
            new Login();
        } else if (ae.getSource() == registerBtn) {
            setVisible(false);
            new RegisterPage();
        }
    }

    public static void main(String[] strings) {
        new LoginSelection();
    }
}
