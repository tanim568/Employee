package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class MeetingManagementPanel2 extends JPanel {
    private JTextField titleField;
    private JTextField dateField; // Expected format: YYYY-MM-DD
    private JTextField timeField; // Expected format: HH:MM:SS
    private JTextArea descriptionArea;
    private JButton loadButton;
    private JButton saveButton;

    public MeetingManagementPanel2() {
        setLayout(new BorderLayout());

        // Form panel for meeting details
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Meeting Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Meeting Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Meeting Time (HH:MM:SS):"));
        timeField = new JTextField();
        formPanel.add(timeField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane);

        add(formPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        loadButton = new JButton("Load Meeting");
        saveButton = new JButton("Save Meeting");
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for the buttons
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadMeeting();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveMeeting();
            }
        });
    }

    // Loads the meeting record for the current month (if it exists)
    private void loadMeeting() {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement", "root", "password");
            Statement st = c.createStatement();
            // Query to fetch the latest meeting record for the current month and year
            String query = "SELECT * FROM meetings " +
                    "WHERE MONTH(meeting_date) = MONTH(CURRENT_DATE()) " +
                    "AND YEAR(meeting_date) = YEAR(CURRENT_DATE()) " +
                    "ORDER BY meeting_id DESC LIMIT 1";
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                titleField.setText(rs.getString("meeting_title"));
                dateField.setText(rs.getString("meeting_date"));
                timeField.setText(rs.getString("meeting_time"));
                descriptionArea.setText(rs.getString("meeting_description"));
            } else {
                JOptionPane.showMessageDialog(this, "No meeting record found for the current month.");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading meeting: " + ex.getMessage());
        }
    }

    // Saves the meeting by updating an existing record if one exists for the given month or inserting a new one
    private void saveMeeting() {
        String title = titleField.getText().trim();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String description = descriptionArea.getText().trim();

        if(title.isEmpty() || date.isEmpty() || time.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in Title, Date, and Time.");
            return;
        }

        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement", "root", "password");
            Statement st = c.createStatement();
            // Check if a meeting exists for the month corresponding to the entered date
            String checkQuery = "SELECT meeting_id FROM meetings " +
                    "WHERE MONTH(meeting_date) = MONTH('" + date + "') " +
                    "AND YEAR(meeting_date) = YEAR('" + date + "')";
            ResultSet rs = st.executeQuery(checkQuery);
            if(rs.next()){
                // Update the existing meeting record
                int meetingId = rs.getInt("meeting_id");
                String updateQuery = "UPDATE meetings SET meeting_title = '" + title + "', meeting_date = '" + date +
                        "', meeting_time = '" + time + "', meeting_description = '" + description + "' " +
                        "WHERE meeting_id = " + meetingId;
                int result = st.executeUpdate(updateQuery);
                if(result > 0){
                    JOptionPane.showMessageDialog(this, "Meeting updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Meeting update failed.");
                }
            } else {
                // Insert a new meeting record
                String insertQuery = "INSERT INTO meetings (meeting_title, meeting_date, meeting_time, meeting_description) " +
                        "VALUES ('" + title + "', '" + date + "', '" + time + "', '" + description + "')";
                int result = st.executeUpdate(insertQuery);
                if(result > 0){
                    JOptionPane.showMessageDialog(this, "Meeting added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add meeting.");
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving meeting: " + ex.getMessage());
        }
    }
}
