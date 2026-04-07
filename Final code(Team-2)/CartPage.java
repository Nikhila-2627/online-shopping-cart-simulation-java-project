import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.awt.*;

public class CartPage extends JFrame {

    JTable table;
    DefaultTableModel model;
    JLabel totalLabel;

    CartPage(){
        if(!Session.isLoggedIn){
        JOptionPane.showMessageDialog(this,"Please login first");
        dispose();
        new LoginPage();
        return;
    }

        setTitle("Shopping Cart");
        setLayout(new BorderLayout());
        model = new DefaultTableModel(){
    public boolean isCellEditable(int row, int column){
        return false;
    }
};
        model.addColumn("Product ID");
        model.addColumn("Product");
        model.addColumn("Price");
        model.addColumn("Quantity");

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        JScrollPane sp = new JScrollPane(table);
        table.setRowHeight(25);
table.getTableHeader().setReorderingAllowed(false);
table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

       

        JButton plus = new JButton("+");
        JButton minus = new JButton("-");
        JButton remove = new JButton("Remove Product");
        JButton bill = new JButton("Generate Bill");

        plus.addActionListener(e -> increaseQty());
        minus.addActionListener(e -> decreaseQty());
        remove.addActionListener(e -> deleteProduct());
        bill.addActionListener(e -> new BillPage());
        totalLabel = new JLabel("Total: 0");
        JPanel panel = new JPanel();

        panel.add(plus);
        panel.add(minus);
        panel.add(remove);
        panel.add(bill);
        panel.add(totalLabel);

        add(sp,BorderLayout.CENTER);
        add(panel,BorderLayout.SOUTH);
        loadCart();

        setSize(520,320);
        setVisible(true);
    }

    // Load cart items
    void loadCart(){

        model.setRowCount(0);
        int total = 0;

        try{

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
            "SELECT PRODUCT_ID,PRODUCT_NAME,PRICE,QUANTITY FROM CART");

            while(rs.next()){

                int price = rs.getInt("PRICE");
                int qty = rs.getInt("QUANTITY");

                total += price * qty;

                model.addRow(new Object[]{
                    rs.getInt("PRODUCT_ID"),
                    rs.getString("PRODUCT_NAME"),
                    price,
                    qty
                });
            }

            totalLabel.setText("Total: " + total);

            rs.close();
            st.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Increase quantity
    void increaseQty(){

    int row = table.getSelectedRow();

    if(row == -1){
        JOptionPane.showMessageDialog(this,"Select product");
        return;
    }

    int id = Integer.parseInt(table.getValueAt(row,0).toString());

    try{

        Connection con = DBConnection.getConnection();

        // Check stock first
        PreparedStatement check = con.prepareStatement(
        "SELECT STOCK FROM PRODUCTS WHERE ID=?");

        check.setInt(1,id);

        ResultSet rs = check.executeQuery();

        if(rs.next()){

            int stock = rs.getInt("STOCK");

            if(stock <= 0){
                JOptionPane.showMessageDialog(this,"No Stock Available");
                rs.close();
                check.close();
                con.close();
                return;
            }
        }

        rs.close();
        check.close();

        // Increase cart quantity
        PreparedStatement ps = con.prepareStatement(
        "UPDATE CART SET QUANTITY = QUANTITY + 1 WHERE PRODUCT_ID=?");

        ps.setInt(1,id);
        ps.executeUpdate();

        // Reduce stock
        PreparedStatement stockUpdate = con.prepareStatement(
        "UPDATE PRODUCTS SET STOCK = STOCK - 1 WHERE ID=?");

        stockUpdate.setInt(1,id);
        stockUpdate.executeUpdate();

        ps.close();
        stockUpdate.close();
        con.close();

        loadCart();

    }catch(Exception e){
        e.printStackTrace();
    }
}
    // Decrease quantity
    void decreaseQty(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select product");
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row,0).toString());
        int qty = Integer.parseInt(table.getValueAt(row,3).toString());

        if(qty <= 1){
            JOptionPane.showMessageDialog(this,"Minimum quantity reached");
            return;
        }

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
            "UPDATE CART SET QUANTITY = QUANTITY - 1 WHERE PRODUCT_ID=?");

            ps.setInt(1,id);
            ps.executeUpdate();

            PreparedStatement stock = con.prepareStatement(
            "UPDATE PRODUCTS SET STOCK = STOCK + 1 WHERE ID=?");

            stock.setInt(1,id);
            stock.executeUpdate();

            ps.close();
            stock.close();
            con.close();

            loadCart();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Remove product
    void deleteProduct(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select product");
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row,0).toString());
        int qty = Integer.parseInt(table.getValueAt(row,3).toString());

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
            "DELETE FROM CART WHERE PRODUCT_ID=?");

            ps.setInt(1,id);
            ps.executeUpdate();

            PreparedStatement stock = con.prepareStatement(
            "UPDATE PRODUCTS SET STOCK = STOCK + ? WHERE ID=?");

            stock.setInt(1,qty);
            stock.setInt(2,id);
            stock.executeUpdate();

            ps.close();
            stock.close();
            con.close();

            loadCart();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}