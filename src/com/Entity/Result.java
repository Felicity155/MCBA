package com.Entity;

import java.util.LinkedList;

public class Result {
    public LinkedList<String> detail;//api组合
    public Double node_score;
    public Double edge_score;
    public Double mean_score;
    public Double score;

    public Result(LinkedList<String> detail){
        this.detail = detail ;
        this.node_score = 0.0;
        this.edge_score = 0.0;
        this.mean_score = 0.0;
        this.score = 0.0;
    }

    public String getString(){
        String s = new String();
        for (String api : detail){
            s = s +"\t"+api;
        }
        return s;
    }

}
