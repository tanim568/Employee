package employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeSearch extends JPanel {
    private JTextField searchField;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JPanel editPanel;
    private JTextField[] editFields;
    private String[] columnNames = {"Employee ID", "Name", "Father's Name", "DOB", "Salary", "Address", "Phone", "Email", "Education", "Department", "Gender"};
    private String currentEmployeeId = "";

    public EmployeeSearch() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initializeComponents();
        loadEmployeeData();
    }

    private void initializeComponents() {
        // Top panel for search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Employees"));

        searchPanel.add(new JLabel("Search by Name/ID:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchEmployees());
        searchPanel.add(searchButton);

        JButton refreshButton = new JButton("Refresh All");
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadEmployeeData());
        searchPanel.add(refreshButton);

        add(searchPanel, BorderLayout.NORTH);

        // Center panel with table
        createEmployeeTable();

        // Bottom panel for editing (initially hidden)
        createEditPanel();
    }

    private void createEmployeeTable() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(25);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add double-click listener to edit employee
        employeeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = employeeTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        editEmployee(selectedRow);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Employee List (Double-click to edit)"));
        scrollPane.setPreferredSize(new Dimension(850, 300));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createEditPanel() {
        editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Employee Details"));
        editPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Create edit fields for each column
        editFields = new JTextField[columnNames.length];

        // Layout: 2 columns, multiple rows
        for (int i = 0; i < columnNames.length; i++) {
            int col = i % 2;
            int row = i / 2;

            gbc.gridx = col * 2;
            gbc.gridy = row;

            JLabel label = new JLabel(columnNames[i] + ":");
            label.setFont(new Font("Arial", Font.BOLD, 12));
            editPanel.add(label, gbc);

            gbc.gridx = col * 2 + 1;
            editFields[i] = new JTextField(15);

            // Make Employee ID read-only
            if (i == 0) { // Employee ID
                editFields[i].setEditable(false);
                editFields[i].setBackground(Color.LIGHT_GRAY);
            }

            // Add special handling for gender field
            if (columnNames[i].equals("Gender")) {
                JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
                editPanel.add(genderCombo, gbc);
                // Store reference to combo box for later use
                editFields[i] = new JTextField(); // Keep the array structure
                editFields[i].putClientProperty("comboBox", genderCombo);
            } else {
                editPanel.add(editFields[i], gbc);
            }
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton updateButton = new JButton("Update Employee");
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> updateEmployee());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> hideEditPanel());

        JButton deleteButton = new JButton("Delete Employee");
        deleteButton.setBackground(new Color(139, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteEmployee());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = (columnNames.length + 1) / 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        editPanel.add(buttonPanel, gbc);

        add(editPanel, BorderLayout.SOUTH);
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0); // Clear existing data

        try {
            conn c = new conn();
            String query = "SELECT empID, name, fname, dob, salary, address, phone, email, education, department, gender FROM employee ORDER BY name";
            ResultSet rs = c.statement.executeQuery(query);

            while(rs.next()) {
                Object[] rowData = {
                        rs.getString("empID"),
                        rs.getString("name"),
                        rs.getString("fname"),
                        rs.getDate("dob"),
                        rs.getBigDecimal("salary"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("education"),
                        rs.getString("department"),
                        rs.getString("gender")
                };
                tableModel.addRow(rowData);
            }

            rs.close();
            c.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }
    }

    private void searchEmployees() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadEmployeeData();
            return;
        }

        tableModel.setRowCount(0);

        try {
            conn c = new conn();
            String query = "SELECT empID, name, fname, dob, salary, address, phone, email, education, department, gender " +
                    "FROM employee WHERE name LIKE ? OR empID LIKE ? OR email LIKE ? ORDER BY name";
            PreparedStatement pstmt = c.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            pstmt.setString(3, "%" + searchTerm + "%");

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Object[] rowData = {
                        rs.getString("empID"),
                        rs.getString("name"),
                        rs.getString("fname"),
                        rs.getDate("dob"),
                        rs.getBigDecimal("salary"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("education"),
                        rs.getString("department"),
                        rs.getString("gender")
                };
                tableModel.addRow(rowData);
            }

            rs.close();
            pstmt.close();
            c.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching employees: " + e.getMessage());
        }
    }

    private void editEmployee(int row) {
        // Get employee data from selected row
        currentEmployeeId = tableModel.getValueAt(row, 0).toString(); // empID

        // Fill edit fields with current data
        for (int i = 0; i < columnNames.length; i++) {
            Object value = tableModel.getValueAt(row, i);

            if (columnNames[i].equals("Gender")) {
                // Handle gender combo box
                JComboBox<String> combo = (JComboBox<String>) editFields[i].getClientProperty("comboBox");
                if (combo != null && value != null) {
                    combo.setSelectedItem(value.toString());
                }
            } else {
                editFields[i].setText(value != null ? value.toString() : "");
            }
        }

        // Show edit panel
        editPanel.setVisible(true);
        revalidate();
        repaint();

        // Scroll to edit panel
        SwingUtilities.invokeLater(() -> {
            JScrollPane parentScrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
            if (parentScrollPane != null) {
                parentScrollPane.getVerticalScrollBar().setValue(parentScrollPane.getVerticalScrollBar().getMaximum());
            }
        });
    }

    private void updateEmployee() {
        if (currentEmployeeId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employee selected for update.");
            return;
        }

        // Validate required fields
        if (editFields[1].getText().trim().isEmpty()) { // Name
            JOptionPane.showMessageDialog(this, "Employee name is required.");
            return;
        }

        if (editFields[7].getText().trim().isEmpty()) { // Email
            JOptionPane.showMessageDialog(this, "Employee email is required.");
            return;
        }

        try {
            conn c = new conn();
            String query = "UPDATE employee SET name=?, fname=?, dob=?, salary=?, address=?, phone=?, email=?, education=?, department=?, gender=? WHERE empID=?";
            PreparedStatement pstmt = c.getConnection().prepareStatement(query);

            // Set parameters
            pstmt.setString(1, editFields[1].getText().trim()); // name
            pstmt.setString(2, editFields[2].getText().trim()); // fname
            pstmt.setString(3, editFields[3].getText().trim()); // dob

            // Handle salary - convert to decimal
            try {
                double salary = Double.parseDouble(editFields[4].getText().trim());
                pstmt.setDouble(4, salary);
            } catch (NumberFormatException e) {
                pstmt.setDouble(4, 0.0);
            }

            pstmt.setString(5, editFields[5].getText().trim()); // address
            pstmt.setString(6, editFields[6].getText().trim()); // phone
            pstmt.setString(7, editFields[7].getText().trim()); // email
            pstmt.setString(8, editFields[8].getText().trim()); // education
            pstmt.setString(9, editFields[9].getText().trim()); // department

            // Handle gender combo box
            JComboBox<String> genderCombo = (JComboBox<String>) editFields[10].getClientProperty("comboBox");
            String gender = genderCombo != null ? (String) genderCombo.getSelectedItem() : editFields[10].getText().trim();
            pstmt.setString(10, gender);

            pstmt.setString(11, currentEmployeeId); // empID for WHERE clause

            int result = pstmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                hideEditPanel();
                loadEmployeeData(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update employee.");
            }

            pstmt.close();
            c.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        if (currentEmployeeId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employee selected for deletion.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this employee?\nThis action cannot be undone.\nEmployee ID: " + currentEmployeeId,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn c = new conn();

                // First, delete related records from holiday_requests and overtime_requests
                String deleteHolidayQuery = "DELETE FROM holiday_requests WHERE empEmail = (SELECT email FROM employee WHERE empID = ?)";
                PreparedStatement pstmt1 = c.getConnection().prepareStatement(deleteHolidayQuery);
                pstmt1.setString(1, currentEmployeeId);
                pstmt1.executeUpdate();
                pstmt1.close();

                String deleteOvertimeQuery = "DELETE FROM overtime_requests WHERE empEmail = (SELECT email FROM employee WHERE empID = ?)";
                PreparedStatement pstmt2 = c.getConnection().prepareStatement(deleteOvertimeQuery);
                pstmt2.setString(1, currentEmployeeId);
                pstmt2.executeUpdate();
                pstmt2.close();

                // Now delete the employee
                String query = "DELETE FROM employee WHERE empID=?";
                PreparedStatement pstmt = c.getConnection().prepareStatement(query);
                pstmt.setString(1, currentEmployeeId);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    hideEditPanel();
                    loadEmployeeData(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee.");
                }

                pstmt.close();
                c.closeConnection();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage());
            }
        }
    }

    private void hideEditPanel() {
        editPanel.setVisible(false);
        currentEmployeeId = "";
        // Clear edit fields
        for (int i = 0; i < editFields.length; i++) {
            if (columnNames[i].equals("Gender")) {
                JComboBox<String> combo = (JComboBox<String>) editFields[i].getClientProperty("comboBox");
                if (combo != null) {
                    combo.setSelectedIndex(0);
                }
            } else {
                editFields[i].setText("");
            }
        }
        revalidate();
        repaint();
    }
}