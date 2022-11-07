package com.b19dcat208.btl.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.b19dcat208.btl.Entity.Author;

import java.util.HashMap;


public class AuthorDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    
    private static final String SELECT_ALL_AUTHOR  = "select * from authors";
    private static final String SELECT_AUTHOR_BY_ID= "select * from authors where id = ?";
    private static final String INSERT_AUTHOR      = "insert into authors values(?,?)";
    private static final String UPDATE_AUTHOR      = "update authors set name = ? where id = ?";
    private static final String DELETE_AUTHOR      = "delete from book where id=?";
    private static final String LAST_AUTHOR_ID     = "select id from book order by id desc limit 1";
    public AuthorDAO(){}


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

    public List<Author> selectAuthors()
    {
        List<Author> authors = new ArrayList<Author>();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_AUTHOR);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                String authorID = resultSet.getString("id");
                String authorName = resultSet.getString("name");
                String authorEmail = resultSet.getString("email");
                int authorCount = resultSet.getInt("count");
                authors.add(new Author(authorID,authorName,authorEmail,authorCount));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return authors;
    }

    public Author selectAuthor(String authorID)
    {
        Author author = new Author();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AUTHOR_BY_ID);
            preparedStatement.setString(1, authorID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                String authorName = resultSet.getString("name");
                String authorEmail = resultSet.getString("email");
                int authorCount = resultSet.getInt("count");
                author =  new Author(authorID,authorName,authorEmail,authorCount);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return author;
    }
}
