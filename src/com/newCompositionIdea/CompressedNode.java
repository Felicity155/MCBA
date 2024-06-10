package com.newCompositionIdea;

import java.util.*;

public class CompressedNode {
    public CompressedNode(LinkedList<Api> api_set) {
        this.c = new HashSet<>();
        this.adj = new HashSet<>();
        this.parent = new HashSet<>();
        this.api_set = new HashSet<>();

        String u = new String();
        this.api_set.addAll(api_set);
        for (Api a : this.api_set) {
            u = u+a.url;
            c.add(a.c);
            adj.addAll(a.adj);
            parent.addAll(a.parent);
        }
        this.url = u;
    }

    public String url;//节点的url标识

    public Set<String> c;//类别
    public Set<String> adj;
    public Set<String> parent;
    public Set<Api> api_set;

    public void update(Map<String,String> url_url){
        Set<String> new_adj = new HashSet<>();
        for(String a : adj){
            new_adj.add(url_url.get(a));
        }
        Set<String> new_parent = new HashSet<>();
        for(String p : parent){
            new_parent.add(url_url.get(p));
        }

        adj.clear();
        adj.addAll(new_adj);
        parent.clear();
        parent.addAll(new_parent);
    }

}
