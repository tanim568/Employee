package employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class HolidayManagementPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;
    private final Main_class mainClass;

    public HolidayManagementPanel(Main_class mainClass) {
        this.mainClass = mainClass;
        setLayout(new BorderLayout());
        setBackground(new Color(230, 230, 230));
        setBorder(BorderFactory.createTitledBorder("Holiday Requests"));

        // Table setup with multiple selection
        String[] columns = {"Request ID", "Employee Email", "Start Date", "End Date", "PDF Path", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadHolidayRequests();
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private void loadHolidayRequests() {
        try {
            Main_class.conn connection = mainClass.new conn();
            ResultSet rs = connection.statement.executeQuery(
                    "SELECT request_id, empEmail, start_date, end_date, pdf_path, status FROM holiday_requests"
            );
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("request_id"),
                        rs.getString("empEmail"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("pdf_path"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading holiday requests.");
        }
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton openPDFBtn = new JButton("Open PDF");
        JButton acceptBtn = new JButton("Accept");
        JButton declineBtn = new JButton("Decline");

        styleButton(openPDFBtn);
        styleButton(acceptBtn);
        styleButton(declineBtn);

        openPDFBtn.addActionListener(this::openPdfAction);
        acceptBtn.addActionListener(e -> processRequests("Approved"));
        declineBtn.addActionListener(e -> processRequests("Declined"));

        panel.add(openPDFBtn);
        panel.add(acceptBtn);
        panel.add(declineBtn);
        return panel;
    }

    private void processRequests(String newStatus) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one request.");
            return;
        }

        int successCount = 0;
        try {
            Main_class.conn connection = mainClass.new conn();
            for (int viewRow : selectedRows) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                String requestId = model.getValueAt(modelRow, 0).toString();

                String query = "UPDATE holiday_requests SET status = '" + newStatus +
                        "' WHERE request_id = " + requestId;
                if (connection.statement.executeUpdate(query) > 0) {
                    model.setValueAt(newStatus, modelRow, 5);
                    successCount++;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Successfully processed " + successCount + " out of " +
                            selectedRows.length + " requests!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openPdfAction(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one request.");
            return;
        }

        for (int viewRow : selectedRows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String pdfPath = model.getValueAt(modelRow, 4).toString();

            if (pdfPath.isEmpty() || pdfPath.equalsIgnoreCase("none")) {
                JOptionPane.showMessageDialog(this, "No PDF available for selected request.");
                continue;
            }

            try {
                File pdfFile = new File(pdfPath);
                if (!pdfFile.exists()) {
                    JOptionPane.showMessageDialog(this, "File not found: " + pdfFile.getAbsolutePath());
                    continue;
                }

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    JOptionPane.showMessageDialog(this, "PDF viewing not supported on this system.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening PDF: " + ex.getMessage());
            }
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Sans Serif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
    }
}