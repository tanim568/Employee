package employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main_class extends JFrame {
    private JPanel contentPanel;
    private JLabel img;
    private JButton holidayButton;
    private JLabel notificationLabel;
    private Timer notificationTimer;
    private int unreadNotifications = 0;

    public Main_class() {
        // Load and scale the background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/home.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1120, 630, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        img = new JLabel(i3);
        img.setBounds(0, 0, 1120, 630);
        add(img);

        // Create header panel with notification area
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBounds(220, 10, 880, 60);
        headerPanel.setOpaque(false);

        // Heading Label
        JLabel heading = new JLabel("Employee Management System", SwingConstants.CENTER);
        heading.setFont(new Font("Raleway", Font.BOLD, 25));
        heading.setForeground(Color.BLACK);
        headerPanel.add(heading, BorderLayout.CENTER);

        // Notification area
        notificationLabel = new JLabel("", SwingConstants.CENTER);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notificationLabel.setForeground(Color.RED);
        notificationLabel.setBackground(new Color(255, 255, 200));
        notificationLabel.setOpaque(true);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationLabel.setVisible(false);
        headerPanel.add(notificationLabel, BorderLayout.SOUTH);

        img.add(headerPanel);

        // HR MENU PANEL (Left Side)
        JPanel hrPanel = new JPanel();
        hrPanel.setBounds(10, 60, 200, 500);
        hrPanel.setBackground(new Color(50, 50, 50));
        hrPanel.setLayout(null);
        img.add(hrPanel);

        JLabel hrMenuLabel = new JLabel("HR MENU");
        hrMenuLabel.setBounds(50, 10, 100, 30);
        hrMenuLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        hrMenuLabel.setForeground(Color.WHITE);
        hrPanel.add(hrMenuLabel);

        // Right Panel (Content area)
        contentPanel = new JPanel();
        contentPanel.setBounds(220, 80, 870, 480);
        contentPanel.setBackground(new Color(230, 230, 230));
        contentPanel.setLayout(new CardLayout());
        contentPanel.setVisible(false);
        img.add(contentPanel);

        // HR Menu Buttons
        createHRMenuButtons(hrPanel);

        // Start notification checking
        startNotificationChecker();

        setSize(1120, 630);
        setLocation(250, 100);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createHRMenuButtons(JPanel hrPanel) {
        String[] hrOptions = {"Employee Search/Edit", "Add/Remove Employee", "View/Change Holidays",
                "View/Change Meetings", "View/Change Overtime", "Check New Updates", "Log Out"};

        int yOffset = 50;
        for (String option : hrOptions) {
            JButton btn = new JButton(option);
            btn.setBounds(10, yOffset, 180, 40);
            btn.setBackground(new Color(150, 150, 150));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Raleway", Font.PLAIN, 14));
            btn.setFocusPainted(false);

            if (option.equals("View/Change Holidays")) {
                holidayButton = btn;
                updateHolidayButtonText();
            }

            hrPanel.add(btn);
            btn.addActionListener(new HRMenuActionListener(option));
            yOffset += 60;
        }
    }

    private void startNotificationChecker() {
        notificationTimer = new Timer(true);
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> checkForNewNotifications());
            }
        }, 0, 30000);
    }

    private void checkForNewNotifications() {
        try {
            conn connection = new conn();

            // First, ensure all holiday requests have tracking records
            String ensureTrackingQuery = """
                INSERT IGNORE INTO hr_notification_tracking (notification_type, reference_id, is_read) 
                SELECT 'holiday_request', request_id, FALSE 
                FROM holiday_requests hr 
                WHERE NOT EXISTS (
                    SELECT 1 FROM hr_notification_tracking nt 
                    WHERE nt.notification_type = 'holiday_request' 
                    AND nt.reference_id = hr.request_id
                )
                """;
            connection.statement.executeUpdate(ensureTrackingQuery);

            // Now count unread notifications
            String query = "SELECT COUNT(*) as unread_count FROM hr_notification_tracking WHERE is_read = FALSE AND notification_type = 'holiday_request'";
            ResultSet rs = connection.statement.executeQuery(query);

            if (rs.next()) {
                int newUnreadCount = rs.getInt("unread_count");
                if (newUnreadCount != unreadNotifications) {
                    unreadNotifications = newUnreadCount;
                    updateHolidayButtonText();

                    System.out.println("Updated notification count: " + unreadNotifications);
                }
            }

            connection.closeConnection();
        } catch (Exception e) {
            System.err.println("Error checking notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Fixed manual check method - now looks for unread notifications instead of untracked ones
    private void checkForRealHolidayNotifications() {
        System.out.println("Manually checking for unread holiday requests...");

        List<HolidayRequestInfo> unreadRequests = checkForUnreadHolidayRequests();

        if (!unreadRequests.isEmpty()) {
            showHolidayNotification(unreadRequests);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No unread holiday requests found.",
                    "Check Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Updated method to find unread requests (not just untracked ones)
    private List<HolidayRequestInfo> checkForUnreadHolidayRequests() {
        List<HolidayRequestInfo> unreadRequests = new ArrayList<>();

        try {
            conn connection = new conn();

            // First ensure all requests have tracking records
            String ensureTrackingQuery = """
                INSERT IGNORE INTO hr_notification_tracking (notification_type, reference_id, is_read) 
                SELECT 'holiday_request', request_id, FALSE 
                FROM holiday_requests hr 
                WHERE NOT EXISTS (
                    SELECT 1 FROM hr_notification_tracking nt 
                    WHERE nt.notification_type = 'holiday_request' 
                    AND nt.reference_id = hr.request_id
                )
                """;
            connection.statement.executeUpdate(ensureTrackingQuery);

            // Now get all unread requests
            String query = """
                SELECT hr.request_id, hr.empEmail, hr.start_date, hr.end_date, hr.status, hr.pdf_path 
                FROM holiday_requests hr 
                INNER JOIN hr_notification_tracking nt ON hr.request_id = nt.reference_id 
                WHERE nt.notification_type = 'holiday_request' 
                AND nt.is_read = FALSE 
                ORDER BY hr.request_id DESC
                """;

            ResultSet rs = connection.statement.executeQuery(query);

            while (rs.next()) {
                HolidayRequestInfo request = new HolidayRequestInfo();
                request.id = rs.getInt("request_id");
                request.empEmail = rs.getString("empEmail");
                request.startDate = rs.getDate("start_date");
                request.endDate = rs.getDate("end_date");
                request.status = rs.getString("status");
                request.pdfPath = rs.getString("pdf_path");
                unreadRequests.add(request);
                System.out.println("Found unread holiday request: ID " + request.id + " from " + request.empEmail);
            }

            rs.close();
            connection.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error checking for unread holiday requests: " + e.getMessage());
        }

        return unreadRequests;
    }

    private void markNotificationAsSeen(String notificationType, int referenceId) {
        try {
            conn connection = new conn();
            String query = "UPDATE hr_notification_tracking SET is_read = TRUE, read_at = NOW() WHERE notification_type = ? AND reference_id = ?";
            PreparedStatement pstmt = connection.c.prepareStatement(query);
            pstmt.setString(1, notificationType);
            pstmt.setInt(2, referenceId);
            int result = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();

            if (result > 0) {
                System.out.println("Successfully marked " + notificationType + " notification " + referenceId + " as seen");
            } else {
                System.out.println("Notification " + notificationType + " " + referenceId + " was already marked as seen or doesn't exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error marking notification as seen: " + e.getMessage());
        }
    }

    private void updateHolidayButtonText() {
        if (holidayButton != null) {
            String baseText = "View/Change Holidays";
            if (unreadNotifications > 0) {
                holidayButton.setText("<html>" + baseText + " <span style='color:red;font-weight:bold;'>(" + unreadNotifications + ")</span></html>");
                holidayButton.setBackground(new Color(180, 100, 100));
            } else {
                holidayButton.setText(baseText);
                holidayButton.setBackground(new Color(150, 150, 150));
            }
        }
    }

    private void showHolidayNotification(List<HolidayRequestInfo> requests) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder notificationText = new StringBuilder("🔔 Unread: ");
            notificationText.append(requests.size()).append(" Holiday Request").append(requests.size() > 1 ? "s" : "");

            notificationLabel.setText(notificationText.toString());
            notificationLabel.setVisible(true);

            System.out.println("Holiday notification displayed: " + notificationText);

            showDetailedHolidayNotificationDialog(requests);

            Timer hideTimer = new Timer();
            hideTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        notificationLabel.setVisible(false);
                        System.out.println("Holiday notification hidden");
                    });
                }
            }, 20000);
        });
    }

    private void showDetailedHolidayNotificationDialog(List<HolidayRequestInfo> requests) {
        JDialog dialog = new JDialog(this, "Unread Holiday Requests", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout());

        StringBuilder content = new StringBuilder("<html><body style='font-family: Arial; margin: 10px;'>");
        content.append("<h2 style='color: blue;'>🔔 Unread Holiday Requests (").append(requests.size()).append("):</h2>");

        for (HolidayRequestInfo request : requests) {
            content.append("<div style='margin: 10px; padding: 15px; background-color: #f0f8ff; border-left: 4px solid #4CAF50; border-radius: 5px;'>");
            content.append("<h3 style='color: #2E7D32; margin: 0 0 10px 0;'>Request ID: ").append(request.id).append("</h3>");
            content.append("<b>Employee:</b> ").append(request.empEmail).append("<br>");
            content.append("<b>Start Date:</b> ").append(request.startDate).append("<br>");
            content.append("<b>End Date:</b> ").append(request.endDate).append("<br>");
            content.append("<b>Status:</b> <span style='color: orange; font-weight: bold;'>").append(request.status).append("</span><br>");
            if (request.pdfPath != null && !request.pdfPath.isEmpty() && !request.pdfPath.equalsIgnoreCase("none")) {
                content.append("<b>PDF:</b> Available<br>");
            } else {
                content.append("<b>PDF:</b> Not provided<br>");
            }
            content.append("</div>");
        }

        content.append("</body></html>");

        JLabel messageLabel = new JLabel(content.toString());
        JScrollPane scrollPane = new JScrollPane(messageLabel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton markAllSeenBtn = new JButton("Mark All as Read");
        JButton viewHolidaysBtn = new JButton("View Holiday Requests");
        JButton closeBtn = new JButton("Close");

        markAllSeenBtn.setBackground(new Color(76, 175, 80));
        markAllSeenBtn.setForeground(Color.WHITE);
        markAllSeenBtn.setFont(new Font("Arial", Font.BOLD, 12));

        viewHolidaysBtn.setBackground(new Color(33, 150, 243));
        viewHolidaysBtn.setForeground(Color.WHITE);

        closeBtn.setBackground(new Color(158, 158, 158));
        closeBtn.setForeground(Color.WHITE);

        markAllSeenBtn.addActionListener(e -> {
            System.out.println("Mark All Holiday Requests as Read clicked");

            int markedCount = 0;
            for (HolidayRequestInfo request : requests) {
                markNotificationAsSeen("holiday_request", request.id);
                markedCount++;
            }

            unreadNotifications = Math.max(0, unreadNotifications - markedCount);
            updateHolidayButtonText();

            JOptionPane.showMessageDialog(dialog,
                    "Successfully marked " + markedCount + " holiday request notifications as read!\n" +
                            "These notifications will not appear as unread again.",
                    "Mark as Read Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            notificationLabel.setVisible(false);
            dialog.dispose();
        });

        viewHolidaysBtn.addActionListener(e -> {
            dialog.dispose();
            switchToHolidaysSection();
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(markAllSeenBtn);
        buttonPanel.add(viewHolidaysBtn);
        buttonPanel.add(closeBtn);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void switchToHolidaysSection() {
        contentPanel.removeAll();
        contentPanel.add(new HolidayManagementPanel(Main_class.this));
        contentPanel.setVisible(true);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class HolidayRequestInfo {
        int id;
        String empEmail;
        Date startDate;
        Date endDate;
        String status;
        String pdfPath;
    }

    private class HRMenuActionListener implements ActionListener {
        private final String option;

        public HRMenuActionListener(String option) {
            this.option = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            contentPanel.removeAll();

            if (option.equals("Log Out")) {
                if (notificationTimer != null) {
                    notificationTimer.cancel();
                }
                Main_class.this.dispose();
                new LoginSelection();
                return;
            } else if (option.equals("Check New Updates")) {
                checkForRealHolidayNotifications(); // This now checks for unread notifications
                return;
            } else if (option.equals("Add/Remove Employee")) {
                JPanel addRemovePanel = new JPanel();
                addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 200));

                JButton addBtn = new JButton("Add Employee");
                JButton removeBtn = new JButton("Remove Employee");

                styleButton(addBtn);
                styleButton(removeBtn);

                addBtn.addActionListener(event -> {
                    contentPanel.removeAll();
                    contentPanel.add(new AddEmployeePanel());
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });
                removeBtn.addActionListener(event -> {
                    contentPanel.removeAll();
                    contentPanel.add(new RemoveEmployeePanel());
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });

                addRemovePanel.add(addBtn);
                addRemovePanel.add(removeBtn);
                contentPanel.removeAll();
                contentPanel.add(addRemovePanel);
            }
            else if (option.equals("Employee Search/Edit")) {
                contentPanel.removeAll();
                contentPanel.add(new EmployeeSearch(), BorderLayout.CENTER);
                contentPanel.setVisible(true);
                contentPanel.revalidate();
                contentPanel.repaint();
            }
            else if (option.equals("View/Change Overtime")) {
                contentPanel.add(new OvertimePanel());
            } else if (option.equals("View/Change Meetings")) {
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("View Meeting", new ViewMeetingPanel());
                tabbedPane.addTab("Update Meeting", new UpdateMeetingPanel());
                contentPanel.add(tabbedPane);
            }
            else if (option.equals("View/Change Holidays")) {
                markHolidayNotificationsAsRead();
                contentPanel.add(new HolidayManagementPanel(Main_class.this));
            } else {
                JLabel placeholder = new JLabel(option + " Page", SwingConstants.CENTER);
                placeholder.setFont(new Font("Raleway", Font.BOLD, 20));
                contentPanel.add(placeholder);
            }

            contentPanel.setVisible(true);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void markHolidayNotificationsAsRead() {
        try {
            conn connection = new conn();
            String updateQuery = "UPDATE hr_notification_tracking SET is_read = TRUE, read_at = NOW() WHERE is_read = FALSE AND notification_type = 'holiday_request'";
            int updated = connection.statement.executeUpdate(updateQuery);

            unreadNotifications = 0;
            updateHolidayButtonText();

            System.out.println("Marked " + updated + " holiday notifications as read when opening holiday panel");

            connection.closeConnection();
        } catch (Exception e) {
            System.err.println("Error marking holiday notifications as read: " + e.getMessage());
        }
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Sans Serif", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    class conn {
        Connection c;
        Statement statement;

        public conn() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                c = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagement", "root", "password");
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

    public JPanel getContentPanel() {
        return contentPanel;
    }

    @Override
    public void dispose() {
        if (notificationTimer != null) {
            notificationTimer.cancel();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        new Main_class();
    }
}