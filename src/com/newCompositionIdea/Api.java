package com.newCompositionIdea;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Api {
    public Api(String url,String c){

        this.url = url;
        this.c = c;
        adj = new HashSet<>();
        parent = new HashSet<>();
    }


    public String url;//API或Mashup的url
    public String c;//类别

    public Set<String> adj;
    public Set<String> parent;





}
