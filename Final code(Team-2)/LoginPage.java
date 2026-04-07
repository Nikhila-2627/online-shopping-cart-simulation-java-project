import javax.swing.*;
import java.sql.*;

public class LoginPage extends JFrame{

    LoginPage(){

        setTitle("Login Page");
        setLayout(null);

        JLabel userLabel = new JLabel("Username");
        JLabel passLabel = new JLabel("Password");

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        userLabel.setBounds(40,40,100,30);
        passLabel.setBounds(40,90,100,30);

        user.setBounds(120,40,150,30);
        pass.setBounds(120,90,150,30);

        add(userLabel);
        add(passLabel);
        add(user);
        add(pass);

        // Show password checkbox
        JCheckBox show = new JCheckBox("Show Password");
        show.setBounds(120,120,150,20);
        add(show);

        show.addActionListener(e -> {

            if(show.isSelected()){
                pass.setEchoChar((char)0);
            }else{
                pass.setEchoChar('*');
            }

        });

        JButton login = new JButton("Login");
        login.setBounds(120,150,120,30);
        add(login);

        login.addActionListener(e -> {

            if(user.getText().trim().isEmpty() ||
               pass.getPassword().length == 0){

                JOptionPane.showMessageDialog(this,
                "Please enter username and password");

                return;
            }

            try{

                Connection con = DBConnection.getConnection();

                PreparedStatement ps = con.prepareStatement(
                "SELECT PASSWORD FROM USERS WHERE USERNAME=?");

                ps.setString(1,user.getText());

                ResultSet rs = ps.executeQuery();

                if(!rs.next()){

                    JOptionPane.showMessageDialog(this,
                    "User is not registered. Please register first.");

                }else{

                    String dbPassword = rs.getString("PASSWORD");
                    String enteredPassword = new String(pass.getPassword());

                    if(dbPassword.equals(enteredPassword)){

                        JOptionPane.showMessageDialog(this,"Login Successful");
                        Session.isLoggedIn = true;
                        dispose();
                        new ViewProducts();

                    }else{

                        JOptionPane.showMessageDialog(this,
                        "Invalid username or password");

                    }
                }
              rs.close();
              ps.close();
              con.close();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }

        });
        setSize(320,250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // center screen
        setVisible(true);
        
    }
}