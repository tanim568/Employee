package employee.management.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class OvertimePanel extends JPanel {

    public OvertimePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));

        // Top panel with buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsPanel.setBackground(new Color(240, 240, 240));
        JButton viewOvertimeBtn = new JButton("View Overtime Status");
        JButton requestOvertimeBtn = new JButton("Request Overtime");
        styleButton(viewOvertimeBtn);
        styleButton(requestOvertimeBtn);
        buttonsPanel.add(viewOvertimeBtn);
        buttonsPanel.add(requestOvertimeBtn);
        add(buttonsPanel, BorderLayout.NORTH);

        // Default center panel
        JPanel defaultPanel = new JPanel();
        defaultPanel.setBackground(new Color(240, 240, 240));
        add(defaultPanel, BorderLayout.CENTER);

        // View Overtime Status functionality
        viewOvertimeBtn.addActionListener(e -> showOvertimeStatus(buttonsPanel));

        // Request Overtime functionality
        requestOvertimeBtn.addActionListener(e -> showRequestOvertimeUI(buttonsPanel));
    }

    private void showOvertimeStatus(JPanel buttonsPanel) {
        removeAll();
        add(buttonsPanel, BorderLayout.NORTH);

        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBackground(new Color(240, 240, 240));
        viewPanel.setBorder(BorderFactory.createTitledBorder("Overtime Request Status"));

        String[] columns = {"Request ID", "Employee Email", "Overtime Date", "Hours", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        viewPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            Conn connection = new Conn();
            ResultSet rs = connection.statement.executeQuery(
                    "SELECT request_id, empEmail, overtime_date, hours, status FROM overtime_requests"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("request_id"),
                        rs.getString("empEmail"),
                        rs.getString("overtime_date"),
                        rs.getString("hours"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching overtime requests.");
        }

        add(viewPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showRequestOvertimeUI(JPanel buttonsPanel) {
        removeAll();
        add(buttonsPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = createSearchPanel();

        // Employee Table
        JTable employeeTable = createEmployeeTable();
        loadAllEmployees(employeeTable);

        // Request Details Panel
        JPanel requestPanel = createRequestPanel(employeeTable);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        mainPanel.add(requestPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Employee Search"));

        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn);

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            JScrollPane scrollPane = (JScrollPane) searchPanel.getParent().getComponent(1);
            JTable employeeTable = (JTable) scrollPane.getViewport().getView();
            searchEmployees(searchText, employeeTable);
        });

        searchPanel.add(new JLabel("Search Employee by Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        return searchPanel;
    }

    private JTable createEmployeeTable() {
        JTable employeeTable = new JTable();
        employeeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Non-editable table model
        employeeTable.setModel(new DefaultTableModel(
                new Object[]{"Employee ID", "Name", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        return employeeTable;
    }

    private void loadAllEmployees(JTable employeeTable) {
        try {
            Conn connection = new Conn();
            String query = "SELECT empID, name, email FROM employee";
            ResultSet rs = connection.statement.executeQuery(query);

            // Non-editable model
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Employee ID", "Name", "Email"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("empID"),
                        rs.getString("name"),
                        rs.getString("email")
                });
            }
            employeeTable.setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees.");
        }
    }

    private void searchEmployees(String searchText, JTable employeeTable) {
        try {
            Conn connection = new Conn();
            String query = "SELECT empID, name, email FROM employee";

            if (!searchText.isEmpty()) {
                query += " WHERE name LIKE '%" + searchText + "%'";
            }

            ResultSet rs = connection.statement.executeQuery(query);
            // Non-editable model
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Employee ID", "Name", "Email"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("empID"),
                        rs.getString("name"),
                        rs.getString("email")
                });
            }
            employeeTable.setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching employees.");
        }
    }

    private JPanel createRequestPanel(JTable employeeTable) {
        JPanel requestPanel = new JPanel(new GridBagLayout());
        requestPanel.setBackground(new Color(245, 245, 245));
        requestPanel.setBorder(BorderFactory.createTitledBorder("Overtime Request Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectedLabel = new JLabel("Selected Employees: 0");
        JTextField dateField = new JTextField(15);
        JTextField hoursField = new JTextField(15);
        JButton submitBtn = new JButton("Submit Request");
        styleButton(submitBtn);

        // Selection listener
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            int count = employeeTable.getSelectedRowCount();
            selectedLabel.setText("Selected Employees: " + count);
        });

        // Submit action
        submitBtn.addActionListener(e -> submitRequests(
                employeeTable,
                dateField.getText().trim(),
                hoursField.getText().trim(),
                selectedLabel
        ));

        // Layout components
        gbc.gridwidth = 2;
        requestPanel.add(selectedLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        requestPanel.add(new JLabel("Overtime Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        requestPanel.add(dateField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        requestPanel.add(new JLabel("Hours:"), gbc);
        gbc.gridx = 1;
        requestPanel.add(hoursField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        requestPanel.add(submitBtn, gbc);

        return requestPanel;
    }

    private void submitRequests(JTable employeeTable, String date, String hours, JLabel selectedLabel) {
        int[] selectedRows = employeeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one employee.");
            return;
        }
        if (date.isEmpty() || hours.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all overtime details.");
            return;
        }

        try {
            Conn connection = new Conn();
            int successCount = 0;

            for (int row : selectedRows) {
                String email = employeeTable.getValueAt(row, 2).toString();
                String query = "INSERT INTO overtime_requests (empEmail, overtime_date, hours, status) " +
                        "VALUES ('" + email + "', '" + date + "', " + hours + ", 'Pending')";

                if (connection.statement.executeUpdate(query) > 0) {
                    successCount++;
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Successfully submitted " + successCount + "/" + selectedRows.length + " requests");

            // Reset UI
            selectedLabel.setText("Selected Employees: 0");
            employeeTable.clearSelection();
            loadAllEmployees(employeeTable);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Sans Serif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    class Conn {
        Connection c;
        Statement statement;

        public Conn() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement", "root", "password");
                statement = c.createStatement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
