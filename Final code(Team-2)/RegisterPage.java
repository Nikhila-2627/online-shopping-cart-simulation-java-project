import javax.swing.*;
import java.sql.*;

public class RegisterPage extends JFrame{

    RegisterPage(){

        setLayout(null);

        JTextField fname = new JTextField();
        JTextField lname = new JTextField();
        JTextField email = new JTextField();
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        add(new JLabel("First Name")).setBounds(30,30,100,30);
        add(new JLabel("Last Name")).setBounds(30,70,100,30);
        add(new JLabel("Email")).setBounds(30,110,100,30);
        add(new JLabel("Username")).setBounds(30,150,100,30);
        add(new JLabel("Password")).setBounds(30,190,100,30);

        fname.setBounds(140,30,150,30);
        lname.setBounds(140,70,150,30);
        email.setBounds(140,110,150,30);
        user.setBounds(140,150,150,30);
        pass.setBounds(140,190,150,30);

        add(fname); 
        add(lname); 
        add(email); 
        add(user); 
        add(pass);

        JButton register = new JButton("Register");
        register.setBounds(110,240,120,30);
        add(register);

        register.addActionListener(e -> {

            try{

                Connection con = DBConnection.getConnection();

                PreparedStatement ps = con.prepareStatement(
                "INSERT INTO USERS VALUES(USER_SEQ.NEXTVAL,?,?,?,?,?)");

                ps.setString(1,fname.getText());
                ps.setString(2,lname.getText());
                ps.setString(3,email.getText());
                ps.setString(4,user.getText());
                ps.setString(5,new String(pass.getPassword()));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Registration Successful");

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        setSize(350,350);
        setVisible(true);
    }
}