package com.Entity;


import java.util.*;

public class Mashup {

    public Mashup(String url , Set<String> categories,String description){
        this.url = url;
        this.categories = categories;
        keywords = new HashSet<>();
        results = new LinkedList<>();
        this.description = description;
    }
    public Mashup(String url){
        this.url = url;
        results = new LinkedList<>();
    }
    public String url;
    public Set<String> categories;
    public Set<String> keywords;
    public LinkedList<Result> results;
    public String description ;
}
