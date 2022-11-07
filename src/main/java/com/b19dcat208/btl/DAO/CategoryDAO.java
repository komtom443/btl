package com.b19dcat208.btl.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.b19dcat208.btl.Entity.Category;
public class CategoryDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    
    private static final String SELECT_ALL_CATEGORY  = "select * from categories";
    private static final String SELECT_CATEGORY_BY_ID= "select * from categories where id = ?";
    private static final String SELECT_CATEGORY_ID   = "select id from book";
    private static final String INSERT_CATEGORY      = "insert into categories values(?,?,0)";
    private static final String UPDATE_CATEGORY      = "update categories set name = ? where id = ?";
    private static final String DELETE_CATEGORY      = "delete from book where id=?";
    public CategoryDAO(){}


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

    public List<Category> getCategories()
    {
        List<Category> categories = new ArrayList<>();
        
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CATEGORY);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                String categoryID = resultSet.getString("id");
                String categoryName = resultSet.getString("name");
                int categoryCount = resultSet.getInt("count");
                Category tmp = new Category(categoryID,categoryName,categoryCount);
                categories.add(tmp);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return categories;
    }

    public Category getCategory(String categoryID)
    {
        Category category = new Category();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CATEGORY_BY_ID);
            preparedStatement.setString(1, categoryID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                String categoryName = resultSet.getString("name");
                int categoryCount = resultSet.getInt("count");
                category = new Category(categoryID,categoryName,categoryCount);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return category;
    }

    public boolean insertCategory(String categoryName)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CATEGORY_BY_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while(resultSet.next())
            {
                String tmp = resultSet.getString("id");
                if(i != Integer.parseInt(tmp.substring(tmp.length()-3)))
                {
                    break;
                }
                i += 1;
            }
            String categoryID = String.format("CATE%03d", i);
            preparedStatement = connection.prepareStatement(INSERT_CATEGORY);
            preparedStatement.setString(1, categoryID);
            preparedStatement.setString(1, categoryName);
            preparedStatement.executeUpdate();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
