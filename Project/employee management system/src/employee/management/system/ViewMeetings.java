package employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class ViewMeetingPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton refreshButton;

    public ViewMeetingPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[] {"ID", "Title", "Date", "Time", "Description"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadMeetings();
            }
        });
        add(refreshButton, BorderLayout.SOUTH);

        loadMeetings();
    }

    private void loadMeetings() {
        // Clear the table first
        model.setRowCount(0);
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement", "root", "password");
            Statement st = c.createStatement();
            // Query meetings for the current month and year
            String query = "SELECT meeting_id, meeting_title, meeting_date, meeting_time, meeting_description " +
                    "FROM meetings " +
                    "WHERE MONTH(meeting_date) = MONTH(CURRENT_DATE()) " +
                    "AND YEAR(meeting_date) = YEAR(CURRENT_DATE())";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("meeting_id"),
                        rs.getString("meeting_title"),
                        rs.getDate("meeting_date").toString(),
                        rs.getTime("meeting_time").toString(),
                        rs.getString("meeting_description")
                });
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading meetings: " + ex.getMessage());
        }
    }
}
