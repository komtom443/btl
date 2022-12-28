package com.b19dcat208.btl.Controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.image.BufferedImage;

import com.b19dcat208.btl.DAO.AccountDAO;
import com.b19dcat208.btl.DAO.AuthorDAO;
import com.b19dcat208.btl.DAO.BookAuthorDAO;
import com.b19dcat208.btl.DAO.BookDAO;
import com.b19dcat208.btl.DAO.CartDAO;
import com.b19dcat208.btl.DAO.CategoryDAO;
import com.b19dcat208.btl.DAO.RateDAO;
import com.b19dcat208.btl.DAO.UserDao;
import com.b19dcat208.btl.Entity.Account;
import com.b19dcat208.btl.Entity.Author;
import com.b19dcat208.btl.Entity.Book;
import com.b19dcat208.btl.Entity.Cart;
import com.b19dcat208.btl.Entity.Category;
import com.b19dcat208.btl.Entity.User;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Controller
@CrossOrigin
public class btlController {
    private static Type mapType = new TypeToken<Map<String,Object>>(){}.getType();
    private static ArrayList<Map<String,String>> currentAccount = new ArrayList<>();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static SimpleDateFormat sqlDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat bookDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    protected String sessionIDValidate(String sessionID) throws ParseException
    {
        for(int i = 0; i < currentAccount.size();i++)
        {
            if(currentAccount.get(i).get("keyValue").equals(sessionID))
            {
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
    protected String imageToBase64(File inpuFile) throws IOException
    {
        if(!inpuFile.exists())
        {
            inpuFile = new File("bookData/noImage.png");
        }
        String encodedString = "";
        try
        {
            byte[] fileContent = FileUtils.readFileToByteArray(inpuFile);
            encodedString = Base64.getEncoder().encodeToString(fileContent);
        }
        catch(FileNotFoundException e)
        {
            encodedString = "";
        }
        return "data:image/png;base64,"+encodedString;
    }

    protected String nullValidate(Scanner sc)
    {
        String tmp = "";
        do
        {
            tmp = sc.nextLine();
        }
        while(tmp.equals(""));
        return tmp;
    }
    @PostMapping("/loginProcess")
    @ResponseBody
    public String loginAccount(Model model,@RequestBody String req,HttpServletRequest request)
    {
        Gson gson          = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        AccountDAO accountDAO = new AccountDAO();
        String username    = outputMap.get("username");
        String passwd      = Hashing.sha256().hashString(outputMap.get("passwd"), StandardCharsets.UTF_8).toString();
        int mode = accountDAO.loginAccount(username, passwd);
        if(mode != -1)
        {
            String key = Hashing.sha256().hashString(username, StandardCharsets.UTF_8).toString();
            Date createTime = new Date();
            Map<String,String> sessionID = new HashMap<>();
            sessionID.put("keyValue", key);
            sessionID.put("createTime", simpleDateFormat.format(createTime));
            sessionID.put("username", username);
            sessionID.put("mode", mode+"");
            currentAccount.add(sessionID);
            return "{\"login\":true,\"key\":\""+key+"\",\"mode\":"+mode+"}";
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
        if(!accountDAO.usernameValidate(outputMap.get("username")))
        {
            return "{\"login\":false,\"error\":1}";
        };
        accountDAO.lastAccountID();
        String pass = Hashing.sha256().hashString(outputMap.get("passwd"), StandardCharsets.UTF_8).toString();
        boolean result = accountDAO.createAccount(new Account(accountDAO.lastAccountID(), outputMap.get("username"),pass, 1, 0,""));
        return "{\"login\":"+result+"}";
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
        model.addAttribute("mode", 0);
        return"bookDetail";
    }

    @GetMapping("/profile")
    public String getProfile()
    {
        return "profile";
    }

    @PostMapping("api/checkSessionID")
    @ResponseBody
    public String checkSessionID(@RequestBody String req,Model model) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        return "{\"status\":true}";
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
                System.out.println("LOGOUT");
            }
        }
    }

    @PostMapping("/api/book/{bookID}/updateAuthors")
    @ResponseBody
    public String updateBookAuthors(@RequestBody String req,@PathVariable String bookID) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        System.out.println(outputMap.get("newAuthor"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
        AuthorDAO authorDAO = new AuthorDAO();
        String author = outputMap.get("author");
        if(!outputMap.get("newAuthor").equals(""))
        {
            for(String i : outputMap.get("newAuthor").split(";"))
            {
                
                if(author.equals(""))
                {
                    author += authorDAO.createAuthorByName(i);
                }
                else
                {
                    author += ";"+ authorDAO.createAuthorByName(i);
                }
            }
        }
        
        bookAuthorDAO.updateAuthor(author, bookID);
        return "{\"status\":true,\"success\":false}";
    }

    @Autowired
    ServletContext application;

    @GetMapping("/api/book/{bookID}/description")
    @ResponseBody
    public String getBookDescription(@PathVariable String bookID) throws FileNotFoundException,IOException
    {
        String content = "";
            File file = new File("bookData/description/"+bookID.toLowerCase()+".txt");
        if(file.exists())
        {
            content = new String(Files.readAllBytes(file.toPath()));
        }
        else
        {
            content = "";
        }
        
        System.out.println(content);
        return "{\"data\":\""+content+"\"}";
    }

    
    @PostMapping("/api/book/{bookID}/description/upload")
    @ResponseBody
    public String uploadBookDescription(@RequestBody String req, @PathVariable String bookID) throws ParseException, IOException
    {
        File file;
        String path = "bookData/description/"+bookID.toLowerCase()+".txt";
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        System.out.println(outputMap.get("data"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        try {
            file = ResourceUtils.getFile(path);
        } catch (FileNotFoundException e) {
            file = new File(path);
            file = ResourceUtils.getFile(path);
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(outputMap.get("data"));
        fileWriter.close();
        return "{\"status\":true}";
    }

    @PostMapping("/api/book/{bookID}/attribute/upload")
    @ResponseBody
    public String uploadAttribute(@PathVariable String bookID, @RequestBody String req) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        BookDAO bookDAO = new BookDAO();
        System.out.println(outputMap.get("data"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        bookDAO.uploadAttribute(outputMap.get("attribute"),outputMap.get("data"),bookID);
        return "{\"status\":true}";
    }
    
    @PostMapping("/api/book/{bookID}/image/upload")
    @ResponseBody
    public String uploadImage(@PathVariable String bookID, @RequestBody String req) throws ParseException,IOException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        };
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(outputMap.get("fileData"));
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        File outputfile = new File("bookData/image/"+bookID +"."+ "png");
        ImageIO.write(image, "png", outputfile);
        return "{\"status\":true}";
    }

    @GetMapping("/api/image/{bookID}")
    @ResponseBody
    public String getImagebyID(@PathVariable String bookID) throws ParseException,IOException
    {
        File inpuFile = new File("bookData/image/"+bookID +"."+ "png");
        String encodedString = "";
        encodedString = imageToBase64(inpuFile);
        return "{\"data\":\""+encodedString+"\"}";
    }

    @GetMapping("/api/image")
    @ResponseBody
    public String getImage() throws IOException
    {
        String resp = "";
        File folder = new File("bookData/image/");
        System.out.println(folder.length());
        if(folder.listFiles().length == 0)
        {
            return "{}";
        }
        for(File i : folder.listFiles())
        {
            resp += ",\""+i.getName().split("\\.")[0]+"\":\""+imageToBase64(i)+"\"";
            
        }
        return "{"+resp.substring(1)+"}";
    }

    @PostMapping("api/book/delete")
    @ResponseBody
    public String deleteBook(@RequestBody String req) throws ParseException
    {
        Gson gson   = new Gson();
        AccountDAO accountDAO = new AccountDAO();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        String passwd      = Hashing.sha256().hashString(outputMap.get("passwd"), StandardCharsets.UTF_8).toString();
        int mode = accountDAO.loginAccount(username, passwd);
        if(mode != 1)
        {
            return "{\"status\":false}";
        }
        BookAuthorDAO bookAuthorDAO = new BookAuthorDAO();
        BookDAO bookDAO = new BookDAO();
        String bookID = outputMap.get("bookID");
        bookDAO.deleteBook(bookID);
        bookAuthorDAO.deleteBook(bookID);
        File file = new File("bookData/image/"+bookID +"."+ "png");
        if(file.exists())
        {
            file.delete();
        }
        file = new File("bookData/description/"+bookID.toLowerCase()+".txt");
        if(file.exists())
        {
            file.delete();
        }
        return "{\"status\":true}";
    }

    @GetMapping("/book/new")
    public String newBook(Model model)
    {
        BookDAO bookDAO = new BookDAO();
        Date date = new Date();
        String bookID = bookDAO.createBook(bookDateFormat.format(date));
        File file = new File("bookData/image/"+bookID +"."+ "png");
        if(file.exists())
        {
            file.delete();
        }
        file = new File("bookData/description/"+bookID.toLowerCase()+".txt");
        if(file.exists())
        {
            file.delete();
        }
        model.addAttribute("bookID",bookID);
        model.addAttribute("mode", 1);
        return"bookDetail";
    }
    @PostMapping("/api/book/create")
    @ResponseBody
    public String confirmCreateBook(@RequestBody String req) throws ParseException,IOException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        System.out.println(outputMap.get("data"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        BookDAO bookDAO = new BookDAO();
        bookDAO.confirmCreate(outputMap.get("bookID"));
        return "{\"status\":true}";
    }

    @GetMapping("/api/book/{bookID}/comment")
    @ResponseBody
    public String getComment(@PathVariable String bookID)
    {
        String comment = "";
        File file = new File("bookData/comment/"+bookID.toLowerCase()+".txt");
        if(file.exists())
        {
            try
            {
                Scanner scanner = new Scanner(file);
                while(scanner.hasNextLine())
                {
                    String name = nullValidate(scanner);
                    String data = "",time = nullValidate(scanner),tmp;
                    int lineCount = 0;
                    while(true)
                    {
                        tmp = scanner.nextLine();
                        System.out.println(tmp);
                        if(tmp.equals("endComment"))
                        {
                            break;
                        }
                        data += ",\""+lineCount+"\":\""+tmp+"\"";
                        lineCount++;
                    }
                    data = data.substring(1);
                    comment += ",{\"date\":\""+time+"\",\"name\":\""+name+"\",\"data\":{"+data+"}}";
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            return "{}";
        }
        return "["+comment.substring(1)+"]";
    }

    @PostMapping("api/book/{bookID}/comment/upload")
    @ResponseBody
    public String uploadComment(@PathVariable String bookID,@RequestBody String req) throws ParseException,IOException
    {
        File file;
        String path = "bookData/comment/"+bookID.toLowerCase()+".txt";
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        System.out.println(outputMap.get("data"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        try {
            file = ResourceUtils.getFile(path);
        } catch (FileNotFoundException e) {
            file = new File(path);
            file = ResourceUtils.getFile(path);
        }
        UserDao userDao = new UserDao();
        Date date = new Date();
        FileWriter fileWriter = new FileWriter(file,true);
        fileWriter.append("\n"+userDao.getUserName(username)+"\n");
        fileWriter.append(bookDateFormat.format(date)+"\n");
        fileWriter.append(outputMap.get("data")+"\n");
        fileWriter.append("endComment");
        fileWriter.close();
        return "{\"status\":true}";
    }

    @PostMapping("api/book/delete/image")
    @ResponseBody
    public String deleteImg(@RequestBody String req) throws ParseException
    {
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        System.out.println(outputMap.get("data"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        String bookID = outputMap.get("bookID");
        File file = new File("bookData/image/"+bookID +"."+ "png");
        if(file.exists())
        {
            file.delete();
        }
        return "{\"status\":true}";
    }
    @GetMapping("/api/description")
    @ResponseBody
    public String getDescription() throws IOException
    {
        Scanner scanner;
        String resp = "";
        File folder = new File("bookData/description/");
        System.out.println(folder.length());
        if(folder.listFiles().length == 0)
        {
            return "{}";
        }
        for(File i : folder.listFiles())
        {
            scanner = new Scanner(i);
            String tmp = "";
            int line = 0;
            while(scanner.hasNextLine())
            {
                tmp += ",\""+line+"\":\""+scanner.nextLine()+"\"";
            }
            if(tmp.equals(""))
            {
                tmp = ",\"0\":\"\"";
            }
            resp += ",\""+i.getName().split("\\.")[0].toUpperCase()+"\":{"+tmp.substring(1)+"}";
            
        }
        return "{"+resp.substring(1)+"}";
    }

    @GetMapping("/api/book/{bookID}/rate")
    @ResponseBody
    public String getBookRate(@PathVariable String bookID)
    {
        RateDAO rateDAO = new RateDAO();
        int[] rate = rateDAO.getRateByBookID(bookID);
        String tmp = "";
        for(int i = 1; i < rate.length;i++)
        {
            tmp += ","+rate[i];
        }
        return "{\"avg\":"+rate[0]+",\"detail\":["+tmp.substring(1)+"]}";
    }

    @GetMapping("/api/book/rate")
    @ResponseBody
    public String getBooksRate()
    {
        String tmp ="";
        RateDAO rateDAO = new RateDAO();
        for(String i : rateDAO.getBooksRate())
        {
            tmp += ","+i;
        }
        return "{"+tmp.substring(1)+"}";
    }

    @PostMapping("/api/book/{bookID}/rate/upload")
    @ResponseBody
    public String upLoadRate(@PathVariable String bookID,@RequestBody String req) throws ParseException
    {
        System.out.println(req);
        Gson gson   = new Gson();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        System.out.println(outputMap.get("data"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        RateDAO rateDAO = new RateDAO();
        rateDAO.uploadRate(username, bookID, Float.parseFloat(outputMap.get("data")));
        return "{\"status\":true}";
    }
    @GetMapping("/cart")
    public String goCart()
    {
        return "cart";
    }

    @PostMapping("/api/cart")
    @ResponseBody
    public String getCart(@RequestBody String req) throws ParseException
    {
        String tmp = "";
        System.out.println(req);
        Gson gson   = new Gson();
        AccountDAO accountDAO = new AccountDAO();
        CartDAO cartDAO = new CartDAO();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        String userID = accountDAO.getAccountByUsername(username).getUserID();
        for(Cart i : cartDAO.getItem(userID)){
            tmp += "," + i.toJson();
        };
        if(tmp.equals(""))
        {
            return "[]";
        }
        return "["+tmp.substring(1)+"]";
    }

    @PostMapping("/api/cart/delete")
    @ResponseBody
    public String deleteCart(@RequestBody String req) throws ParseException
    {
        System.out.println(req);
        Gson gson   = new Gson();
        CartDAO cartDAO = new CartDAO();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        cartDAO.deleteItem(Integer.parseInt(outputMap.get("value")));
        return "{\"status\":true}";
    }
    @PostMapping("/api/cart/new/")
    @ResponseBody
    public String newCart(@RequestBody String req) throws ParseException
    {
        System.out.println(req);
        Gson gson   = new Gson();
        CartDAO cartDAO = new CartDAO();
        AccountDAO accountDAO = new AccountDAO();
        Map<String,String> outputMap = gson.fromJson(req, mapType);
        String username = sessionIDValidate(outputMap.get("key"));
        if(username.equals(""))
        {
            return "{\"status\":false}";
        }
        String userID = accountDAO.getAccountByUsername(username).getUserID();
        Date A = new Date();
        cartDAO.insertItem(outputMap.get("bookID"),userID,Integer.parseInt(outputMap.get("value")),sqlDateTime.format(A),Integer.parseInt(outputMap.get("mode")));
        return "{\"status\":true}";
    }
}
