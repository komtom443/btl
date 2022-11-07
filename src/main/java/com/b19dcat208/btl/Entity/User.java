package com.b19dcat208.btl.Entity;

public class User {
    private String id;
    private String name;
    private String dob;
    private String email;
    public User(String id, String name, String dob, String email) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
    }
    public User() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String toJson()
    {
        return "{\"id\":\""+id+"\",\"name\":\""+name+"\",\"dob\":\""+dob+"\",\"email\":\""+email+"\"}";
    }
}
