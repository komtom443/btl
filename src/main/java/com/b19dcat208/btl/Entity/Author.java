package com.b19dcat208.btl.Entity;

public class Author {
    private String id;
    private String name;
    private String email;
    private int count;
    public Author(String id, String name, String email,int count) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.count = count;
    }
    public Author() {
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String toJson()
    {
        return "{\"id\":\""+this.id+"\",\"name\":\""+this.name+"\",\"email\":\""+this.email+"\",\"count\":"+this.count+"}";
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
