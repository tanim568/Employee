package employee.management.system;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.ArrayList;


public class UpdateMeetingPanel extends JPanel {
    private JTextField titleField;
    private JDateChooser dateChooser;       // Using JDateChooser instead of dateField + date picker
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JComboBox<String> amPmComboBox;
    private JTextArea descriptionArea;
    private JButton addButton;
    private JButton updateButton;
    private int selectedMeetingId = -1;

    public UpdateMeetingPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Form panel for meeting details
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                "Meeting Details",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(0, 102, 204)));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Meeting Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createStyledLabel("Meeting Title:"), gbc);
        gbc.gridx = 1;
        titleField = createStyledTextField(25);
        formPanel.add(titleField, gbc);

        // 2. Meeting Date (using JDateChooser)
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(createStyledLabel("Meeting Date:"), gbc);
        gbc.gridx = 1;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");  // Show format in the UI
        dateChooser.setPreferredSize(new Dimension(150, 28));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(dateChooser);
        formPanel.add(datePanel, gbc);

        // 3. Meeting Time
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(createStyledLabel("Meeting Time:"), gbc);
        gbc.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(Color.WHITE);

        // Hour Spinner
        hourSpinner = createStyledSpinner();
        hourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));

        // Minute Spinner
        minuteSpinner = createStyledSpinner();
        minuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));

        // AM/PM ComboBox
        amPmComboBox = new JComboBox<>(new String[]{"AM", "PM"});
        amPmComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        timePanel.add(createStyledLabel("HH:"));
        timePanel.add(hourSpinner);
        timePanel.add(createStyledLabel("MM:"));
        timePanel.add(minuteSpinner);
        timePanel.add(amPmComboBox);
        formPanel.add(timePanel, gbc);

        // 4. Description
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(createStyledLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel (Add / Update)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        addButton = createStyledButton("Add Meeting", new Color(76, 175, 80));
        updateButton = createStyledButton("Update Meeting", new Color(33, 150, 243));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> saveMeeting());
        updateButton.addActionListener(e -> {
            if (selectedMeetingId == -1) {
                showMeetingSelectionDialog();
            } else {
                saveMeeting();
            }
        });
    }

    //--------------------------------------------------------------------------------
    // Styling helper methods
    //--------------------------------------------------------------------------------
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return field;
    }

    private JSpinner createStyledSpinner() {
        JSpinner spinner = new JSpinner();
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spinner.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        Dimension size = new Dimension(60, 28);
        spinner.setPreferredSize(size);
        spinner.setMinimumSize(size);
        return spinner;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    //--------------------------------------------------------------------------------
    // Loading & selecting existing meetings (unchanged logic)
    //--------------------------------------------------------------------------------
    private void showMeetingSelectionDialog() {
        List<Meeting> meetings = loadMeetingsForCurrentMonth();
        if (meetings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No meetings found for current month");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Select Meeting to Update", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);

        DefaultListModel<Meeting> listModel = new DefaultListModel<>();
        JList<Meeting> meetingList = new JList<>(listModel);
        meetingList.setCellRenderer(new MeetingListCellRenderer());
        meetings.forEach(listModel::addElement);

        JScrollPane scrollPane = new JScrollPane(meetingList);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            Meeting selected = meetingList.getSelectedValue();
            if (selected != null) {
                populateForm(selected);
                dialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private List<Meeting> loadMeetingsForCurrentMonth() {
        List<Meeting> meetings = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employeemanagement", "root", "password")) {

            String query = "SELECT * FROM meetings " +
                    "WHERE MONTH(meeting_date) = MONTH(CURRENT_DATE()) " +
                    "AND YEAR(meeting_date) = YEAR(CURRENT_DATE()) " +
                    "ORDER BY meeting_date, meeting_time";

            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    meetings.add(new Meeting(
                            rs.getInt("meeting_id"),
                            rs.getString("meeting_title"),
                            rs.getString("meeting_date"),  // storing as String in DB
                            rs.getString("meeting_time"),
                            rs.getString("meeting_description")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading meetings: " + ex.getMessage());
        }
        return meetings;
    }

    //--------------------------------------------------------------------------------
    // Populating form when user selects a meeting to update
    //--------------------------------------------------------------------------------
    private void populateForm(Meeting meeting) {
        selectedMeetingId = meeting.id;
        titleField.setText(meeting.title);

        // Parse the date from the DB string and set it to JDateChooser
        try {
            if (meeting.date != null && !meeting.date.isEmpty()) {
                java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(meeting.date);
                dateChooser.setDate(parsedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Parse the time from the DB string (HH:mm:ss) and update the spinners
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("HH:mm:ss");
            java.util.Date time = dbFormat.parse(meeting.time);

            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            int hour24 = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            // Convert 24-hour to 12-hour + AM/PM
            String amPm;
            int hour12;
            if (hour24 == 0) {
                hour12 = 12;
                amPm = "AM";
            } else if (hour24 == 12) {
                hour12 = 12;
                amPm = "PM";
            } else if (hour24 > 12) {
                hour12 = hour24 - 12;
                amPm = "PM";
            } else {
                hour12 = hour24;
                amPm = "AM";
            }

            hourSpinner.setValue(hour12);
            minuteSpinner.setValue(minute);
            amPmComboBox.setSelectedItem(amPm);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        descriptionArea.setText(meeting.description);
    }

    //--------------------------------------------------------------------------------
    // Insert / Update meeting logic
    //--------------------------------------------------------------------------------
    private void saveMeeting() {
        String title = titleField.getText().trim();
        java.util.Date utilDate = dateChooser.getDate(); // Retrieve date from JDateChooser
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || utilDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill in Title and Date.");
            return;
        }

        // Convert selected date to yyyy-MM-dd format for DB
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(utilDate);

        try {
            // Build the time string from spinners
            int hour = (int) hourSpinner.getValue();
            int minute = (int) minuteSpinner.getValue();
            String amPm = (String) amPmComboBox.getSelectedItem();

            // Convert 12-hour to 24-hour
            if ("PM".equals(amPm) && hour < 12) {
                hour += 12;
            }
            if ("AM".equals(amPm) && hour == 12) {
                hour = 0;
            }
            String timeString = String.format("%02d:%02d:00", hour, minute);

            // Connect to DB
            try (Connection c = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/employeemanagement", "root", "password")) {

                if (selectedMeetingId > 0) {
                    // UPDATE
                    String query = "UPDATE meetings SET meeting_title=?, meeting_date=?, meeting_time=?, meeting_description=? WHERE meeting_id=?";
                    try (PreparedStatement pst = c.prepareStatement(query)) {
                        pst.setString(1, title);
                        pst.setString(2, dateString);
                        pst.setString(3, timeString);
                        pst.setString(4, description);
                        pst.setInt(5, selectedMeetingId);
                        if (pst.executeUpdate() > 0) {
                            JOptionPane.showMessageDialog(this, "Meeting updated!");
                            clearForm();
                        }
                    }
                } else {
                    // INSERT
                    String query = "INSERT INTO meetings (meeting_title, meeting_date, meeting_time, meeting_description) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pst = c.prepareStatement(query)) {
                        pst.setString(1, title);
                        pst.setString(2, dateString);
                        pst.setString(3, timeString);
                        pst.setString(4, description);
                        if (pst.executeUpdate() > 0) {
                            JOptionPane.showMessageDialog(this, "Meeting added!");
                            clearForm();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        selectedMeetingId = -1;
        titleField.setText("");
        dateChooser.setDate(null); // clear date
        hourSpinner.setValue(12);
        minuteSpinner.setValue(0);
        amPmComboBox.setSelectedIndex(0);
        descriptionArea.setText("");
    }

    //--------------------------------------------------------------------------------
    // Inner classes for storing & rendering Meeting objects
    //--------------------------------------------------------------------------------
    private static class Meeting {
        int id;
        String title;
        String date;   // "yyyy-MM-dd"
        String time;   // "HH:mm:ss"
        String description;

        public Meeting(int id, String title, String date, String time, String description) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.time = time;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("%s %s - %s", date, time, title);
        }
    }

    private static class MeetingListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Meeting meeting = (Meeting) value;
            String text = String.format("%s | %s | %s", meeting.date, meeting.time, meeting.title);
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
    }
}
