package com.chazz.inventory2;

/**
 * Created by dilkov on 2/15/14.
 */
public class ListItem  {
    protected String title;
    protected Boolean isSeparator;
    protected String uri;

    public ListItem(String title, String uri, Boolean separator ) {
        this.title = title;
        this.isSeparator = separator;
        this.uri = uri;
    }
    public ListItem(String title, Boolean isSeparator) {
        this.title = title;
        this.isSeparator = isSeparator;
        this.uri="";
    }

    public ListItem(String title, String uri) {
        this.title = title;
        this.uri = uri;
        this.isSeparator = false;
    }

    public ListItem(String title) {
        this.title = title;
        this.uri = "";
        this.isSeparator = true;
    }

    public boolean isEnabled(){
        return !isSeparator;
    }

    public boolean isSeparator(){
        return isSeparator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
