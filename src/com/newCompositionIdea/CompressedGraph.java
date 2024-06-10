package com.newCompositionIdea;

import com.manyAns.Graph;
import com.manyAns.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CompressedGraph {

    public static  void main(String[] args) throws IOException {
        CompressedGraph cg = new CompressedGraph("F:\\try\\after_api_nodes.txt",
                "F:\\try\\after_all_pairs.txt");
    }


    //从原图压缩得到压缩图
    public CompressedGraph(String api_path, String a_a_path){
        this.api_path = api_path;
        this.a_a_path = a_a_path;
        init();
        compress();
    }

    public static String api_path ; //API信息读入
    public static String a_a_path; //API历史来联合调用信息读入
    public static Map<String,CompressedNode> comNodes = new HashMap<>();

    public static Map<String,Api> apis = new HashMap<>();
    public static Map<String,String> url_url = new HashMap<>();//压缩前后的url配对
    //public static Map<String,Integer> api_2_id = new HashMap<>();

    //初始化构造图,有向图，调用顺序有先后
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
                Api newapi = new Api(param[1],param[6]);
                apis.put(param[1],newapi);
            }
            System.out.println("图的API节点一共有"+apis.size()+"个");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, LinkedList<String>> e = new HashMap<>();//分类完毕的mashup数据
        //加入所有边
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流

        try {
            File file2 = new File(a_a_path);
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
            System.out.println("图的mashup一共有"+e.size()+"组");
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
                    apis.get(n1).adj.add(n2);
                }
                for(int i = 1 ;i < s.size() ; i++){
                    String n1 = s.get(i);
                    String n2 = s.get(i-1);
                    apis.get(n1).parent.add(n2);
                    parent_size++;
                }
            }
        }
        System.out.println("图的边一共有"+edge_size+"条");
        System.out.println("图的父子关系一共有"+parent_size+"条");
    }

    public void secondinit(){
        //加入所有节点
        int line = -1;
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(this.api_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                line++;
                String[] param = data.split("\\t");
                Api newapi = new Api(param[1],param[6]);
                apis.put(param[1],newapi);
                //api_2_id.put(param[1],line);
            }
            System.out.println("图的API节点一共有"+apis.size()+"个");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //加入所有边
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流
        int edge_size = 0;
        try {
            File file2 = new File(a_a_path);
            fr2 = new FileReader(file2);
            br2 = new BufferedReader(fr2);
            String data2 = null;

            while ((data2 = br2.readLine()) != null) {
                //循环读取每一行
                String[] param2 = data2.split("\\t");
                String n1 = param2[0];
                String n2 = param2[1];
                int frequency =  Integer.parseInt (param2[4]);
                apis.get(n2).parent.add(n1);
                apis.get(n1).adj.add(n2);
                edge_size++;
            }
            System.out.println("图的api-api pair信息一共有"+edge_size+"条");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void compress(){
        Set<String> been = new HashSet<>();

        for(String a_url_1 : apis.keySet()){
            if(!been.contains(a_url_1)){
                int flag = 0;
                for(String s:apis.get(a_url_1).adj){
                    if(!been.contains(s) ){
                        LinkedList<Api> temp = new LinkedList<>();
                        temp.add(apis.get(a_url_1));
                        temp.add(apis.get(s));
                        CompressedNode new_node = new CompressedNode(temp);
                        comNodes.put(new_node.url,new_node);
                        flag = 1;

                        url_url.put(a_url_1,new_node.url);
                        url_url.put(s,new_node.url);
                        been.add(a_url_1);
                        been.add(s);
                        break;
                    }
                }
                if(flag == 0){
                    for(String s:apis.get(a_url_1).parent){
                        //if(!been.contains(s) && !apis.get(a_url_1).c.equals(apis.get(s).c)){
                        if(!been.contains(s)){
                            LinkedList<Api> temp = new LinkedList<>();
                            temp.add(apis.get(a_url_1));
                            temp.add(apis.get(s));
                            CompressedNode new_node = new CompressedNode(temp);
                            comNodes.put(new_node.url,new_node);
                            flag = 1;

                            url_url.put(a_url_1,new_node.url);
                            url_url.put(s,new_node.url);
                            been.add(a_url_1);
                            been.add(s);
                            break;
                        }
                    }
                }
                if(flag == 0){
                    LinkedList<Api> temp = new LinkedList<>();
                    temp.add(apis.get(a_url_1));
                    CompressedNode new_node = new CompressedNode(temp);
                    comNodes.put(new_node.url,new_node);

                    url_url.put(a_url_1,new_node.url);
                    been.add(a_url_1);
                }
            }
        }

        //url需要对应改变
        for(String s :comNodes.keySet()){
            comNodes.get(s).update(url_url);
        }


        int num = 0;
        for(String n:comNodes.keySet()){
            CompressedNode node = comNodes.get(n);
            //System.out.println(node.url);
            num = num + node.api_set.size();
        }
        System.out.println("压缩后的节点数目为"+comNodes.size());
        System.out.println("压缩后的API数目为"+num);
    }



}
