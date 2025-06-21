package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddRemoveUI extends JFrame implements ActionListener {
    private JButton addEmployeeBtn, removeEmployeeBtn;
    private Main_class mainFrame; // Reference to the main window

    public AddRemoveUI(Main_class mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Employee Management - HR Panel");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE if the main window is still open
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(44, 62, 80));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel title = new JLabel("HR Employee Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(30));

        addEmployeeBtn = createStyledButton("Add Employee");
        removeEmployeeBtn = createStyledButton("Remove Employee");

        mainPanel.add(addEmployeeBtn);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(removeEmployeeBtn);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Sans Serif", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addEmployeeBtn) {
            mainFrame.getContentPanel().removeAll();
            mainFrame.getContentPanel().add(new AddEmployeePanel());
            mainFrame.getContentPanel().revalidate();
            mainFrame.getContentPanel().repaint();
            dispose();
        } else if (e.getSource() == removeEmployeeBtn) {
            mainFrame.getContentPanel().removeAll();
            mainFrame.getContentPanel().add(new RemoveEmployeePanel());
            mainFrame.getContentPanel().revalidate();
            mainFrame.getContentPanel().repaint();
            dispose();
        }
    }

    public static void main(String[] args) {
        Main_class mainFrame = new Main_class();
        new AddRemoveUI(mainFrame);
    }
}
