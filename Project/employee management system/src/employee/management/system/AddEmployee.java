package employee.management.system;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;
import java.util.Random;
import java.util.regex.Pattern;

class AddEmployeePanel extends JPanel implements ActionListener {
    Random ran = new Random();
    int number = ran.nextInt(999999);

    // Form components
    private JTextField tname, tfname, taddress, tphone, temail, tsalary;
    private JLabel tempid;
    private JDateChooser tdob;
    private JButton add, back, clear, generate;
    private JComboBox<String> Boxeducation, Boxgender, Boxdepartment;

    // Validation labels
    private JLabel nameValidation, emailValidation, phoneValidation, salaryValidation;

    // Scroll pane
    private JScrollPane scrollPane;

    // Connection pool or reusable connection
    private static Connection sharedConnection = null;

    public AddEmployeePanel() {
        initializeComponents();
        addValidationListeners();
        // Initialize connection pool
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            if (sharedConnection == null || sharedConnection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                sharedConnection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/employeemanagement?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                        "root",
                        "password"
                );
                // Set connection properties for better performance
                sharedConnection.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.Colors.BACKGROUND_COLOR);

        // Create the main content panel that will be scrollable
        JPanel mainContentPanel = createMainContentPanel();

        // Create scroll pane
        scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(UIUtils.Colors.BACKGROUND_COLOR);
        contentPanel.setPreferredSize(new Dimension(870, 720));

        // Title Panel
        createTitlePanel(contentPanel);
        // Main Form Panel
        createMainFormPanel(contentPanel);
        // Action Buttons Panel
        createActionButtonsPanel(contentPanel);

        return contentPanel;
    }

    // [Include all your existing UI creation methods here - createTitlePanel, createMainFormPanel, etc.]
    // I'm keeping them the same for brevity, but include all the methods from your previous code

    private void createTitlePanel(JPanel parent) {
        JPanel titlePanel = new JPanel();
        titlePanel.setBounds(10, 10, 850, 70);
        titlePanel.setBackground(UIUtils.Colors.PRIMARY_COLOR);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(UIUtils.createRoundedBorder(10));

        JLabel heading = new JLabel("Add New Employee", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(Color.WHITE);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        titlePanel.add(heading, BorderLayout.CENTER);
        parent.add(titlePanel);
    }

    private void createMainFormPanel(JPanel parent) {
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(10, 90, 850, 520);
        mainPanel.setBackground(UIUtils.Colors.PANEL_COLOR);
        mainPanel.setLayout(null);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                UIUtils.createRoundedBorder(10),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        createEmployeeIdSection(mainPanel);
        createPersonalInfoSection(mainPanel);
        createContactInfoSection(mainPanel);
        createProfessionalInfoSection(mainPanel);

        parent.add(mainPanel);
    }

    private void createEmployeeIdSection(JPanel parent) {
        JPanel empIdPanel = new JPanel();
        empIdPanel.setBounds(15, 15, 820, 65);
        empIdPanel.setBackground(new Color(52, 152, 219, 20));
        empIdPanel.setLayout(null);
        empIdPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.Colors.PRIMARY_COLOR, 2),
                "Employee ID",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                UIUtils.Colors.PRIMARY_COLOR
        ));

        JLabel empid = new JLabel("Employee ID:");
        empid.setBounds(15, 30, 100, 20);
        empid.setFont(new Font("Segoe UI", Font.BOLD, 12));
        empid.setForeground(UIUtils.Colors.TEXT_COLOR);
        empIdPanel.add(empid);

        tempid = new JLabel("EMP" + String.format("%06d", number));
        tempid.setBounds(120, 30, 150, 20);
        tempid.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tempid.setForeground(UIUtils.Colors.PRIMARY_COLOR);
        empIdPanel.add(tempid);

        generate = UIUtils.createStyledButton("Generate New ID", UIUtils.Colors.INFO_COLOR);
        generate.setBounds(300, 25, 140, 30);
        generate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        generate.addActionListener(e -> {
            number = ran.nextInt(999999);
            tempid.setText("EMP" + String.format("%06d", number));
        });
        empIdPanel.add(generate);

        parent.add(empIdPanel);
    }

    private void createPersonalInfoSection(JPanel parent) {
        JPanel personalPanel = new JPanel();
        personalPanel.setBounds(15, 95, 820, 130);
        personalPanel.setBackground(UIUtils.Colors.PANEL_COLOR);
        personalPanel.setLayout(null);
        personalPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.Colors.SUCCESS_COLOR, 2),
                "Personal Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                UIUtils.Colors.SUCCESS_COLOR
        ));

        addFormField(personalPanel, "Full Name:", 15, 25, tname = createStyledTextField(), nameValidation = createValidationLabel());
        addFormField(personalPanel, "Father's Name:", 280, 25, tfname = createStyledTextField(), null);
        addFormField(personalPanel, "Date of Birth:", 545, 25, null, null);

        tdob = new JDateChooser();
        tdob.setBounds(545, 45, 160, 25);
        tdob.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tdob.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        personalPanel.add(tdob);

        addFormField(personalPanel, "Gender:", 15, 85, null, null);
        String[] genders = {"Male", "Female", "Other"};
        Boxgender = createStyledComboBox(genders);
        Boxgender.setBounds(90, 85, 100, 25);
        personalPanel.add(Boxgender);

        parent.add(personalPanel);
    }

    private void createContactInfoSection(JPanel parent) {
        JPanel contactPanel = new JPanel();
        contactPanel.setBounds(15, 240, 820, 100);
        contactPanel.setBackground(UIUtils.Colors.PANEL_COLOR);
        contactPanel.setLayout(null);
        contactPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
                "Contact Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(155, 89, 182)
        ));

        addFormField(contactPanel, "Phone:", 15, 25, tphone = createStyledTextField(), phoneValidation = createValidationLabel());
        addFormField(contactPanel, "Email:", 280, 25, temail = createStyledTextField(), emailValidation = createValidationLabel());
        addFormField(contactPanel, "Address:", 545, 25, taddress = createStyledTextField(), null);

        parent.add(contactPanel);
    }

    private void createProfessionalInfoSection(JPanel parent) {
        JPanel professionalPanel = new JPanel();
        professionalPanel.setBounds(15, 355, 820, 100);
        professionalPanel.setBackground(UIUtils.Colors.PANEL_COLOR);
        professionalPanel.setLayout(null);
        professionalPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.Colors.WARNING_COLOR, 2),
                "Professional Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                UIUtils.Colors.WARNING_COLOR
        ));

        addFormField(professionalPanel, "Department:", 15, 25, null, null);
        String[] departments = {"IT", "HR", "Finance", "Marketing", "Operations", "Sales"};
        Boxdepartment = createStyledComboBox(departments);
        Boxdepartment.setBounds(100, 25, 120, 25);
        professionalPanel.add(Boxdepartment);

        addFormField(professionalPanel, "Education:", 280, 25, null, null);
        String[] educations = {"High School", "Bachelor's", "Master's", "PhD", "Diploma", "Certificate"};
        Boxeducation = createStyledComboBox(educations);
        Boxeducation.setBounds(355, 25, 120, 25);
        professionalPanel.add(Boxeducation);

        addFormField(professionalPanel, "Salary:", 545, 25, tsalary = createStyledTextField(), salaryValidation = createValidationLabel());

        parent.add(professionalPanel);
    }

    private void createActionButtonsPanel(JPanel parent) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(10, 630, 850, 70);
        buttonPanel.setBackground(UIUtils.Colors.BACKGROUND_COLOR);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        add = UIUtils.createStyledButton("Add Employee", UIUtils.Colors.SUCCESS_COLOR);
        add.setPreferredSize(new Dimension(150, 40));
        add.addActionListener(this);

        clear = UIUtils.createStyledButton("Clear Form", UIUtils.Colors.INFO_COLOR);
        clear.setPreferredSize(new Dimension(150, 40));
        clear.addActionListener(this);

        back = UIUtils.createStyledButton("Back", new Color(149, 165, 166));
        back.setPreferredSize(new Dimension(150, 40));
        back.addActionListener(this);

        buttonPanel.add(add);
        buttonPanel.add(clear);
        buttonPanel.add(back);

        parent.add(buttonPanel);
    }

    private void addFormField(JPanel parent, String labelText, int x, int y, JTextField textField, JLabel validationLabel) {
        JLabel label = new JLabel(labelText);
        label.setBounds(x, y, 100, 15);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(UIUtils.Colors.TEXT_COLOR);
        parent.add(label);

        if (textField != null) {
            textField.setBounds(x, y + 20, 160, 30);
            parent.add(textField);
        }

        if (validationLabel != null) {
            validationLabel.setBounds(x, y + 52, 160, 12);
            parent.add(validationLabel);
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private JLabel createValidationLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        label.setForeground(UIUtils.Colors.ERROR_COLOR);
        return label;
    }

    // Include all your validation methods here...
    private void addValidationListeners() {
        tname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateName();
            }
        });

        temail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });

        tphone.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePhone();
            }
        });

        tsalary.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateSalary();
            }
        });
    }

    private boolean validateName() {
        String name = tname.getText().trim();
        if (name.isEmpty()) {
            nameValidation.setText("Name is required");
            return false;
        } else if (name.length() < 2) {
            nameValidation.setText("Name too short");
            return false;
        } else {
            nameValidation.setText("");
            return true;
        }
    }

    private boolean validateEmail() {
        String email = temail.getText().trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (email.isEmpty()) {
            emailValidation.setText("Email is required");
            return false;
        } else if (!Pattern.matches(emailRegex, email)) {
            emailValidation.setText("Invalid email format");
            return false;
        } else {
            emailValidation.setText("");
            return true;
        }
    }

    private boolean validatePhone() {
        String phone = tphone.getText().trim();
        String phoneRegex = "^[0-9]{10,15}$";
        if (phone.isEmpty()) {
            phoneValidation.setText("Phone is required");
            return false;
        } else if (!Pattern.matches(phoneRegex, phone)) {
            phoneValidation.setText("Invalid phone format");
            return false;
        } else {
            phoneValidation.setText("");
            return true;
        }
    }

    private boolean validateSalary() {
        String salaryText = tsalary.getText().trim();
        if (salaryText.isEmpty()) {
            salaryValidation.setText("Salary is required");
            return false;
        }
        try {
            double salary = Double.parseDouble(salaryText);
            if (salary <= 0) {
                salaryValidation.setText("Salary must be positive");
                return false;
            } else {
                salaryValidation.setText("");
                return true;
            }
        } catch (NumberFormatException e) {
            salaryValidation.setText("Invalid salary format");
            return false;
        }
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        if (!validateName()) isValid = false;
        if (!validateEmail()) isValid = false;
        if (!validatePhone()) isValid = false;
        if (!validateSalary()) isValid = false;

        if (tfname.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Father's name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            isValid = false;
        }

        if (tdob.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date of birth is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            isValid = false;
        }

        if (taddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            handleAddEmployee();
        } else if (e.getSource() == clear) {
            clearFields();
        } else if (e.getSource() == back) {
            handleBack();
        }
    }

    private void handleAddEmployee() {
        if (!validateAllFields()) {
            return;
        }

        add.setEnabled(false);
        add.setText("Adding...");

        // Add timing for debugging
        long startTime = System.currentTimeMillis();
        System.out.println("Starting employee addition at: " + new java.util.Date());

        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            private String errorMessage = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    publish("Connecting to database...");
                    Thread.sleep(100); // Small delay for UI update

                    publish("Validating employee ID...");
                    boolean result = addEmployeeToDatabase();

                    publish("Finalizing...");
                    return result;
                } catch (Exception ex) {
                    errorMessage = ex.getMessage();
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                // Update progress (you can show this in a progress dialog)
                for (String message : chunks) {
                    System.out.println("Progress: " + message);
                }
            }

            @Override
            protected void done() {
                long endTime = System.currentTimeMillis();
                System.out.println("Database operation completed in: " + (endTime - startTime) + "ms");

                add.setEnabled(true);
                add.setText("Add Employee");

                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AddEmployeePanel.this,
                                "Employee added successfully!\nEmployee ID: " + tempid.getText(),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    } else {
                        String message = errorMessage.isEmpty() ? "Failed to add employee to database." : errorMessage;
                        JOptionPane.showMessageDialog(AddEmployeePanel.this,
                                "Error adding employee: " + message,
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(AddEmployeePanel.this,
                            "Unexpected error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void handleBack() {
        Container parent = getParent();
        if (parent instanceof JPanel) {
            ((JPanel) parent).removeAll();
            parent.repaint();
            parent.revalidate();
        }
    }

    private boolean addEmployeeToDatabase() {
        PreparedStatement pstmt = null;
        PreparedStatement checkStmt = null;

        try {
            long dbStartTime = System.currentTimeMillis();

            // Ensure connection is valid
            if (sharedConnection == null || sharedConnection.isClosed()) {
                initializeDatabase();
            }

            if (sharedConnection == null) {
                throw new SQLException("Failed to establish database connection");
            }

            System.out.println("Connection established in: " + (System.currentTimeMillis() - dbStartTime) + "ms");

            // Quick ID check with optimized query
            long checkStartTime = System.currentTimeMillis();
            String checkQuery = "SELECT 1 FROM employee WHERE empID = ? LIMIT 1";
            checkStmt = sharedConnection.prepareStatement(checkQuery);
            checkStmt.setString(1, tempid.getText());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Generate new ID if current one exists
                number = ran.nextInt(999999);
                tempid.setText("EMP" + String.format("%06d", number));
            }
            rs.close();
            checkStmt.close();

            System.out.println("ID check completed in: " + (System.currentTimeMillis() - checkStartTime) + "ms");

            // Optimized insert query
            long insertStartTime = System.currentTimeMillis();
            String query = "INSERT INTO employee (name, fname, dob, salary, address, phone, email, education, department, gender, empID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = sharedConnection.prepareStatement(query);

            pstmt.setString(1, tname.getText().trim());
            pstmt.setString(2, tfname.getText().trim());
            pstmt.setDate(3, new java.sql.Date(tdob.getDate().getTime()));
            pstmt.setDouble(4, Double.parseDouble(tsalary.getText().trim()));
            pstmt.setString(5, taddress.getText().trim());
            pstmt.setString(6, tphone.getText().trim());
            pstmt.setString(7, temail.getText().trim());
            pstmt.setString(8, (String) Boxeducation.getSelectedItem());
            pstmt.setString(9, (String) Boxdepartment.getSelectedItem());
            pstmt.setString(10, (String) Boxgender.getSelectedItem());
            pstmt.setString(11, tempid.getText());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Insert completed in: " + (System.currentTimeMillis() - insertStartTime) + "ms");

            return rowsAffected > 0;

        } catch (SQLException ex) {
            System.err.println("SQL Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("General Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Unexpected error: " + ex.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (checkStmt != null) checkStmt.close();
                // Don't close the shared connection here
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearFields() {
        tname.setText("");
        tfname.setText("");
        tdob.setDate(null);
        tsalary.setText("");
        taddress.setText("");
        tphone.setText("");
        temail.setText("");
        Boxeducation.setSelectedIndex(0);
        Boxdepartment.setSelectedIndex(0);
        Boxgender.setSelectedIndex(0);

        nameValidation.setText("");
        emailValidation.setText("");
        phoneValidation.setText("");
        salaryValidation.setText("");

        number = ran.nextInt(999999);
        tempid.setText("EMP" + String.format("%06d", number));
        tname.requestFocus();
    }

    // Cleanup method to call when closing the application
    public static void cleanup() {
        try {
            if (sharedConnection != null && !sharedConnection.isClosed()) {
                sharedConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}