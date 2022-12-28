package com.b19dcat208.btl.DAO;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.b19dcat208.btl.Entity.Author;
import com.b19dcat208.btl.Entity.Book;
import com.b19dcat208.btl.Entity.Cart;
import com.b19dcat208.btl.Entity.Category;

public class CartDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    private static final String SELECT_ALL_ITEM  = "select * from cart where userid = ?";
    private static final String COUNT_ITEM_BY_ID = "select count(id) from cart where userid = ? and bookid = ?";
    private static final String SELECT_ITEM_BY_ID = "select value from cart where userid = ? and bookid = ?";
    private static final String INSERT_ITEM      = "insert into cart (bookid,userid,value,date) values(?,?,?,?)";
    private static final String DELETE_ITEM      = "delete from cart where id=?";
    private static final String EDIT_BY_ID = "update cart set value = ?, date=? where userid = ? and bookid = ?";
    public CartDAO(){}
    protected Connection getConnection()
    {
        Connection connection = null;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPasswd);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return connection;
    }
    public ArrayList<Cart> getItem(String userID)
    {
        ArrayList<Cart> carts = new ArrayList<>();
        Connection connection = getConnection();
        try {
            BookDAO bookDAO = new BookDAO();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ITEM);
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next())
            {
                carts.add(new Cart(rs.getInt("id"),bookDAO.getBook(rs.getString("bookid")), rs.getInt("value"),rs.getString("date")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carts;
    }

    public boolean deleteItem(int recordID)
    {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ITEM);
            preparedStatement.setInt(1, recordID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public boolean insertItem(String bookID,String userID,int value,String date,int mode)
    {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_ITEM_BY_ID);
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, bookID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            if(rs.getInt(1) == 0)
            {
                preparedStatement = connection.prepareStatement(INSERT_ITEM);
                preparedStatement.setInt(3, value);
                preparedStatement.setString(1, bookID);
                preparedStatement.setString(2, userID);
                preparedStatement.setString(4, date);
                preparedStatement.executeUpdate();
            }
            else
            {
                preparedStatement = connection.prepareStatement(SELECT_ITEM_BY_ID);
                preparedStatement.setString(1, userID);
                preparedStatement.setString(2, bookID);
                rs = preparedStatement.executeQuery();
                rs.next();
                preparedStatement = connection.prepareStatement(EDIT_BY_ID);
                if(mode == 0)
                {
                    preparedStatement.setInt(1, value + rs.getInt(1));
                }
                else
                {
                    preparedStatement.setInt(1, value);
                }
                preparedStatement.setString(4, bookID);
                preparedStatement.setString(3, userID);
                preparedStatement.setString(2, date);
                preparedStatement.executeUpdate();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
