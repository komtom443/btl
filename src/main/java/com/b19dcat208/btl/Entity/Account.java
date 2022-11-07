package com.b19dcat208.btl.Entity;

public class Account {
    private String id;
    private String username;
    private String passwd;
    private int status;
    private int mode;
    private String userID;
    public Account(String id, String username, int status, int mode, String userID) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.mode = mode;
        this.userID = userID;
    }
    public Account(String id, String username, String passwd, int status, int mode,String userID) {
        this.id = id;
        this.username = username;
        this.passwd = passwd;
        this.status = status;
        this.mode = mode;
        this.userID = userID;
    }
    public Account() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswd() {
        return passwd;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String toJson()
    {
        String status,mode;
        if(this.status == 1)
        {
            status = "Khả dụng";
        }
        else
        {
            status = "Vô hiệu hóa";
        }
        if(this.mode == 1)
        {
            mode = "Admin";
        }
        else
        {
            mode = "User";
        }
        return "{\"id\":\""+this.id+"\",\"username\":\""+this.username+"\",\"status\":\""+status+"\",\"mode\":\""+mode+"\"}";
    }
}
