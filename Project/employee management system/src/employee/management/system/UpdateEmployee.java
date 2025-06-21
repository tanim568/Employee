package employee.management.system;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class UpdateEmployee extends JFrame implements ActionListener {
    JTextField tfname, taddress, tphone, temail, tsalary;
    JLabel tempid, tname, tdob, tgender;
    JButton update, back;
    JComboBox<String> boxEducation, boxDepartment;
    String empId;

    UpdateEmployee(String empId) {
        this.empId = empId;
        setTitle("Update Employee");
        setSize(900, 600);
        setLocation(300, 100);
        setLayout(null);
        getContentPane().setBackground(new Color(163, 255, 188));

        JLabel heading = new JLabel("Update Employee Detail");
        heading.setFont(new Font("SAN_SERIF", Font.BOLD, 25));
        heading.setBounds(320, 30, 500, 50);
        add(heading);

        JLabel name = new JLabel("Name");
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        name.setBounds(50, 100, 150, 30);
        add(name);

        tname = new JLabel();
        tname.setFont(new Font("Tahoma", Font.BOLD, 20));
        tname.setBounds(200, 100, 200, 30);
        add(tname);

        JLabel fname = new JLabel("Father's Name");
        fname.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        fname.setBounds(400, 100, 150, 30);
        add(fname);

        tfname = new JTextField();
        tfname.setBounds(600, 100, 200, 30);
        add(tfname);

        JLabel dob = new JLabel("Date of Birth");
        dob.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        dob.setBounds(50, 150, 150, 30);
        add(dob);

        tdob = new JLabel();
        tdob.setFont(new Font("Tahoma", Font.BOLD, 20));
        tdob.setBounds(200, 150, 200, 30);
        add(tdob);

        JLabel salary = new JLabel("Salary");
        salary.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        salary.setBounds(400, 150, 150, 30);
        add(salary);

        tsalary = new JTextField();
        tsalary.setBounds(600, 150, 200, 30);
        add(tsalary);

        JLabel address = new JLabel("Address");
        address.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        address.setBounds(50, 200, 150, 30);
        add(address);

        taddress = new JTextField();
        taddress.setBounds(200, 200, 200, 30);
        add(taddress);

        JLabel phone = new JLabel("Phone");
        phone.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        phone.setBounds(400, 200, 150, 30);
        add(phone);

        tphone = new JTextField();
        tphone.setBounds(600, 200, 200, 30);
        add(tphone);

        JLabel email = new JLabel("Email");
        email.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        email.setBounds(50, 250, 150, 30);
        add(email);

        temail = new JTextField();
        temail.setBounds(200, 250, 200, 30);
        add(temail);

        JLabel education = new JLabel("Education");
        education.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        education.setBounds(400, 250, 150, 30);
        add(education);

        String[] educationOptions = {"BBA", "B.Tech", "BCA", "BA", "BSC", "B.COM", "MBA", "MCA", "MA", "MTech", "MSC", "PHD"};
        boxEducation = new JComboBox<>(educationOptions);
        boxEducation.setBounds(600, 250, 200, 30);
        add(boxEducation);

        JLabel gender = new JLabel("Gender");
        gender.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        gender.setBounds(50, 300, 150, 30);
        add(gender);

        tgender = new JLabel();
        tgender.setFont(new Font("Tahoma", Font.BOLD, 20));
        tgender.setBounds(200, 300, 200, 30);
        add(tgender);

        JLabel department = new JLabel("Department");
        department.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        department.setBounds(400, 300, 150, 30);
        add(department);

        String[] departmentOptions = {"IT", "HR", "Finance", "Marketing"};
        boxDepartment = new JComboBox<>(departmentOptions);
        boxDepartment.setBounds(600, 300, 200, 30);
        add(boxDepartment);

        JLabel empidLabel = new JLabel("Employee ID");
        empidLabel.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        empidLabel.setBounds(50, 350, 150, 30);
        add(empidLabel);

        tempid = new JLabel();
        tempid.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        tempid.setForeground(Color.RED);
        tempid.setBounds(200, 350, 200, 30);
        add(tempid);

        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery("SELECT * FROM employee WHERE empId = '" + empId + "'");
            if (rs.next()) {
                tname.setText(rs.getString("name"));
                tfname.setText(rs.getString("fname"));
                tdob.setText(rs.getString("dob"));
                taddress.setText(rs.getString("address"));
                tsalary.setText(rs.getString("salary"));
                tphone.setText(rs.getString("phone"));
                temail.setText(rs.getString("email"));
                boxEducation.setSelectedItem(rs.getString("education"));
                tgender.setText(rs.getString("gender"));
                tempid.setText(rs.getString("empId"));
                boxDepartment.setSelectedItem(rs.getString("department"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        update = new JButton("UPDATE");
        update.setBounds(400, 450, 150, 40);
        update.addActionListener(this);
        add(update);

        back = new JButton("BACK");
        back.setBounds(200, 450, 150, 40);
        back.addActionListener(this);
        add(back);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            setVisible(false);
            new EmployeeSearch();
        }
    }
}
