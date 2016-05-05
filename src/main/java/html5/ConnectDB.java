package html5;

//import javafx.tools.resource.PackagerResource;
import java.io.Serializable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Created by admin on 4/27/16.
 */

@ManagedBean(name = "controller", eager = true)
@SessionScoped
public class ConnectDB implements Serializable{
private static final long serialVersionUID = 1L;
private Product product = null;

public ConnectDB(){
    if(product == null)
        product = new Product();
}

    private static Connection getConnection() {
        Connection con = null;
        try {//jdbc:mysql://localhost:3334/MusicStore?zeroDateTimeBehavior=convertToNull [root on Default schema]
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3334/MusicStore", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return con;
        }
    }
    
    public void resetProduct(){
        FacesContext fc = FacesContext.getCurrentInstance(); 
        product = new Product();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        product.code = params.get("code");
        product.description = params.get("description");
        product.price = Double.parseDouble(params.get("price"));
    }
    
    public void setProduct(Product product){
        this.product = product;
    }
    
    public Product getProduct(){
        return product;
    }

    //This method returns null if a product isn't found.
    private static Product selectProduct(String productCode)
    {

        Connection connection = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM Product " +
                "WHERE ProductCode = ?";
        try
        {
            ps = connection.prepareStatement(query);
            ps.setString(1, productCode);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Product p = new Product();
                p.setCode(rs.getString("ProductCode"));
                p.setDescription(rs.getString("ProductDescription"));
                p.setPrice(rs.getInt("ProductPrice")/100.0);
                return p;
            }
            else
            {
                return null;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
        }
    }

    //This method will return 0 if productID isn't found.
    private static int selectProductID(Product product)
    {
        Connection connection = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT ProductID FROM Product " +
                "WHERE ProductCode = ?";
        try
        {
            ps = connection.prepareStatement(query);
            ps.setString(1, product.getCode());
            rs = ps.executeQuery();
            rs.next();
            int productID = rs.getInt("ProductID");
            return productID;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return 0;
        }
        finally
        {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
            DBUtil.closeConnection(connection);
        }
    }

    //This method returns null if a product isn't found.
    private static Product selectProduct(int productID)
    {
        Connection connection = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM Product " +
                "WHERE ProductID = ?";
        try
        {
            ps = connection.prepareStatement(query);
            ps.setInt(1, productID);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Product p = new Product();
                p.setCode(rs.getString("ProductCode"));
                p.setDescription(rs.getString("ProductDescription"));
                p.setPrice(rs.getInt("ProductPrice")/100.0);
                return p;
            }
            else
            {
                return null;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
            DBUtil.closeConnection(connection);
        }
    }

    //This method returns null if a product isn't found.
    public List<Product> getProducts()
    {     
        Connection connection = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Product> products;
      
        String query = "SELECT * FROM Product";
        try
        {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            products = new ArrayList<Product>();
            while (rs.next())
            {
                Product p = new Product();
                p.setCode(rs.getString("ProductCode"));
                p.setDescription(rs.getString("ProductDescription"));
                p.setPrice(rs.getInt("ProductPrice")/100.0);
                products.add(p);
            }
            return products;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
        }
    }

    public static boolean exists(String productCode){
        Product p = selectProduct(productCode);

        if(p != null) return true;
        return false;
    }
    

    public String insertProduct(){
        if(!exists(product.getCode())){
            Connection connection = getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;

            String query = "INSERT INTO Product " +
                    "(`ProductID`, `ProductCode`, `ProductDescription`, `ProductPrice`) " +
                    "VALUES (NULL, ?, ?, ?);";
            try {
                ps = connection.prepareStatement(query);
                ps.setString(1, product.getCode());
                ps.setString(2, product.getDescription());
                ps.setInt(3, (int)(product.getPrice() * 100));
                ps.executeUpdate();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                DBUtil.closeResultSet(rs);
                DBUtil.closePreparedStatement(ps);
            }
        }
        return "display";
    }
    
    public String toAdd(){
        product = new Product();
        return "add";
    }
    
    public String toEdit(){
        resetProduct();
        return "edit";
    }

    public String updateProduct(){
        if(exists(product.getCode())){
            Connection connection = getConnection();
            PreparedStatement ps = null;

            String query = "UPDATE  `MusicStore`.`Product` SET  `ProductCode` =  ?," +
                    "`ProductDescription` =  ?," +
                    "`ProductPrice` =  ? WHERE  `Product`.`ProductCode` =?;";
            try {
                ps = connection.prepareStatement(query);
                ps.setString(1, product.getCode());
                ps.setString(2, product.getDescription());
                ps.setInt(3, (int)(product.getPrice() * 100));
                ps.setString(4, product.getCode());
                ps.executeUpdate();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                DBUtil.closePreparedStatement(ps);
                DBUtil.closeConnection(connection);
            }
        }else{
            System.out.println("Product with code: " + product.getCode() + " not found");
        }
        return "display";
    }
    
    public String toDelete(){
        resetProduct();
        return "delete";
    }

    public String deleteProduct(){
        if(exists(product.getCode())){
            Connection connection = getConnection();
            PreparedStatement ps = null;

            String query = "DELETE FROM `MusicStore`.`Product` WHERE `Product`.`ProductCode` = ?";
            try {
                ps = connection.prepareStatement(query);
                ps.setString(1, product.getCode());
                ps.executeUpdate();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                DBUtil.closePreparedStatement(ps);
                DBUtil.closeConnection(connection);
            }
        }else{
            System.out.println("Product with code: " + product.getCode() + " not found");
        }
        return "display";
    }


}
