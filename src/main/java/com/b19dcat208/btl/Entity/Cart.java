package com.b19dcat208.btl.Entity;

public class Cart {
    private Book book;
    private int value;
    private int id;
    private String date;
    public Cart( int id,Book book, int value, String date) {
        this.book = book;
        this.value = value;
        this.id = id;
        this.date = date;
    }

    public Cart() {
    }
    
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public String toJson()
    {
        return "{\"id\":"+id+",\"book\":"+book.toJson()+",\"value\":"+value+",\"date\":\""+date+"\"}";
    }
}
