package com.example.ebook;

import java.util.Set;

public class SearchBookResultList {
    private String fileName;
    private Boolean isChecked;
    private String path;
    private Set<String> checkSet;
    private Boolean isfile;
    public  SearchBookResultList(String fileName, String path, Set<String> checkSet,Boolean isChecked,Boolean isfile) {
        this.fileName = fileName;
        this.isChecked = isChecked;
        this.path = path;
        this.checkSet = checkSet;
        this.isfile = isfile;
    }
    public String getFileName(){
        return fileName;
    }
    public String getPath(){
        return path;
    }
    public Set<String> getCheckSet(){
        return checkSet;
    }
    public Boolean getIsChecked(){
        return isChecked;
    }
    public Boolean getIsfile() {return isfile;}
}
