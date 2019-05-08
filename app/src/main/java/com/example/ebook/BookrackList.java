package com.example.ebook;

public class BookrackList {
    private String title;
    private String path;
    public Boolean ischeck;
    public String cover;
    public BookrackList(String title, String path, String cover,Boolean ischeck){
        this.title = title;
        this.path = path;
        this.ischeck = ischeck;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }
    public String getPath(){
        return path;
    }
    public String getCover() {return cover;}
    public Boolean getIscheck() {return ischeck;}
}
