package com.Entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {

    public Graph(String api_path, String m_a_path) {
        this.api_path = api_path;
        this.m_a_path = m_a_path;
        nodes = new HashMap<>();
        pairs = new HashMap<>();

        cs = new HashSet<>();
        times = new HashMap<>();

    }
    public Graph() {
        nodes = new HashMap<>();
        pairs = new HashMap<>();

        cs = new HashSet<>();
        times = new HashMap<>();
    }

    public String api_path;
    public String m_a_path;
    public Map<String, Node> nodes;
    public Set<String> cs;
    public Map<String,Map<String,Integer>> pairs;//api被联合调用的数据
    public Map<String, Integer> times;//api被调用次数



    //初始化构造图,无向图
    public void firstinit(){
        //加入所有节点
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(this.api_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split("\\t");
                cs.add(param[6]);
                nodes.put(param[1],
                        new Node(param[0],param[1],param[2],param[3],param[4],param[5],param[6],param[7],param[8]));
                //for(int i =0 ;i<param.length;i++){
                //    System.out.println(param[i]);
                //}
            }
            System.out.println("图的API节点一共有"+nodes.size()+"个");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String,Set<String>> e = new HashMap<>();//分类完毕的mashup数据
        //加入所有边
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流
        try {
            File file2 = new File(m_a_path);
            fr2 = new FileReader(file2);
            br2 = new BufferedReader(fr2);
            String data2 = null;


            while ((data2 = br2.readLine()) != null) {
                //循环读取每一行
                String[] param2 = data2.split("\\t");
                if(e.keySet().contains(param2[0])){
                    e.get(param2[0]).add(param2[1]);
                }else{
                    Set<String> temp = new HashSet<>();
                    temp.add(param2[1]);
                    e.put(param2[0],temp);
                }

            }
            System.out.println("图的边集(mashup-API信息)一共有"+e.size()+"条");
            //System.out.println("其中几组："+e);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        int edge_size = 0;
        for(Set<String> s:e.values()){
            if(s.size()>1){
                for(String n1:s){
                    for(String n2:s){
                        //System.out.println(n1);
                        //System.out.println(n2);
                        if(nodes.get(n1)!=null){
                            edge_size++;
                            if(n1!=n2 && nodes.get(n1).adj.containsKey(n2)){
                                int last = nodes.get(n1).adj.get(n2);
                                nodes.get(n1).adj.remove(n2);
                                nodes.get(n1).adj.put(n2,last+1);
                            }else if(n1!=n2 && !nodes.get(n1).adj.containsKey(n2)){
                                nodes.get(n1).adj.put(n2,1);
                            }
                        }else{
                            System.out.println("找不到此点"+n1);
                        }

                    }
                }
            }

        }
        System.out.println("边的数量为"+edge_size);

    }
    //初始化构造图,有向图，调用顺序有先后,使用mashup-api数据
    public void init(){
        //加入所有节点
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(this.api_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split("\\t");
                cs.add(param[6]);
                nodes.put(param[1],
                        new Node(param[0],param[1],param[2],param[3],param[4],param[5],param[6],param[7],param[8]));
                //for(int i =0 ;i<param.length;i++){
                //    System.out.println(param[i]);
                //}
            }
            System.out.println("图的API节点一共有"+nodes.size()+"个");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, LinkedList<String>> e = new HashMap<>();//分类完毕的mashup数据
        //加入所有边
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流

        try {
            File file2 = new File(m_a_path);
            fr2 = new FileReader(file2);
            br2 = new BufferedReader(fr2);
            String data2 = null;

            e = new HashMap<>();
            while ((data2 = br2.readLine()) != null) {
                //循环读取每一行
                String[] param2 = data2.split("\\t");
                if(e.keySet().contains(param2[0])){
                    e.get(param2[0]).offer(param2[1]);
                }else{
                    LinkedList<String> temp = new LinkedList<>();
                    temp.offer(param2[1]);
                    e.put(param2[0],temp);
                }

            }
            System.out.println("图的mashup-api一共有"+e.keySet().size()+"条");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        int edge_size = 0;
        int parent_size = 0 ;

        for(LinkedList<String> s:e.values()){
            if(s.size()>1){
                for(int i = 0 ;i < s.size()-1 ; i++){
                    String n1 = s.get(i);
                    String n2 = s.get(i+1);
                    edge_size++;
                    if(nodes.get(n1).adj.containsKey(n2)){
                        int last = nodes.get(n1).adj.get(n2);
                        nodes.get(n1).adj.remove(n2);
                        nodes.get(n1).adj.put(n2,last+1);
                    }else if(n1!=n2 && !nodes.get(n1).adj.containsKey(n2)){
                        nodes.get(n1).adj.put(n2,1);
                    }
                }
                for(int i = 1 ;i < s.size() ; i++){
                    String n1 = s.get(i);
                    String n2 = s.get(i-1);

                    if(nodes.get(n1).parent.containsKey(n2)){
                        int last = nodes.get(n1).parent.get(n2);
                        nodes.get(n1).parent.remove(n2);
                        nodes.get(n1).parent.put(n2,last+1);
                        parent_size++;
                    }else if(n1!=n2 && !nodes.get(n1).parent.containsKey(n2)){
                        nodes.get(n1).parent.put(n2,1);
                        parent_size++;
                    }
                }
            }
        }
        System.out.println("图的子关系一共有"+edge_size+"条");
        System.out.println("图的父关系一共有"+parent_size+"条");
    }
    //初始化构造图，有向图，根据历史调用数据api-pair
    public void secondinit(){
        //加入所有节点
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(this.api_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split("\\t");
                cs.add(param[6]);
                nodes.put(param[1],
                        new Node(param[0],param[1],param[2],param[3],param[4],param[5],param[6],param[7],param[8]));
                //for(int i =0 ;i<param.length;i++){
                //    System.out.println(param[i]);
                //}
            }
            System.out.println("图的API节点一共有"+nodes.size()+"个");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //加入所有边
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流
        int edge_size = 0;
        try {
            File file2 = new File(m_a_path);
            fr2 = new FileReader(file2);
            br2 = new BufferedReader(fr2);
            String data2 = null;

            while ((data2 = br2.readLine()) != null) {
                //循环读取每一行
                String[] param2 = data2.split("\\t");
                String n1 = param2[0];
                String n2 = param2[1];
                int frequency =  Integer.parseInt (param2[4]);
                nodes.get(n2).parent.put(n1,frequency);
                nodes.get(n1).adj.put(n2,frequency);
                edge_size++;
            }
            System.out.println("图的api-api pair信息一共有"+edge_size+"条");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    //已经初始化完毕的图，找最大子图
    public Set<String> maxG(){
        Set<String> result = new HashSet<>();
        Set<Set<String>> can = new HashSet<>();
        for(String url :nodes.keySet()){
            if(!nodes.get(url).been){
                Set<String> set = new HashSet<>();
                Set<String> newset = new HashSet<>();
                nodes.get(url).been = true;
                set.add(url);
                Boolean flag = true;
                while(flag){//循环直至无新的节点加入
                    flag = false;
                    int origin = set.size();
                    for(String s:set){
                        if(nodes.get(s)!=null){
                            newset.addAll(nodes.get(s).adj.keySet());
                            for( String a: nodes.get(s).adj.keySet()){
                                if(nodes.get(a)!=null){
                                    nodes.get(a).been = true;
                                }
                            }
                        }
                        if(nodes.get(s)!=null){
                            newset.addAll(nodes.get(s).parent.keySet());
                            for( String a: nodes.get(s).parent.keySet()){
                                if(nodes.get(a)!=null){
                                    nodes.get(a).been = true;
                                }
                            }
                        }
                    }
                    set.addAll(newset);
                    if(set.size()>origin)
                        flag = true;
                }
                //System.out.println(set);
                //System.out.println("------");
                can.add(set);
            }


        }
        //System.out.println(can);
        System.out.println("联通子图有多少个"+can.size());
        int count =0;
        Set<String> satis = new HashSet<>();
        int e_size=0;
        for(Set<String> s:can) {
            if(s.size()>1000){
                result.addAll(s);
                for(String ss:s){
                    if(nodes.get(ss)!=null){
                        satis.add(nodes.get(ss).c);
                        e_size = e_size+nodes.get(ss).adj.size();
                        //System.out.println("API"+ss+"；类别为"+nodes.get(ss).c
                        //+"邻接点集为："+nodes.get(ss).adj);
                    }
                }
                //System.out.println(s);
                System.out.println("（超过1000个节点，默认最大）的连通子图节点数目为"+s.size());
                System.out.println("（超过1000个节点，默认最大）此连通子图边数目为"+e_size/2);
                //System.out.println("此连通子图所属类别为"+satis);
                System.out.println("此连通子图所属类别数为"+satis.size());
                count++;
            }
        }
        //System.out.println("节点数量大于1000联通子图有多少个"+count);

        //单独求一个api的连通图
        String url = "/api/platform-trust-product";
        Set<String> set = new HashSet<>();
        Set<String> newset = new HashSet<>();
        set.add(url);
        newset.add(url);
        Boolean flag = true;
        while(flag){
            flag = false;
            int origin = set.size();
            for(String s:set){
                if(nodes.get(s)!=null)
                    newset.addAll(nodes.get(s).adj.keySet());
            }
            set.addAll(newset);
            if(set.size()>origin)
                flag = true;
        }
        for(String s:set) {
            System.out.println(s+" "+nodes.get(s).c);
        }


        return result;
    }
    //是否连通
    public void isConnected(){
        //单独求一个api的连通图即可
        String url = "/api/twilio";
        Set<String> set = new HashSet<>();
        set.add(url);
        set.addAll(nodes.get(url).adj.keySet());

        int cishu = 0;
        while(cishu < nodes.size()){
            cishu++;
            Set<String> newset = new HashSet<>();
            newset.add(url);
            Boolean flag = true;
            while(flag){
                flag = false;
                int origin = set.size();
                for(String s:set){
                    if(nodes.get(s)!=null)
                        newset.addAll(nodes.get(s).adj.keySet());
                }
                set.addAll(newset);
                if(set.size()>origin)
                    flag = true;
            }
            for(String one :nodes.keySet()){
                if(!set.contains(one)){
                    if(!TotalDiff(nodes.get(one).adj.keySet() , set)){
                        set.add(one);
                        set.addAll(nodes.get(one).adj.keySet());
                    }
                }
            }
        }
        System.out.println("检测子图是否连通：由这个节点得到连通子图节点数为"+set.size()+"个");

    }
    //计算被调用次数
    public void count(String sou_path){
        for(String url:nodes.keySet()){
            int temp = nodes.get(url).countTimes();
            times.put(url,temp);
        }
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(sou_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split("\\t");
                if(pairs.containsKey(param[0])){
                    pairs.get(param[0]).put(param[1],Integer.parseInt(param[4]));
                }else{
                    Map<String,Integer> pair = new HashMap<>();
                    pair.put(param[1],Integer.parseInt(param[4]));
                    pairs.put(param[0],pair);
                }
                //System.out.println(param[0]+","+param[1]+"次数为"+Integer.parseInt(param[4]));
            }
            System.out.println("有pairs信息的mashup的size为"+pairs.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean TotalDiff(Set<String> k1, Set<String> k2){
        for(String s1:k1){
            if (k2.contains(s1))
                return false;
        }
        for(String s2:k2){
            if (k1.contains(s2))
                return false;
        }
        return true;
    }
    //补充节点tags数据
    public void countTags(String sou_path) {
        int num = 0;
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(sou_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split("\\t");
                for(int i =1 ;i<param.length-1;i++){
                    nodes.get(param[0]).tags.add(param[i]);
                }
                nodes.get(param[0]).description=param[param.length-1];
                num++;
            }
            System.out.println("api_inf中的信息一共有"+num+"条");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
