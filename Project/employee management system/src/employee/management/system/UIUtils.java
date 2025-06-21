package employee.management.system;

import javax.swing.border.AbstractBorder;
import javax.swing.*;
import java.awt.*;

public class UIUtils {
    
    /**
     * Creates a rounded border with the specified radius
     * @param radius the radius of the rounded corners
     * @return a rounded border
     */
    public static javax.swing.border.Border createRoundedBorder(int radius) {
        return new RoundedBorder(radius);
    }
    
    /**
     * Creates a rounded border with default radius of 10
     * @return a rounded border with radius 10
     */
    public static javax.swing.border.Border createRoundedBorder() {
        return new RoundedBorder(10);
    }
    
    /**
     * Creates a styled button with rounded corners
     * @param text button text
     * @param backgroundColor background color
     * @return styled JButton
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(createRoundedBorder(5));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    /**
     * Inner class for rounded border implementation
     */
    private static class RoundedBorder extends AbstractBorder {
        private int radius;
        
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(189, 195, 199));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = 4;
            return insets;
        }
    }
    
    // Additional utility methods you can add
    
    /**
     * Common colors used throughout the application
     */
    public static class Colors {
        public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
        public static final Color SUCCESS_COLOR = new Color(39, 174, 96);
        public static final Color ERROR_COLOR = new Color(231, 76, 60);
        public static final Color WARNING_COLOR = new Color(230, 126, 34);
        public static final Color INFO_COLOR = new Color(52, 152, 219);
        public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
        public static final Color PANEL_COLOR = Color.WHITE;
        public static final Color TEXT_COLOR = new Color(52, 73, 94);
    }
    
    /**
     * Common fonts used throughout the application
     */
    public static class Fonts {
        public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 28);
        public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
        public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
        public static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
        public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    }
}