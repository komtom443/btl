package com.b19dcat208.btl.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.b19dcat208.btl.Entity.Account;

public class AccountDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    
    private static final String SELECT_ALL_ACCOUNT  = "select id,username,status,mode from accounts";
    private static final String SELECT_ACCOUNT_BY_USERNAME= "select id,username,status,mode,userid from accounts where username=?";
    private static final String SELECT_ACCOUNT_MODE = "select id,username,status,mode from accounts where mode=?";
    private static final String SELECT_ACCOUNT      = "select count(*) from accounts where username=? and passwd=?";
    private static final String CHECK_ACCOUNT       = "select count(username) from accounts where username=?";
    private static final String INSERT_ACCOUNT      = "insert into accounts values(?,?,?,?,?,?)";
    private static final String UPDATE_ACCOUNT      = "update accounts set passwd=?,status=? where id=? and username=?";
    private static final String DELETE_ACCOUNT      = "delete from accounts where id=?";
    private static final String LAST_ACCOUNT_ID     = "select id from accounts order by id desc limit 1";
    public AccountDAO(){}
    
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
    public Account getAccountByUsername(String accountUsername)
    {
        Account account = new Account();
        Connection connection = getConnection();
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_USERNAME);
            preparedStatement.setString(1, accountUsername);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String accountID = resultSet.getString("id");
            int accountStatus = resultSet.getInt("status");
            int accountMode = resultSet.getInt("mode");
            String userID = resultSet.getString("userid");
            account = new Account(accountID, accountUsername, accountStatus, accountMode,userID);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return account;
    }
    public String lastAccountID()
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(LAST_ACCOUNT_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int lastIndex =Integer.parseInt(resultSet.getString("id").substring(resultSet.getString("id").length()-3));
            return String.format("ACC%03d",lastIndex+1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public boolean createAccount(Account account)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCOUNT);
            preparedStatement.setString(1, account.getId());
            preparedStatement.setString(2, account.getUsername());
            preparedStatement.setString(3, account.getPasswd());
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, 0);
            preparedStatement.executeUpdate();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public boolean loginAccount(String username,String passwd)
    {
        try
        {
            int result;
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = resultSet.getInt(1);
            if(result == 1)
            {
                return true;
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
