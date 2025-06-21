package employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import com.toedter.calendar.JDateChooser;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Employee_class extends JFrame {
    // Background panel class
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private JPanel contentPanel;
    private String userEmail;
    private JLabel notificationLabel;

    public Employee_class(String userEmail) {
        this.userEmail = userEmail;

        // Setup background
        Image backgroundImage = new ImageIcon(
                ClassLoader.getSystemResource("icons/home.jpg")
        ).getImage();
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImage);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Header panel with notification area
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel heading = new JLabel("Employee Management System", SwingConstants.CENTER);
        heading.setFont(new Font("Raleway", Font.BOLD, 28));
        heading.setForeground(Color.BLACK);
        headerPanel.add(heading, BorderLayout.CENTER);

        // Notification area
        notificationLabel = new JLabel("", SwingConstants.CENTER);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        notificationLabel.setForeground(Color.RED);
        notificationLabel.setBackground(Color.YELLOW);
        notificationLabel.setOpaque(true);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationLabel.setVisible(false);
        headerPanel.add(notificationLabel, BorderLayout.SOUTH);

        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        // Left navigation
        JPanel employeePanel = new JPanel();
        employeePanel.setPreferredSize(new Dimension(220, 0));
        employeePanel.setBackground(new Color(50, 50, 50, 200));
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.Y_AXIS));
        employeePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel employeeMenuLabel = new JLabel("Non HR MENU");
        employeeMenuLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        employeeMenuLabel.setForeground(Color.WHITE);
        employeeMenuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        employeePanel.add(employeeMenuLabel);
        employeePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] employeeOptions = {
                "View/Change Information",
                "View/Change Holidays",
                "View/Change Meetings",
                "View/Change Overtime",
                "Check New Updates",       // Only notification option needed
                "Log Out"
        };
        for (String option : employeeOptions) {
            JButton btn = new JButton(option);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(new EmployeeMenuActionListener(option));
            employeePanel.add(btn);
            employeePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        mainPanel.add(employeePanel, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setTitle("Employee Management System - " + userEmail);
        setSize(1120, 630);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        System.out.println("Employee system loaded for user: " + userEmail);
        showChangeViewInformationPanel(userEmail);
    }

    // Manual check for real new notifications
    private void checkForRealNotifications() {
        System.out.println("Checking for new notifications for user: " + userEmail);

        List<MeetingInfo> newMeetings = checkForNewMeetings();
        List<OvertimeInfo> newOvertimeRequests = checkForNewOvertimeRequests();

        if (!newMeetings.isEmpty() || !newOvertimeRequests.isEmpty()) {
            showCombinedNotification(newMeetings, newOvertimeRequests);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No new notifications found.",
                    "Check Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Check for new meetings (for all employees)
    private List<MeetingInfo> checkForNewMeetings() {
        List<MeetingInfo> newMeetings = new ArrayList<>();

        try {
            conn connection = new conn();
            String query = "SELECT m.meeting_id, m.meeting_title, m.meeting_date, m.meeting_time " +
                    "FROM meetings m " +
                    "WHERE m.meeting_date >= CURDATE() " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM notification_tracking nt " +
                    "    WHERE nt.user_email = ? " +
                    "    AND nt.notification_type = 'meeting' " +
                    "    AND nt.reference_id = m.meeting_id" +
                    ") " +
                    "ORDER BY m.meeting_id DESC";

            PreparedStatement pstmt = connection.c.prepareStatement(query);
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MeetingInfo meeting = new MeetingInfo();
                meeting.id = rs.getInt("meeting_id");
                meeting.title = rs.getString("meeting_title");
                meeting.date = rs.getDate("meeting_date");
                meeting.time = rs.getTime("meeting_time");
                newMeetings.add(meeting);
                System.out.println("Found new meeting for " + userEmail + ": " + meeting.title + " (ID: " + meeting.id + ")");
            }

            rs.close();
            pstmt.close();
            connection.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error checking for new meetings: " + e.getMessage());
        }

        return newMeetings;
    }

    // Check for new overtime requests (only for current user)
    private List<OvertimeInfo> checkForNewOvertimeRequests() {
        List<OvertimeInfo> newOvertimeRequests = new ArrayList<>();

        try {
            conn connection = new conn();
            String query = "SELECT o.request_id, o.overtime_date, o.hours, o.status " +
                    "FROM overtime_requests o " +
                    "WHERE o.empEmail = ? " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM notification_tracking nt " +
                    "    WHERE nt.user_email = ? " +
                    "    AND nt.notification_type = 'overtime' " +
                    "    AND nt.reference_id = o.request_id" +
                    ") " +
                    "ORDER BY o.request_id DESC";

            PreparedStatement pstmt = connection.c.prepareStatement(query);
            pstmt.setString(1, userEmail);  // Only for current user
            pstmt.setString(2, userEmail);  // Check if user has seen this notification
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OvertimeInfo overtime = new OvertimeInfo();
                overtime.id = rs.getInt("request_id");
                overtime.date = rs.getDate("overtime_date");
                overtime.hours = rs.getDouble("hours");
                overtime.status = rs.getString("status");
                newOvertimeRequests.add(overtime);
                System.out.println("Found new overtime request for " + userEmail + ": " + overtime.hours + " hours on " + overtime.date + " (ID: " + overtime.id + ")");
            }

            rs.close();
            pstmt.close();
            connection.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error checking for new overtime requests: " + e.getMessage());
        }

        return newOvertimeRequests;
    }

    // Mark notification as seen
    private void markNotificationAsSeen(String notificationType, int referenceId) {
        try {
            conn connection = new conn();
            String query = "INSERT IGNORE INTO notification_tracking (user_email, notification_type, reference_id) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.c.prepareStatement(query);
            pstmt.setString(1, userEmail);
            pstmt.setString(2, notificationType);
            pstmt.setInt(3, referenceId);
            int result = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();

            if (result > 0) {
                System.out.println("Successfully marked " + notificationType + " notification " + referenceId + " as seen for " + userEmail);
            } else {
                System.out.println("Notification " + notificationType + " " + referenceId + " was already marked as seen for " + userEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error marking notification as seen: " + e.getMessage());
        }
    }

    // Show combined notification with mark as seen functionality
    private void showCombinedNotification(List<MeetingInfo> newMeetings, List<OvertimeInfo> newOvertimeRequests) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder notificationText = new StringBuilder("🔔 New: ");

            if (!newMeetings.isEmpty()) {
                notificationText.append(newMeetings.size()).append(" Meeting").append(newMeetings.size() > 1 ? "s" : "");
            }

            if (!newMeetings.isEmpty() && !newOvertimeRequests.isEmpty()) {
                notificationText.append(" & ");
            }

            if (!newOvertimeRequests.isEmpty()) {
                notificationText.append(newOvertimeRequests.size()).append(" Overtime Request").append(newOvertimeRequests.size() > 1 ? "s" : "");
            }

            notificationLabel.setText(notificationText.toString());
            notificationLabel.setVisible(true);

            System.out.println("Notification displayed for " + userEmail + ": " + notificationText);

            // Show detailed notification with mark as seen options
            showDetailedNotificationDialog(newMeetings, newOvertimeRequests);

            // Hide notification after 20 seconds
            Timer hideTimer = new Timer(20000, e -> {
                notificationLabel.setVisible(false);
                System.out.println("Notification hidden for " + userEmail);
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        });
    }

    // Show detailed notification dialog with mark as seen options
    private void showDetailedNotificationDialog(List<MeetingInfo> newMeetings, List<OvertimeInfo> newOvertimeRequests) {
        JDialog dialog = new JDialog(this, "New Notifications", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create notification content
        StringBuilder content = new StringBuilder("<html><body style='font-family: Arial; margin: 10px;'>");
        content.append("<h2 style='color: blue;'>🔔 New Notifications for ").append(userEmail).append(":</h2>");

        if (!newMeetings.isEmpty()) {
            content.append("<h3 style='color: green;'>📅 MEETINGS:</h3>");
            for (MeetingInfo meeting : newMeetings) {
                content.append("<div style='margin: 10px; padding: 10px; background-color: #f0f8ff; border-left: 4px solid #4CAF50;'>");
                content.append("<b>").append(meeting.title).append("</b><br>");
                content.append("Date: ").append(meeting.date).append("<br>");
                content.append("Time: ").append(meeting.time).append("<br>");
                content.append("ID: ").append(meeting.id);
                content.append("</div>");
            }
        }

        if (!newOvertimeRequests.isEmpty()) {
            content.append("<h3 style='color: orange;'>⏰ OVERTIME REQUESTS:</h3>");
            for (OvertimeInfo overtime : newOvertimeRequests) {
                content.append("<div style='margin: 10px; padding: 10px; background-color: #fff8f0; border-left: 4px solid #FF9800;'>");
                content.append("<b>").append(overtime.hours).append(" hours</b><br>");
                content.append("Date: ").append(overtime.date).append("<br>");
                content.append("Status: ").append(overtime.status).append("<br>");
                content.append("ID: ").append(overtime.id);
                content.append("</div>");
            }
        }

        content.append("</body></html>");

        JLabel messageLabel = new JLabel(content.toString());
        JScrollPane scrollPane = new JScrollPane(messageLabel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton markAllSeenBtn = new JButton("Mark All as Seen");
        JButton viewMeetingsBtn = new JButton("View Meetings");
        JButton viewOvertimeBtn = new JButton("View Overtime");
        JButton closeBtn = new JButton("Close");

        // Style buttons
        markAllSeenBtn.setBackground(new Color(76, 175, 80));
        markAllSeenBtn.setForeground(Color.WHITE);
        markAllSeenBtn.setFont(new Font("Arial", Font.BOLD, 12));

        viewMeetingsBtn.setBackground(new Color(33, 150, 243));
        viewMeetingsBtn.setForeground(Color.WHITE);

        viewOvertimeBtn.setBackground(new Color(255, 152, 0));
        viewOvertimeBtn.setForeground(Color.WHITE);

        closeBtn.setBackground(new Color(158, 158, 158));
        closeBtn.setForeground(Color.WHITE);

        markAllSeenBtn.addActionListener(e -> {
            System.out.println("Mark All as Seen clicked for " + userEmail);

            // Mark real notifications as seen in database
            int markedCount = 0;
            for (MeetingInfo meeting : newMeetings) {
                markNotificationAsSeen("meeting", meeting.id);
                markedCount++;
            }
            for (OvertimeInfo overtime : newOvertimeRequests) {
                markNotificationAsSeen("overtime", overtime.id);
                markedCount++;
            }
            JOptionPane.showMessageDialog(dialog,
                    "Successfully marked " + markedCount + " notifications as seen!\n" +
                            "These notifications will not appear again.",
                    "Mark as Seen Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            notificationLabel.setVisible(false);
            dialog.dispose();
        });

        viewMeetingsBtn.addActionListener(e -> {
            dialog.dispose();
            switchToMeetingsSection();
        });

        viewOvertimeBtn.addActionListener(e -> {
            dialog.dispose();
            switchToOvertimeSection();
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(markAllSeenBtn);
        if (!newMeetings.isEmpty()) buttonPanel.add(viewMeetingsBtn);
        if (!newOvertimeRequests.isEmpty()) buttonPanel.add(viewOvertimeBtn);
        buttonPanel.add(closeBtn);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    // Helper method to switch to meetings section
    private void switchToMeetingsSection() {
        contentPanel.removeAll();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("View Meeting", new ViewMeetingPanel());
        contentPanel.add(tabbedPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Helper method to switch to overtime section
    private void switchToOvertimeSection() {
        showOvertimePanel();
    }

    // Inner class to hold meeting information
    private class MeetingInfo {
        int id;
        String title;
        Date date;
        Time time;
    }

    // Inner class to hold overtime information
    private class OvertimeInfo {
        int id;
        Date date;
        double hours;
        String status;
    }

    // Your existing methods (keeping them unchanged)
    private void showChangeViewInformationPanel(String email) {
        // Your existing implementation - keep as is
        String empIDValue = "";
        String firstNameValue = "";
        String lastNameValue = "";
        String addressValue = "";
        String genderValue = "";
        String salaryValue = "";

        try {
            conn connection = new conn();
            String query = "SELECT * FROM employee WHERE email = '" + email + "'";
            ResultSet rs = connection.statement.executeQuery(query);
            if (rs.next()) {
                empIDValue = rs.getString("empID");
                firstNameValue = rs.getString("name");
                lastNameValue = rs.getString("fname");
                addressValue = rs.getString("address");
                genderValue = rs.getString("gender");
                salaryValue = rs.getString("salary");
            }
            connection.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching employee data.");
        }

        // Left: Panel for updating a single field
        JPanel changePanel = new JPanel(null);
        changePanel.setPreferredSize(new Dimension(400, 300));
        changePanel.setBackground(Color.WHITE);
        changePanel.setBorder(BorderFactory.createTitledBorder("Change / View Information"));

        JLabel selectLabel = new JLabel("Select an option:");
        selectLabel.setBounds(20, 40, 120, 25);
        changePanel.add(selectLabel);

        String[] fields = {
                "Please select",
                "Address",
                "Name",
                "Father's Name",
                "Password",
                "Gender"
        };
        JComboBox<String> fieldComboBox = new JComboBox<>(fields);
        fieldComboBox.setBounds(150, 40, 200, 25);
        changePanel.add(fieldComboBox);

        JLabel newValueLabel = new JLabel("Changed to:");
        newValueLabel.setBounds(20, 80, 120, 25);
        changePanel.add(newValueLabel);

        JTextField newValueField = new JTextField();
        newValueField.setBounds(150, 80, 200, 25);
        changePanel.add(newValueField);

        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.setBounds(150, 130, 100, 30);
        changePanel.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            String selectedField = (String) fieldComboBox.getSelectedItem();
            String newValue = newValueField.getText().trim();

            if ("Please select".equals(selectedField)) {
                JOptionPane.showMessageDialog(null, "Please select a valid field to update.");
                return;
            }
            if (newValue.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the new value.");
                return;
            }

            String sqlSnippet = "";
            switch (selectedField) {
                case "Address":
                    sqlSnippet = "address = '" + newValue + "'";
                    break;
                case "Name":
                    sqlSnippet = "name = '" + newValue + "'";
                    break;
                case "Father's Name":
                    sqlSnippet = "fname = '" + newValue + "'";
                    break;
                case "Password":
                    sqlSnippet = "password = '" + newValue + "'";
                    break;
                case "Gender":
                    sqlSnippet = "gender = '" + newValue + "'";
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Field not recognized.");
                    return;
            }

            try {
                conn connection = new conn();
                String updateQuery = "UPDATE employee SET " + sqlSnippet + " WHERE email = '" + email + "'";
                int updated = connection.statement.executeUpdate(updateQuery);
                if (updated > 0) {
                    JOptionPane.showMessageDialog(null, "Updated " + selectedField + " successfully!");
                    showChangeViewInformationPanel(email);
                } else {
                    JOptionPane.showMessageDialog(null, "Update failed!");
                }
                connection.closeConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });

        // Right: Panel to display current employee details
        JPanel viewPanel = new JPanel(null);
        viewPanel.setBackground(Color.WHITE);
        viewPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(20, 30, 100, 25);
        viewPanel.add(idLabel);

        JLabel idValueLabel = new JLabel(empIDValue);
        idValueLabel.setBounds(130, 30, 200, 25);
        viewPanel.add(idValueLabel);

        JLabel fnLabel = new JLabel("Name:");
        fnLabel.setBounds(20, 70, 100, 25);
        viewPanel.add(fnLabel);

        JLabel fnValueLabel = new JLabel(firstNameValue);
        fnValueLabel.setBounds(130, 70, 200, 25);
        viewPanel.add(fnValueLabel);

        JLabel lnLabel = new JLabel("Father's Name:");
        lnLabel.setBounds(20, 110, 100, 25);
        viewPanel.add(lnLabel);

        JLabel lnValueLabel = new JLabel(lastNameValue);
        lnValueLabel.setBounds(130, 110, 200, 25);
        viewPanel.add(lnValueLabel);

        JLabel addrLabel = new JLabel("Address:");
        addrLabel.setBounds(20, 150, 100, 25);
        viewPanel.add(addrLabel);

        JLabel addrValueLabel = new JLabel(addressValue);
        addrValueLabel.setBounds(130, 150, 300, 25);
        viewPanel.add(addrValueLabel);

        JLabel genLabel = new JLabel("Gender:");
        genLabel.setBounds(20, 190, 100, 25);
        viewPanel.add(genLabel);

        JLabel genValueLabel = new JLabel(genderValue);
        genValueLabel.setBounds(130, 190, 200, 25);
        viewPanel.add(genValueLabel);

        JLabel salLabel = new JLabel("Salary:");
        salLabel.setBounds(20, 230, 100, 25);
        viewPanel.add(salLabel);

        JLabel salValueLabel = new JLabel(salaryValue);
        salValueLabel.setBounds(130, 230, 200, 25);
        viewPanel.add(salValueLabel);

        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.setOpaque(false);
        parentPanel.add(changePanel, BorderLayout.WEST);
        parentPanel.add(viewPanel, BorderLayout.CENTER);

        contentPanel.removeAll();
        contentPanel.add(parentPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHolidayPanel() {
        JPanel holidayPanel = new JPanel(new BorderLayout());
        holidayPanel.setBorder(BorderFactory.createTitledBorder("Holiday Requests"));
        holidayPanel.setBackground(Color.WHITE);

        String[] columns = {"Request ID", "Start Date", "End Date", "PDF Path", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);

        try {
            conn connection = new conn();
            String query = "SELECT request_id, start_date, end_date, pdf_path, status " +
                    "FROM holiday_requests WHERE empEmail = '" + userEmail + "'";
            ResultSet rs = connection.statement.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("pdf_path"),
                        rs.getString("status")
                });
            }
            connection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading holiday requests!");
        }

        JScrollPane scrollPane = new JScrollPane(table);
        holidayPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton openPDFBtn = new JButton("Open PDF");
        openPDFBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a holiday request to open its PDF.");
                    return;
                }
                Object pathValue = model.getValueAt(selectedRow, 3);
                if (pathValue == null) {
                    JOptionPane.showMessageDialog(null, "No PDF available for this request.");
                    return;
                }
                String pdfPath = pathValue.toString();
                if (pdfPath.isEmpty() || pdfPath.equalsIgnoreCase("none")) {
                    JOptionPane.showMessageDialog(null, "No PDF available for this request.");
                    return;
                }
                File pdfFile = new File(pdfPath);
                if (!pdfFile.exists()) {
                    JOptionPane.showMessageDialog(null, "File does not exist at: " + pdfFile.getAbsolutePath());
                    return;
                }
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        JOptionPane.showMessageDialog(null, "Desktop API is not supported on your system.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error opening PDF: " + ex.getMessage());
                }
            }
        });

        JButton newRequestButton = new JButton("New Holiday Request");
        newRequestButton.addActionListener(e -> showHolidayRequestPanel());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> showHolidayPanel());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newRequestButton);
        buttonPanel.add(openPDFBtn);

        holidayPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.removeAll();
        contentPanel.add(holidayPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHolidayRequestPanel() {
        JPanel requestPanel = new JPanel(null);
        requestPanel.setPreferredSize(new Dimension(500, 300));
        requestPanel.setBackground(Color.WHITE);
        requestPanel.setBorder(BorderFactory.createTitledBorder("Request Holiday"));

        // Start Date Components with JDateChooser (same style as AddEmployeePanel)
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setBounds(20, 30, 100, 15);
        startLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        startLabel.setForeground(new Color(52, 73, 94)); // Similar to your TEXT_COLOR
        requestPanel.add(startLabel);

        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setBounds(20, 50, 160, 30);
        startDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        startDateChooser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        startDateChooser.setBackground(Color.WHITE);
        startDateChooser.setDateFormatString("yyyy-MM-dd");

        // Set minimum date to today (prevent past dates)
        startDateChooser.setMinSelectableDate(new java.util.Date());

        // Set maximum date to 1 year from now
        java.util.Calendar maxCal = java.util.Calendar.getInstance();
        maxCal.add(java.util.Calendar.YEAR, 1);
        startDateChooser.setMaxSelectableDate(maxCal.getTime());

        // Set default to today
        startDateChooser.setDate(new java.util.Date());
        requestPanel.add(startDateChooser);

        // End Date Components with JDateChooser (same style as AddEmployeePanel)
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setBounds(200, 30, 100, 15);
        endLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        endLabel.setForeground(new Color(52, 73, 94));
        requestPanel.add(endLabel);

        JDateChooser endDateChooser = new JDateChooser();
        endDateChooser.setBounds(200, 50, 160, 30);
        endDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        endDateChooser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        endDateChooser.setBackground(Color.WHITE);
        endDateChooser.setDateFormatString("yyyy-MM-dd");

        // Set minimum date to today
        endDateChooser.setMinSelectableDate(new java.util.Date());

        // Set maximum date to 1 year from now
        endDateChooser.setMaxSelectableDate(maxCal.getTime());

        // Set default to tomorrow
        java.util.Calendar tomorrowCal = java.util.Calendar.getInstance();
        tomorrowCal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        endDateChooser.setDate(tomorrowCal.getTime());
        requestPanel.add(endDateChooser);

        // PDF Components
        JLabel pdfLabel = new JLabel("Selected PDF:");
        pdfLabel.setBounds(20, 90, 100, 15);
        pdfLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pdfLabel.setForeground(new Color(52, 73, 94));
        requestPanel.add(pdfLabel);

        JLabel pdfPathLabel = new JLabel("No file selected");
        pdfPathLabel.setBounds(20, 110, 250, 25);
        pdfPathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        pdfPathLabel.setForeground(new Color(127, 140, 141));
        requestPanel.add(pdfPathLabel);

        JButton selectPdfButton = new JButton("Select PDF");
        selectPdfButton.setBounds(280, 110, 120, 30);
        selectPdfButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        selectPdfButton.setBackground(new Color(52, 152, 219));
        selectPdfButton.setForeground(Color.WHITE);
        selectPdfButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        selectPdfButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        requestPanel.add(selectPdfButton);

        // Action Buttons
        JButton backButton = new JButton("Back");
        backButton.setBounds(150, 180, 100, 35);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> showHolidayPanel());
        requestPanel.add(backButton);

        JButton submitButton = new JButton("Submit Request");
        submitButton.setBounds(260, 180, 120, 35);
        submitButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        requestPanel.add(submitButton);

        // PDF Selection Handler
        selectPdfButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF files", "pdf"));
            fileChooser.setDialogTitle("Select Holiday Request PDF");

            int result = fileChooser.showOpenDialog(requestPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                    pdfPathLabel.setText(selectedFile.getName()); // Show only filename for better UI
                    pdfPathLabel.setToolTipText(selectedFile.getAbsolutePath()); // Full path in tooltip
                    pdfPathLabel.setForeground(new Color(39, 174, 96)); // Green color for selected file
                } else {
                    JOptionPane.showMessageDialog(requestPanel,
                            "Please select a valid PDF file",
                            "Invalid File Type",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Submit Handler with JDateChooser (same logic as your AddEmployeePanel)
        submitButton.addActionListener(e -> {
            try {
                // Get dates from JDateChooser components
                java.util.Date startUtilDate = startDateChooser.getDate();
                java.util.Date endUtilDate = endDateChooser.getDate();

                // Validation (similar to your AddEmployeePanel validation style)
                if (startUtilDate == null) {
                    JOptionPane.showMessageDialog(requestPanel,
                            "Please select a start date.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (endUtilDate == null) {
                    JOptionPane.showMessageDialog(requestPanel,
                            "Please select an end date.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convert util.Date to LocalDate for validation
                LocalDate startDate = startUtilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = endUtilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate today = LocalDate.now();

                // Validate dates
                if (startDate.isBefore(today)) {
                    JOptionPane.showMessageDialog(requestPanel,
                            "Start date cannot be in the past!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(requestPanel,
                            "End date cannot be before start date!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both days
                if (daysBetween < 2) {
                    JOptionPane.showMessageDialog(requestPanel,
                            "Minimum 2 days required for holiday request!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String pdfPath = pdfPathLabel.getToolTipText(); // Get full path from tooltip
                if (pdfPath == null || pdfPathLabel.getText().equals("No file selected")) {
                    JOptionPane.showMessageDialog(requestPanel,
                            "Please select a PDF file!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Disable submit button during processing (like in AddEmployeePanel)
                submitButton.setEnabled(false);
                submitButton.setText("Submitting...");

                // Process in background thread (similar to your AddEmployeePanel SwingWorker pattern)
                SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
                    private String errorMessage = "";

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {
                            // Store PDF
                            Path source = Paths.get(pdfPath);
                            Path targetDir = Paths.get("holiday_docs");
                            if (!Files.exists(targetDir)) {
                                Files.createDirectories(targetDir);
                            }

                            // Create unique filename to avoid conflicts
                            String timestamp = String.valueOf(System.currentTimeMillis());
                            String fileName = "holiday_" + userEmail.replace("@", "_").replace(".", "_") + "_" + timestamp + ".pdf";
                            Path target = targetDir.resolve(fileName);
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

                            // Convert LocalDate to SQL Date for database
                            Date sqlStartDate = Date.valueOf(startDate);
                            Date sqlEndDate = Date.valueOf(endDate);

                            conn connection = new conn();
                            String absolutePath = target.toAbsolutePath().toString();
                            String insertQuery = "INSERT INTO holiday_requests (empEmail, start_date, end_date, pdf_path, status, request_date, days_requested) " +
                                    "VALUES (?, ?, ?, ?, 'Pending', NOW(), ?)";

                            PreparedStatement pstmt = connection.c.prepareStatement(insertQuery);
                            pstmt.setString(1, userEmail);
                            pstmt.setDate(2, sqlStartDate);
                            pstmt.setDate(3, sqlEndDate);
                            pstmt.setString(4, absolutePath);
                            pstmt.setLong(5, daysBetween);

                            int result = pstmt.executeUpdate();
                            pstmt.close();
                            connection.closeConnection();

                            return result > 0;

                        } catch (Exception ex) {
                            errorMessage = ex.getMessage();
                            ex.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit Request");

                        try {
                            if (get()) {
                                JOptionPane.showMessageDialog(requestPanel,
                                        "Holiday request submitted successfully!\n" +
                                                "Start Date: " + startDate + "\n" +
                                                "End Date: " + endDate + "\n" +
                                                "Duration: " + daysBetween + " days\n" +
                                                "Status: Pending Review",
                                        "Request Submitted",
                                        JOptionPane.INFORMATION_MESSAGE);
                                showHolidayPanel(); // Go back to main holiday panel
                            } else {
                                String message = errorMessage.isEmpty() ? "Failed to submit holiday request." : errorMessage;
                                JOptionPane.showMessageDialog(requestPanel,
                                        "Error submitting request: " + message,
                                        "Submission Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(requestPanel,
                                    "Unexpected error: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(requestPanel,
                        "Unexpected error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

                // Re-enable submit button on error
                submitButton.setEnabled(true);
                submitButton.setText("Submit Request");
            }
        });

        contentPanel.removeAll();
        contentPanel.add(requestPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOvertimePanel() {
        JPanel overtimePanel = new JPanel(new BorderLayout());
        overtimePanel.setBorder(BorderFactory.createTitledBorder("Overtime Requests"));
        overtimePanel.setBackground(Color.WHITE);

        String[] columns = {"Request ID", "Overtime Date", "Hours", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);

        try {
            conn connection = new conn();
            String query = "SELECT request_id, overtime_date, hours, status FROM overtime_requests WHERE empEmail = '" + userEmail + "'";
            ResultSet rs = connection.statement.executeQuery(query);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getDate("overtime_date"),
                        rs.getDouble("hours"),
                        rs.getString("status")
                });
            }
            connection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading overtime requests!");
        }

        JScrollPane scrollPane = new JScrollPane(table);
        overtimePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton acceptButton = new JButton("Accept Overtime");
        JButton declineButton = new JButton("Decline Overtime");
        JButton refreshButton = new JButton("Refresh");

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow < 0) {
                    JOptionPane.showMessageDialog(null, "Please select an overtime request to accept.");
                    return;
                }
                String status = model.getValueAt(selectedRow, 3).toString();
                if(!status.equalsIgnoreCase("Pending")) {
                    JOptionPane.showMessageDialog(null, "Only pending requests can be accepted.");
                    return;
                }
                int requestId = (int) model.getValueAt(selectedRow, 0);
                try {
                    conn connection = new conn();
                    String updateQuery = "UPDATE overtime_requests SET status = 'Accepted' WHERE request_id = " + requestId;
                    int updated = connection.statement.executeUpdate(updateQuery);
                    if(updated > 0) {
                        JOptionPane.showMessageDialog(null, "Overtime request accepted.");
                        showOvertimePanel(); // refresh the panel
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update request.");
                    }
                    connection.closeConnection();
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow < 0) {
                    JOptionPane.showMessageDialog(null, "Please select an overtime request to decline.");
                    return;
                }
                String status = model.getValueAt(selectedRow, 3).toString();
                if(!status.equalsIgnoreCase("Pending")) {
                    JOptionPane.showMessageDialog(null, "Only pending requests can be declined.");
                    return;
                }
                int requestId = (int) model.getValueAt(selectedRow, 0);
                try {
                    conn connection = new conn();
                    String updateQuery = "UPDATE overtime_requests SET status = 'Declined' WHERE request_id = " + requestId;
                    int updated = connection.statement.executeUpdate(updateQuery);
                    if(updated > 0) {
                        JOptionPane.showMessageDialog(null, "Overtime request declined.");
                        showOvertimePanel(); // refresh the panel
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update request.");
                    }
                    connection.closeConnection();
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showOvertimePanel();
            }
        });

        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);
        buttonPanel.add(refreshButton);
        overtimePanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.removeAll();
        contentPanel.add(overtimePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    class EmployeeMenuActionListener implements ActionListener {
        private final String option;

        public EmployeeMenuActionListener(String option) {
            this.option = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("Log Out".equals(option)) {
                dispose();
                new LoginSelection();
                return;
            }

            if ("Check New Updates".equals(option)) {
                checkForRealNotifications(); // Manual check for real notifications
                return;
            }

            contentPanel.removeAll();
            switch (option) {
                case "View/Change Information":
                    showChangeViewInformationPanel(userEmail);
                    break;
                case "View/Change Holidays":
                    showHolidayPanel();
                    break;
                case "View/Change Meetings":
                    JTabbedPane tabbedPane = new JTabbedPane();
                    tabbedPane.addTab("View Meeting", new ViewMeetingPanel());
                    contentPanel.add(tabbedPane);
                    break;
                case "View/Change Overtime":
                    showOvertimePanel();
                    break;
                default:
                    JLabel placeholder = new JLabel(option + " Page (Under Construction)", SwingConstants.CENTER);
                    placeholder.setFont(new Font("Raleway", Font.BOLD, 20));
                    contentPanel.add(placeholder, BorderLayout.CENTER);
                    break;
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    class conn {
        Connection c;
        Statement statement;

        public conn() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement",
                        "root",
                        "password");
                statement = c.createStatement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void closeConnection() {
            try {
                if (statement != null) statement.close();
                if (c != null) c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Employee_class("shishirdune2022@gmail.com"));
    }
}