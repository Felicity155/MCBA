package com.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Node {
    public Node(String tp,String url,String name, String st,String et,String oet,
    String c,String oac,String ac){
        this.tp = tp;
        this.url = url;
        this.name = name;
        this.st = st;
        this.et = et;
        this.oet = oet;
        this.c = c;
        this.oac =oac;
        this.ac =ac;
        description = new String();
        adj = new HashMap<>();
        parent = new HashMap<>();
        been = false;
        tags = new HashSet<>();
    }
    public String tp;//类型。API或Mashup
    public String url;//API或Mashup的url
    public String name;//API或Mashup的名称
    public String st;//提交日期
    public String et;//纠正死亡日期
    public String oet;//PW中提供的死亡日期
    public String c;//类别
    public String oac;//纠正可达性
    public String ac;//可访问PW
    public Map<String,Integer> adj;
    public Map<String,Integer> parent;
    public Boolean been;
    public Set<String> tags;
    public String description;

    public Integer countTimes(){
        int num = 0;
        for(String s:adj.keySet()){
            num = num + adj.get(s);
        }
        return num;

    }


}
