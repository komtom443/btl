package com.b19dcat208.btl.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.b19dcat208.btl.DAO.AccountDAO;
import com.b19dcat208.btl.DAO.AuthorDAO;
import com.b19dcat208.btl.DAO.BookAuthorDAO;
import com.b19dcat208.btl.DAO.BookDAO;
import com.b19dcat208.btl.DAO.CategoryDAO;
import com.b19dcat208.btl.DAO.UserDao;
import com.b19dcat208.btl.Entity.Account;
import com.b19dcat208.btl.Entity.Author;
import com.b19dcat208.btl.Entity.Book;
import com.b19dcat208.btl.Entity.Category;
import com.b19dcat208.btl.Entity.User;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
public class btlController {
    private static Type mapType = new TypeToken<Map<String,Object>>(){}.getType();
    private static ArrayList<Map<String,String>> currentAccount = new ArrayList<>();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    protected String sessionIDValidate(String sessionID) throws ParseException
    {
        for(int i = 0; i < currentAccount.size();i++)
        {
            System.out.println(currentAccount.get(i).get("keyValue"));
            System.out.println(sessionID);
            if(currentAccount.get(i).get("keyValue").equals(sessionID))
            {
                System.out.println(currentAccount.get(i).get("keyValue"));
                Date timeNow = new Date();
                if(Math.abs(simpleDateFormat.parse(currentAccount.get(i).get("createTime")).getTime() - timeNow.getTime()) <= 36000000)
                {
                    currentAccount.get(i).put("createTime", simpleDateFormat.format(timeNow));
                    return currentAccount.get(i).get("username");
                }
                else
                {
                    currentAccount.remove(i);
                    return "";
                }
            }
        }
        return "";
    }
    @PostMapping("/loginProcess")
    @ResponseBody
    public String loginAccount(Model model,@RequestBody String req,HttpServletRequest request)
    {
        Gson gson          = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        AccountDAO accountDAO = new AccountDAO();
        String username    = outputMap.get("username");
        String passwd      = Hashing.sha256().hashString(outputMap.get("passwd"), StandardCharsets.UTF_8).toString();;
        if(accountDAO.loginAccount(username, passwd) == true)
        {
            String key = Hashing.sha256().hashString(username, StandardCharsets.UTF_8).toString();
            Date createTime = new Date();
            Map<String,String> sessionID = new HashMap<>();
            sessionID.put("keyValue", key);
            sessionID.put("createTime", simpleDateFormat.format(createTime));
            sessionID.put("username", username);
            currentAccount.add(sessionID);
            return "{\"login\":true,\"key\":\""+key+"\"}";
        }
        return "{\"login\":false}";
    }
    @GetMapping("/login")
    public String loginUI()
    {
        return "login";
    }
    @PostMapping("/create_account")
    @ResponseBody
    public String createAccount(Model model,@RequestBody String req,HttpServletRequest request)
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        AccountDAO accountDAO = new AccountDAO();
        accountDAO.lastAccountID();
        String pass = Hashing.sha256().hashString(outputMap.get("passwd"), StandardCharsets.UTF_8).toString();
        boolean result = accountDAO.createAccount(new Account(accountDAO.lastAccountID(), outputMap.get("username"),pass, 0, 0,""));
        return "{\"login\":\""+result+"\"}";
    }

    @GetMapping("/api/category")
    @ResponseBody
    public String getCategory()
    {
        String categories = "";
        CategoryDAO categoryDAO = new CategoryDAO();
        categoryDAO.getCategories();
        for(Category i : categoryDAO.getCategories())
        {
            categories += ","+i.toJson();
            //categories = i.toJson();
        }
        return "["+categories.substring(1)+"]";
        //return categories;
    }
    
    @GetMapping("/api/author")
    @ResponseBody
    public String getAuthor()
    {
        String authorsJson = "";
        AuthorDAO authorDAO = new AuthorDAO();
        for(Author i : authorDAO.selectAuthors())
        {
            authorsJson += ","+i.toJson();
        }
        return "[" + authorsJson.substring(1) + "]";
    }

    @GetMapping("/api/author/{authorID}")
    @ResponseBody
    public String getAuthorByID(@PathVariable String authorID)
    {
        AuthorDAO authorDAO = new AuthorDAO();
        Author author = authorDAO.selectAuthor(authorID);
        return author.toJson();
    }
    @GetMapping("/category/{categoryID}")
    public String showCategory(@PathVariable String categoryID,Model model)
    {
        model.addAttribute("categoryID", categoryID);
        model.addAttribute("state", 1);
        return "categoryDetail";
    }

    @GetMapping("/api/category/{categoryID}")
    @ResponseBody
    public String getCategory(@PathVariable String categoryID)
    {
        CategoryDAO categoryDAO = new CategoryDAO();
        Category category =  categoryDAO.getCategory(categoryID);
        return category.toJson();
    }

    @GetMapping("/api/book/get/category/{categoryID}")
    @ResponseBody
    public String getBookByCategory(@PathVariable String categoryID)
    {
        BookDAO bookDAO = new BookDAO();
        String resp = "";
        List<Book> books = bookDAO.getBooksByAttribute("category", categoryID);
        if(books.size() == 0)
        {
            return "{\"status\":1}";
        }
        for(Book tmp : books)
        {
            resp += "," + tmp.toJson();
        };
        return "["+resp.substring(1)+"]";
    }


    @GetMapping("/author/{authorID}")
    public String showAuthor(@PathVariable String authorID,Model model)
    {
        model.addAttribute("authorID", authorID);
        model.addAttribute("state", 0);
        return "categoryDetail";
    }

    


    @GetMapping("/api/book/get/author/{authorID}")
    @ResponseBody
    public String getBookByAuthor(@PathVariable String authorID)
    {
        BookDAO bookDAO = new BookDAO();
        String resp = "";
        List<Book> books = bookDAO.getBooksByAuthor(authorID);
        if(books.size() == 0)
        {
            return "{\"status\":1}";
        }
        for(Book tmp : books)
        {
            resp += "," + tmp.toJson();
        };
        return "["+resp.substring(1)+"]";
    }

    @PostMapping("/api/create/category")
    @ResponseBody
    public String createCategory(@RequestBody String req)
    {
        CategoryDAO categoryDAO = new CategoryDAO();
        if(categoryDAO.insertCategory(req) == true)
        {
            return "{\"status\":\"Tạo thể loại thành công\"}";
        }
        return "{\"status\":\"Tạo thể loại thất bại\"}";
    }

    @GetMapping("/api/book/")
    @ResponseBody
    public String getBooks()
    {
        String books="";
        BookDAO bookDAO = new BookDAO();
        for(Book i : bookDAO.getBooks())
        {
            books += ","+ i.toJson();
        }
        return "["+books.substring(1)+"]";
    }

    @GetMapping("/api/book/{bookID}")
    @ResponseBody
    public String getBook(@PathVariable String bookID)
    {
        BookDAO bookDAO = new BookDAO();
        return bookDAO.getBook(bookID).toJson();
    }

    @GetMapping("/book/{bookID}")
    public String uiBookDetail(@PathVariable String bookID,Model model)
    {
        model.addAttribute("bookID", bookID);
        return"bookDetail";
    }

    @GetMapping("/profile")
    public String getProfile()
    {
        return "profile";
    }

    @PostMapping("api/checkSessionID")
    @ResponseBody
    public String checkSessionID(@RequestBody String sessionID,Model model) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(sessionID, mapType);
        String tmp = outputMap.get("key");
        return "{\"status\":true}"; // Xóa ở bài chính
        // if(sessionIDValidate(tmp)==true)
        // {
        //     return "{\"status\":true}";
        // }
        // return "{\"status\":false}";
    }


    @PostMapping("api/accountDetail")
    @ResponseBody
    public String getAccount(@RequestBody String req,Model model) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        UserDao userDao = new UserDao();
        AccountDAO accountDAO = new AccountDAO();
        Account account = accountDAO.getAccountByUsername(username);
        User user = userDao.getUser(account.getUserID());
        return "{\"status\":true,\"accountData\":"+account.toJson()+",\"userData\":"+user.toJson()+"}";
    }

    @PostMapping("api/update/user/attribute")
    @ResponseBody
    public String updateUserAttribute(@RequestBody String req) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        UserDao userDao = new UserDao();
        if(userDao.updateUser(outputMap.get("attribute"), outputMap.get("value"), outputMap.get("userid"))== true)
        {
            return "{\"status\":true,\"success\":true}";
        };
        return "{\"status\":true,\"success\":false}";
    }

    @PostMapping("/logout")
    @ResponseBody
    public void logout(@RequestBody String req)
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String key = outputMap.get("key");
        for(int i = 0; i < currentAccount.size();i++)
        {
            if(currentAccount.get(i).get("keyValue").equals(key))
            {
                currentAccount.remove(i);
            }
        }
    }

}
