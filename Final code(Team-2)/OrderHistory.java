import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class OrderHistory extends JFrame {

    JTable table;

    OrderHistory(){
         if(!Session.isLoggedIn){
        JOptionPane.showMessageDialog(this,"Please login first");
        dispose();
        new LoginPage();
        return;
    }

        // Create non-editable table model
        DefaultTableModel model = new DefaultTableModel(){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        model.addColumn("Product Name");
        model.addColumn("Price");
        model.addColumn("Delivered Date");

        table = new JTable(model);
        table.setRowHeight(25);
table.getTableHeader().setReorderingAllowed(false);
        try{

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
            "SELECT PRODUCT_NAME,PRICE,DELIVERED_DATE FROM ORDER_ITEMS");

            while(rs.next()){

                model.addRow(new Object[]{
                    rs.getString("PRODUCT_NAME"),
                    rs.getInt("PRICE"),
                    rs.getDate("DELIVERED_DATE")
                });
            }

            rs.close();
            st.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        add(new JScrollPane(table));

        setSize(420,250);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}