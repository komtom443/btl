package com.b19dcat208.btl.DAO;

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
import com.b19dcat208.btl.Entity.Category;


public class BookDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    
    private static final String SELECT_ALL_BOOK  = "select * from book";
    private static final String SELECT_BOOK      = "select * from book where id=?";
    private static final String CHECK_BOOK       = "select count(username) from book where username=?";
    private static final String INSERT_BOOK      = "insert into book values(?,?,?,?,?)";
    private static final String UPDATE_BOOK      = "update book set username=?,passwd=?,status=?";
    private static final String DELETE_BOOK      = "delete from book where id=?";
    private static final String LAST_BOOK_ID     = "select id from book order by id desc limit 1";
    public BookDAO(){}
    
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

    public List<Book> getBooks()
    {
        List<Book> books = new ArrayList<Book>();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BOOK);
            ResultSet resultSet = preparedStatement.executeQuery();
            CategoryDAO categoryDAO = new CategoryDAO();
            BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
            while(resultSet.next())
            {
                String bookID = resultSet.getString("id");
                String bookName = resultSet.getString("title");
                Category bookCate = categoryDAO.getCategory(resultSet.getString("category"));
                String bookDate = resultSet.getString("date");
                int bookPage = resultSet.getInt("page");
                List<Author> authors = bookAuthorDAO.getAuthor(bookID);
                books.add(new Book(bookID,bookName,bookCate,bookDate,bookPage,authors));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Collections.sort(books);
        return books;
    }

    public Book getBook(String bookID)
    {
        Book book = new Book();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK);
            preparedStatement.setString(1, bookID);
            ResultSet resultSet = preparedStatement.executeQuery();
            CategoryDAO categoryDAO = new CategoryDAO();
            BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
            while(resultSet.next())
            {
                bookID = resultSet.getString("id");
                String bookName = resultSet.getString("title");
                Category bookCate = categoryDAO.getCategory(resultSet.getString("category"));
                String bookDate = resultSet.getString("date");
                int bookPage = resultSet.getInt("page");
                List<Author> authors = bookAuthorDAO.getAuthor(bookID);
                book = new Book(bookID,bookName,bookCate,bookDate,bookPage,authors);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return book;
    }

    public List<Book> getBooksByAttribute(String attribute, String value)
    {
        List<Book> books = new ArrayList<>();
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from book where "+attribute+" = \""+value+"\"");
            ResultSet resultSet = preparedStatement.executeQuery();
            CategoryDAO categoryDAO = new CategoryDAO();
            BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
            while(resultSet.next())
            {
                String bookID = resultSet.getString("id");
                String bookName = resultSet.getString("title");
                Category bookCate = categoryDAO.getCategory(resultSet.getString("category"));
                String bookDate = resultSet.getString("date");
                int bookPage = resultSet.getInt("page");
                List<Author> authors = bookAuthorDAO.getAuthor(bookID);
                books.add(new Book(bookID,bookName,bookCate,bookDate,bookPage,authors));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Collections.sort(books);
        return books;
    }

    public List<Book> getBooksByAuthor(String authorID)
    {
        List<Book> books = new ArrayList<>();
        BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
        for( String i : bookAuthorDAO.getBook(authorID))
        {
            books.add(getBook(i));
        }
        Collections.sort(books);
        return books;
    }
}
