import javax.swing.*;
import java.sql.*;

public class BillPage extends JFrame {

    JTextArea bill;
    int subtotal = 0;
    int gst = 0;
    int total = 0;

    BillPage(){

        setTitle("Generate Bill");
        setLayout(null);

        bill = new JTextArea();
        bill.setEditable(false);
        JScrollPane sp = new JScrollPane(bill);
        sp.setBounds(20,20,330,180);
        generateBill();

        JTextField coupon = new JTextField();
        coupon.setBounds(40,210,150,30);

        JButton apply = new JButton("Apply Coupon");
        apply.setBounds(200,210,140,30);

        JButton placeOrder = new JButton("Place Order");
        placeOrder.setBounds(100,270,150,30);

        add(sp);
        add(coupon);
        add(apply);
        add(placeOrder);

        apply.addActionListener(e -> {

            String code = coupon.getText().trim();

            if(code.startsWith("SAVE")){

                try{

                    int discount = Integer.parseInt(code.substring(4));

                    int discountAmount = total * discount / 100;

                    int finalAmount = total - discountAmount;

                    bill.append("\nDiscount ("+discount+"%) = -"+discountAmount);
                    bill.append("\nFinal Amount = "+finalAmount);

                }catch(Exception ex){

                    JOptionPane.showMessageDialog(this,"Invalid Coupon");

                }

            }else{

                JOptionPane.showMessageDialog(this,"Invalid Coupon");

            }

        });

        placeOrder.addActionListener(e -> new OrderDetails());

        setSize(400,350);
        setVisible(true);
    }

    void generateBill(){

        try{

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
            "SELECT PRODUCT_NAME,PRICE,QUANTITY FROM CART");

            bill.setText("------ BILL ------\n\n");

            while(rs.next()){

                int price = rs.getInt(2);
                int qty = rs.getInt(3);

                int sub = price * qty;

                bill.append(rs.getString(1)+" x "+qty+" = "+sub+"\n");

                subtotal += sub;
            }

            gst = subtotal * 5 / 100;
            total = subtotal + gst;

            bill.append("\nSubtotal = "+subtotal);
            bill.append("\nGST (5%) = "+gst);
            bill.append("\nTotal = "+total);
            rs.close();
            st.close();
            con.close();


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}