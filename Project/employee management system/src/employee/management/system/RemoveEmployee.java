package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class RemoveEmployeePanel extends JPanel implements ActionListener {
    private JTextField searchField;
    private JButton searchButton, deleteButton, backButton;
    private JLabel nameLabel, phoneLabel, emailLabel, addressLabel, genderLabel, salaryLabel;
    private JLabel nameValue, phoneValue, emailValue, addressValue, genderValue, salaryValue;
    private String empID;

    public RemoveEmployeePanel() {
        setLayout(null);
        setBackground(new Color(230, 230, 230));

        JLabel searchLabel = new JLabel("Search Employee by Name:");
        searchLabel.setBounds(50, 30, 200, 30);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(250, 30, 200, 30);
        add(searchField);

        searchButton = new JButton("Search");
        searchButton.setBounds(470, 30, 100, 30);
        searchButton.addActionListener(this);
        add(searchButton);

        nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 80, 100, 30);
        add(nameLabel);

        nameValue = new JLabel();
        nameValue.setBounds(200, 80, 200, 30);
        add(nameValue);

        phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 120, 100, 30);
        add(phoneLabel);

        phoneValue = new JLabel();
        phoneValue.setBounds(200, 120, 200, 30);
        add(phoneValue);

        emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 160, 100, 30);
        add(emailLabel);

        emailValue = new JLabel();
        emailValue.setBounds(200, 160, 200, 30);
        add(emailValue);

        addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50, 200, 100, 30);
        add(addressLabel);

        addressValue = new JLabel();
        addressValue.setBounds(200, 200, 200, 30);
        add(addressValue);

        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(50, 240, 100, 30);
        add(genderLabel);

        genderValue = new JLabel();
        genderValue.setBounds(200, 240, 200, 30);
        add(genderValue);

        salaryLabel = new JLabel("Salary:");
        salaryLabel.setBounds(50, 280, 100, 30);
        add(salaryLabel);

        salaryValue = new JLabel();
        salaryValue.setBounds(200, 280, 200, 30);
        add(salaryValue);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 340, 100, 30);
        deleteButton.addActionListener(this);
        deleteButton.setEnabled(false);
        add(deleteButton);

        backButton = new JButton("Back");
        backButton.setBounds(250, 340, 100, 30);
        backButton.addActionListener(this);
        add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            searchEmployee();
        } else if (e.getSource() == deleteButton) {
            deleteEmployee();
        } else if (e.getSource() == backButton) {
            // For "BACK", clear the panel or signal the parent to change the view.
            this.removeAll();
            this.repaint();
        }
    }

    private void searchEmployee() {
        String name = searchField.getText();
        try {
            conn c = new conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM employee WHERE name LIKE '%" + name + "%'");
            if (rs.next()) {
                empID = rs.getString("empID");
                nameValue.setText(rs.getString("name"));
                phoneValue.setText(rs.getString("phone"));
                emailValue.setText(rs.getString("email"));
                addressValue.setText(rs.getString("address"));
                genderValue.setText(rs.getString("gender"));
                salaryValue.setText(rs.getString("salary"));
                deleteButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Employee not found");
                deleteButton.setEnabled(false);
            }
            rs.close();
            c.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteEmployee() {
        try {
            conn c = new conn();
            String query = "DELETE FROM employee WHERE empID = '" + empID + "'";
            c.s.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Employee Deleted Successfully");
            clearFields();
            c.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        nameValue.setText("");
        phoneValue.setText("");
        emailValue.setText("");
        addressValue.setText("");
        genderValue.setText("");
        salaryValue.setText("");
        deleteButton.setEnabled(false);
    }

    // Inner class for database connection
    class conn {
        Connection c;
        Statement s;
        public conn() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement", "root", "password");
                s = c.createStatement();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        public void closeConnection() {
            try {
                if(c != null)
                    c.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
