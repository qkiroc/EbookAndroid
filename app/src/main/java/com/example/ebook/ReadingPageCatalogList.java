package com.example.ebook;

public class ReadingPageCatalogList {
    private String title;
    private Integer pagenumber;
    private Boolean isnowsection;
    public ReadingPageCatalogList(String title, Integer pagenumber, boolean isnowsection) {
        this.title = title;
        this.pagenumber = pagenumber;
        this.isnowsection = isnowsection;
    }
    public String getTitle(){
        return title;
    }
    public Integer getPagenumber(){
        return pagenumber;
    }
    public Boolean getIsnowsection(){
        return isnowsection;
    }
}
