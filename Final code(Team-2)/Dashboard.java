import javax.swing.*;

public class Dashboard extends JFrame{

    Dashboard(){

        setLayout(null);

        JButton login = new JButton("Login");
        JButton register = new JButton("Register");
        JButton products = new JButton("View Products");
        JButton cart = new JButton("View Cart");
        JButton history = new JButton("Order History");
        JButton logout = new JButton("Logout");

        login.setBounds(80,30,200,40);
        register.setBounds(80,80,200,40);
        products.setBounds(80,130,200,40);
        cart.setBounds(80,180,200,40);
        history.setBounds(80,230,200,40);
        logout.setBounds(80,280,200,40);

        add(login);
        add(register);
        add(products);
        add(cart);
        add(history);
        add(logout);

        login.addActionListener(e -> new LoginPage());
        register.addActionListener(e -> new RegisterPage());
        products.addActionListener(e -> new ViewProducts());
        cart.addActionListener(e -> new CartPage());
        history.addActionListener(e -> new OrderHistory());
        logout.addActionListener(e -> {

    int choice = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Logout",
        JOptionPane.YES_NO_OPTION
    );

    if(choice == JOptionPane.YES_OPTION){

        Session.isLoggedIn = false;

        JOptionPane.showMessageDialog(this,"Logged out successfully");

        dispose();
        new LoginPage();
    }

});

        setSize(350,300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}