package com.example.ebook;

public class ReadingPageBookmarkList {
    private String title;
    private String page;
    private String time;
    private String text;
    public ReadingPageBookmarkList(String title, String page, String time, String text){
        this.page = page;
        this.text = text;
        this.time = time;
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public String getPage(){
        return page;
    }
    public String getTime(){
        return time;
    }
    public String getText(){
        return text;
    }
}
