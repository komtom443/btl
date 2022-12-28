package com.b19dcat208.btl.Entity;

import java.util.List;

public class Book implements Comparable<Book> {
    private String id;
    private String name;
    private Category category;
    private String date;
    private int page;
    private String detail;
    private List<Author> authors;
    public Book(String id, String name, Category category, String date, int page, String detail) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.page = page;
        this.detail = detail;
    }
    public Book(String id, String name, Category category, String date, int page, List<Author> authors) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.page = page;
        this.authors = authors;
    }
    public Book(String id, String name, Category category, String date, int page, String detail,
            List<Author> authors) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.page = page;
        this.detail = detail;
        this.authors = authors;
    }
    public Book() {
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
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public List<Author> getAuthors() {
        return authors;
    }
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String toJson()
    {
        String authors="";
        if(this.authors.size() != 0)
        {
            for(Author i : this.authors)
            {
                authors += ","+i.toJson();
            }
        }
        else
        {
            authors += ",";
        }
        
        authors = "["+authors.substring(1)+"]";
        return "{\"id\":\""+this.id+"\",\"name\":\""+this.name+"\",\"authors\":"+authors+",\"category\":"+this.category.toJson()+",\"date\":\""+this.date+"\",\"page\":"+this.page+"}";
    }

    @Override
    public int compareTo(Book o) {
        System.out.println(o.getName());
        if(this.id.compareTo(o.getId()) > 0)
        {
            return 1;
        }
        return -1;
    }
    
}
