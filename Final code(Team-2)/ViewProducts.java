import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.awt.*;

public class ViewProducts extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField search;

    ViewProducts(){
        if(!Session.isLoggedIn){
        JOptionPane.showMessageDialog(this,"Please login first");
        dispose();
        new LoginPage();
        return;
    }

        setLayout(new BorderLayout());

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Stock");

        table = new JTable(model);

        loadProducts();

        add(new JScrollPane(table),"Center");

        // Top panel
        JPanel topPanel = new JPanel();

        search = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        topPanel.add(new JLabel("Search Product:"));
        topPanel.add(search);
        topPanel.add(searchBtn);

        add(topPanel,"North");

        // Bottom panel
        JPanel bottomPanel = new JPanel();

        JButton cart = new JButton("Add To Cart");

        bottomPanel.add(cart);

        add(bottomPanel,"South");

        searchBtn.addActionListener(e -> searchProduct());
        cart.addActionListener(e -> addToCart());

        setSize(500,350);
        setVisible(true);
    }

    // Load products from database
    void loadProducts(){

        model.setRowCount(0);

        try{

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM PRODUCTS");

            while(rs.next()){

                model.addRow(new Object[]{

                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getInt("PRICE"),
                        rs.getInt("STOCK")
                });

            }
             rs.close();
             st.close();
             con.close();  

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Search product
    void searchProduct(){

        String name = search.getText().trim().toLowerCase();

        boolean found = false;

        for(int i=0;i<table.getRowCount();i++){

            String productName = table.getValueAt(i,1).toString().toLowerCase();

            if(productName.contains(name)){

                table.setRowSelectionInterval(i,i);
                table.scrollRectToVisible(table.getCellRect(i,0,true));

                found = true;
                break;
            }
        }

        if(!found){
            JOptionPane.showMessageDialog(this,"Product not found");
        }
    }

    // Add product to cart
   void addToCart(){

    int row = table.getSelectedRow();

    if(row == -1){
        JOptionPane.showMessageDialog(this,"Select Product");
        return;
    }

    int id = Integer.parseInt(table.getValueAt(row,0).toString());
    String name = table.getValueAt(row,1).toString();
    int price = Integer.parseInt(table.getValueAt(row,2).toString());
    int stock = Integer.parseInt(table.getValueAt(row,3).toString());

    if(stock == 0){
        JOptionPane.showMessageDialog(this,"Out Of Stock");
        return;
    }

    try{

        Connection con = DBConnection.getConnection();

        // Try updating first
        PreparedStatement update = con.prepareStatement(
        "UPDATE CART SET QUANTITY = QUANTITY + 1 WHERE PRODUCT_ID=?");

        update.setInt(1,id);

        int rows = update.executeUpdate();

        // If no row updated → insert new
        if(rows == 0){

            PreparedStatement insert = con.prepareStatement(
            "INSERT INTO CART VALUES(CART_SEQ.NEXTVAL,?,?,?,1)");

            insert.setInt(1,id);
            insert.setString(2,name);
            insert.setInt(3,price);

            insert.executeUpdate();
        }

        // Reduce stock
        PreparedStatement stockUpdate = con.prepareStatement(
        "UPDATE PRODUCTS SET STOCK = STOCK - 1 WHERE ID=?");

        stockUpdate.setInt(1,id);
        stockUpdate.executeUpdate();

        JOptionPane.showMessageDialog(this,"Added to Cart");

        loadProducts();
        update.close();
        stockUpdate.close();
        con.close();

    }catch(Exception e){
        e.printStackTrace();
    }
}}