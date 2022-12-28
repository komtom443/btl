package com.b19dcat208.btl.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.b19dcat208.btl.Entity.User;

public class UserDao {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    
    private static final String SELECT_ALL_USER      = "select * from users";
    private static final String SELECT_USER_BY_ID= "select * from users where userid = ?";
    private static final String INSERT_USER      = "insert into users values(?,?,?,?)";
    private static final String UPDATE_USER      = "update users set name = ?,dob = ?,email = ? where userid = ?";
    private static final String DELETE_USER      = "delete from book where userid =?";

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
    public String getUserName(String username)
    {
        AccountDAO accountDAO = new AccountDAO();
        return getUser(accountDAO.getAccountByUsername(username).getUserID()).getName();
    }
    public User getUser(String useriD)
    {
        User user = new User();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);
            preparedStatement.setString(1, useriD);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            user = new User(resultSet.getString("userid"),resultSet.getString("name"),resultSet.getString("dob"),resultSet.getString("email"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return user;
    }

    public boolean updateUser(String attribute,String value,String userID)
    {
        try
        {
            Connection connection = getConnection();
            String tmp = "update users set "+attribute+" = ? where userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(tmp);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, userID);
            preparedStatement.executeUpdate();
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
