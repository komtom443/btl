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
import com.b19dcat208.btl.Entity.Category;


public class BookDAO {
    private String jdbcURL      = "jdbc:mysql://localhost:3306/jdbc_demo";
    private String jdbcUsername = "root";
    private String jdbcPasswd   = "12345";
    private static final String SELECT_ALL_BOOK  = "select * from book where detail=?";
    private static final String SELECT_ALL_BOOK1  = "select * from book where detail=\"old\"";
    private static final String SELECT_BOOK      = "select * from book where id=? and detail=\"old\"";
    private static final String SELECT_BOOK_ID   = "select id from book";
    private static final String UNCOMPLETE_BOOK1  = "select count(id) from book where detail = \"new\" order by id limit 1";
    private static final String UNCOMPLETE_BOOK2  = "select id from book where detail = \"new\"";
    private static final String CHECK_BOOK       = "select count(username) from book where username=?";
    private static final String INSERT_BOOK      = "insert into book values(?,?,?,?,?,?)";
    private static final String UPDATE_BOOK      = "update book set title=?,category=?,page=?,detail=?,date=? where id = ?";
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
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BOOK1);
            //preparedStatement.setString(1, "old");
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

    public String getNewID()
    {
        int i = 1;
        Connection connection = getConnection();
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next())
            {
                String tmp = resultSet.getString("id");
                if(Integer.parseInt(tmp.substring(tmp.length()-3)) != i)
                {
                    break;
                }
                i++;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(String.format("BOOK%03d", i));
        return String.format("BOOK%03d", i);

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
            System.out.println("|id: "+bookID+"|");
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
            PreparedStatement preparedStatement = connection.prepareStatement("select * from book where "+attribute+" = \""+value+"\" and detail=\"old\"");
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
            System.out.println("|id: "+i+"|");
            System.out.println("|id_after: "+getBook(i).getId()+"|");
            ;
            books.add(getBook(i));
        }

        Collections.sort(books);
        return books;
    }

    public Boolean uploadAttribute(String attribute,String value,String bookID)
    {
        Connection connection = getConnection();
        System.out.println(attribute);
        System.out.println(value);
        System.out.println(bookID);
        System.out.println("update book set" + attribute + "=? where id=\""+bookID+"\"");
        try {
            PreparedStatement preparedStatement  = connection.prepareStatement("update book set " + attribute + "=? where id=\""+bookID+"\"");
            if(attribute.equals("page"))
            {
                preparedStatement.setInt(1, Integer.parseInt(value));
            }
            else
            {
                preparedStatement.setString(1, value);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            // TODO: handle exception
        }
        

        return true;
    }

    public void deleteBook(String bookID)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK);
            preparedStatement.setString(1, bookID);
            preparedStatement.executeUpdate();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public String createBook(String date)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UNCOMPLETE_BOOK1);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int countID = resultSet.getInt(1);
            if( countID>= 1)
            {
                preparedStatement = connection.prepareStatement(UNCOMPLETE_BOOK2);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                preparedStatement = connection.prepareStatement(UPDATE_BOOK);
                BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
                bookAuthorDAO.deleteBook(resultSet.getString("id"));
                preparedStatement.setString(6, resultSet.getString("id"));
                preparedStatement.setString(1, resultSet.getString("id"));
                preparedStatement.setString(2, "");
                preparedStatement.setString(4, "new");
                preparedStatement.setString(5, date);
                preparedStatement.setInt(3, 1);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return resultSet.getString("id");
            }
            else
            {
                String bookID = getNewID();
                preparedStatement = connection.prepareStatement(INSERT_BOOK);
                preparedStatement.setString(1, bookID);
                preparedStatement.setString(2, bookID);
                preparedStatement.setString(3, "");
                preparedStatement.setString(5, "new");
                preparedStatement.setString(6, date);
                preparedStatement.setInt(4, 1);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return bookID;
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return getNewID();
    }

    public boolean confirmCreate(String bookID)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("update book set detail=? where id = ?");
            preparedStatement.setString(1, "old");
            preparedStatement.setString(2, bookID);
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
