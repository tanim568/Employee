package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Splash extends JFrame {

    Splash() {
        // Load splash screen image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/front.gif"));
        Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);
        JLabel image = new JLabel(new ImageIcon(i2));
        image.setBounds(0, 0, 1170, 650);
        add(image);

        // Frame settings
        setSize(1170, 650);
        setLocation(200, 50);
        setLayout(null);
        setUndecorated(true);

        // Rounded corners
        try {
            setShape(new RoundRectangle2D.Double(0, 0, 1170, 650, 50, 50));
        } catch (Exception e) {
            System.out.println("Rounded corners not supported.");
        }

        setVisible(true);

        // Delay and transition to the LoginSelection page
        try {
            Thread.sleep(5000);
            setVisible(false);
            new LoginSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Splash();
    }
}
