import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class OrderDetails extends JFrame {

    JTextField fname,lname,location;
    JComboBox payment;

    OrderDetails(){

        setTitle("Order Details");
        setLayout(null);

        JLabel f = new JLabel("First Name:");
        JLabel l = new JLabel("Last Name:");
        JLabel loc = new JLabel("Location:");
        JLabel pay = new JLabel("Payment Method:");

        fname = new JTextField();
        lname = new JTextField();
        location = new JTextField();

        String methods[]={"UPI","Credit Card","Cash on Delivery"};
        payment = new JComboBox<>(methods);
        f.setBounds(40,40,100,30);
        l.setBounds(40,80,100,30);
        loc.setBounds(40,120,100,30);
        pay.setBounds(40,160,120,30);

        fname.setBounds(150,40,180,30);
        lname.setBounds(150,80,180,30);
        location.setBounds(150,120,180,30);
        payment.setBounds(150,160,180,30);

        JButton confirm = new JButton("Confirm Order");
        confirm.setBounds(150,210,150,30);

        add(f);
        add(l);
        add(loc); 
        add(pay);
        add(fname); 
        add(lname); 
        add(location);
        add(payment);
        add(confirm);

        confirm.addActionListener(e -> saveOrder());

        setSize(400,320);
        setVisible(true);
    }

    void saveOrder(){

        if(fname.getText().equals("") ||
           lname.getText().equals("") ||
           location.getText().equals("")){

            JOptionPane.showMessageDialog(this,
            "Please fill all fields");

            return;
        }

        try{

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            PreparedStatement order = con.prepareStatement(
            "INSERT INTO ORDERS VALUES(ORDER_SEQ.NEXTVAL,?,?,?,?,SYSDATE)");
            order.setString(1,fname.getText());
            order.setString(2,lname.getText());
            order.setString(3,location.getText());
            order.setString(4,payment.getSelectedItem().toString());
            order.executeUpdate();

            ResultSet idrs = st.executeQuery(
            "SELECT MAX(ORDER_ID) FROM ORDERS");

            int orderId=0;

            if(idrs.next()){
                orderId=idrs.getInt(1);
            }

            ResultSet cart = st.executeQuery(
            "SELECT PRODUCT_NAME,PRICE,QUANTITY FROM CART");

            while(cart.next()){

                PreparedStatement item = con.prepareStatement(
                "INSERT INTO ORDER_ITEMS VALUES(ITEM_SEQ.NEXTVAL,?,?,?,?,SYSDATE+2)");

                item.setInt(1,orderId);
                item.setString(2,cart.getString(1));
                item.setInt(3,cart.getInt(2));
                item.setInt(4,cart.getInt(3));

                item.executeUpdate();
            }

            st.executeUpdate("DELETE FROM CART");

            JOptionPane.showMessageDialog(this,
            "Order Placed Successfully \nYour order will be delivered soon!");

            new OrderHistory();
            cart.close();
            idrs.close();
            st.close();
            order.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}