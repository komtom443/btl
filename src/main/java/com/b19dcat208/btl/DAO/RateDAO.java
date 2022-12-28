package com.b19dcat208.btl.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RateDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    
    private static final String SELECT_RATE = "select value from rate where book_id=?";
    private static final String COUNT_RATE = "select count(id) from rate where book_id=?";
    private static final String CHECK_RATE = "select count(id) from rate where book_id=? and user_id=?";
    private static final String INSERT_RATE = "insert into rate values(?,?,?,?)";
    private static final String UPDATE_RATE = "update rate set value=? where book_id = ? and user_id=?";
    public RateDAO(){}
    
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
    private boolean checkEmpty(String bookID)
    {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_RATE);
            preparedStatement.setString(1, bookID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt(1) <= 0)
            {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
    public int[] getRateByBookID(String bookID)
    {
        int[] rate = {0,0,0,0,0,0,0,0,0,0,0};
        try {
            if(checkEmpty(bookID))
            {
                return rate;
            }
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RATE);
            preparedStatement.setString(1, bookID);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            float sum = 0;
            while(resultSet.next())
            {
                float tmp = resultSet.getFloat("value");
                sum += tmp;count+=1;
                rate[(int)(tmp*2)] +=1;
            }
            Collections.reverse(Arrays.asList(rate));
            rate[0] = (int)(sum / count * 10);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return rate;
    }

    public ArrayList<String> getBooksRate()
    {
        ArrayList<String> list = new ArrayList<>();
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select id from book where detail=\"old\"");
            ResultSet resultSet = preparedStatement.executeQuery(),rs2;
            while(resultSet.next())
            {
                list.add("\""+resultSet.getString("id")+"\":"+getRateByBookID(resultSet.getString("id"))[0]+"");
            };
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }

    public boolean uploadRate(String username,String bookID,float value)
    {
        try {
            Connection connection = getConnection();
            AccountDAO accountDAO = new AccountDAO();
            String userID = accountDAO.getAccountByUsername(username).getUserID();
            PreparedStatement preparedStatement= connection.prepareStatement("select id from rate order by id desc limit 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int id = resultSet.getInt("id") + 1;
            preparedStatement= connection.prepareStatement(CHECK_RATE);
            preparedStatement.setString(1, bookID);
            preparedStatement.setString(2, userID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt(1) == 0)
            {
                preparedStatement = connection.prepareStatement(INSERT_RATE);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(3, bookID);
                preparedStatement.setString(2, userID);
                preparedStatement.setFloat(4, value);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            else
            {
                preparedStatement = connection.prepareStatement(UPDATE_RATE);
                preparedStatement.setString(2, bookID);
                preparedStatement.setString(3, userID);
                preparedStatement.setFloat(1, value);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return true;
    }
}
