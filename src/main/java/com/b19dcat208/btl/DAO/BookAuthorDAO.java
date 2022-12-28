package com.b19dcat208.btl.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.b19dcat208.btl.Entity.Author;
import com.b19dcat208.btl.Entity.Book;

public class BookAuthorDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    private static final String SELECT_BOOK_BY_AUTHOR_ID = "select bookid from book_author where authorid = ?";
    private static final String SELECT_AUTHOR_BY_BOOK_ID = "select authorid from book_author where bookid = ?";
    private static final String CREATE_NEW = "insert into book_author values(?,?,?)";
    private static final String DELETE_BY_BOOK_ID = "delete from book_author where bookid = ?";
    private static final String SELECT_LAST_ID = "select id from book_author order by id desc limit 1";
    private static final String SELECT_AUTHOR_BOOK_NUMBER = "select count(bookid) from book_author where authorid = ?";
    
    
    public BookAuthorDAO(){}


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

    public List<Author> getAuthor(String bookID)
    {
        List<Author> authors = new ArrayList<>();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AUTHOR_BY_BOOK_ID);
            preparedStatement.setString(1, bookID);
            ResultSet resultSet = preparedStatement.executeQuery();
            AuthorDAO authorDAO = new AuthorDAO();
            while(resultSet.next())
            {
                authors.add(authorDAO.selectAuthor(resultSet.getString("authorid")));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return authors;
    }

    public List<String> getBook(String authorID)
    {
        List<String> books = new ArrayList<>();
        try
        {
            System.out.println("|author_id: "+authorID+"|");
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_AUTHOR_ID);
            preparedStatement.setString(1, authorID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                books.add(resultSet.getString("bookid"));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return books;
    }

    public int getAuthorBookCount(String authorID)
    {
        int num = 0;
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AUTHOR_BOOK_NUMBER);
            preparedStatement.setString(1, authorID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                num = resultSet.getInt(1);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return num;
    }

    public void updateAuthor(String authorIDList,String bookID)
    {
        
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_BOOK_ID);
            preparedStatement.setString(1, bookID);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(SELECT_LAST_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int lastID = resultSet.getInt("id");
            System.out.println(lastID);
            preparedStatement = connection.prepareStatement(CREATE_NEW);
            preparedStatement.setString(2, bookID);
            for(String i : authorIDList.split(";"))
            {
                if(!i.equals(""))
                {
                    preparedStatement.setInt(1, ++lastID);
                    preparedStatement.setString(3, i);
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void deleteBook(String bookID)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_BOOK_ID);
            preparedStatement.setString(1, bookID);
            preparedStatement.executeUpdate();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
