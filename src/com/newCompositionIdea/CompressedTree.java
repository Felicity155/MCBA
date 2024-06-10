package com.newCompositionIdea;

import com.Entity.Edge;

import java.util.*;

public class CompressedTree {

    public CompressedTree(){

        num=0;
        nodes = new LinkedList<>();
        needs = new LinkedList<>();
        edges = new HashSet<>();
        apis = new HashSet<>();
    }
    public Boolean direction ;
    public int num;//所含有用关键词数量
    public LinkedList<String> nodes;
    public Set<Edge> edges;
    public LinkedList<String> needs;

    public Set<String> apis;

    public void decompress(CompressedGraph cg,Set<String> keywords){
        //System.out.println(nodes);
        Set<String> all_api = new HashSet<>();
        Set<String> after_api = new HashSet<>();
        for(String n:nodes){
            for(Api api : cg.comNodes.get(n).api_set){
                all_api.add(api.url);
            }
        }
        after_api.addAll(all_api);
        for(String s:all_api){
            //无用/重复功能的节点，需要看看需不需要删去
            boolean flag = false;
            if(!keywords.contains(cg.apis.get(s).c)){
                flag = true;
            }
            for(String another : after_api){
                if(!another.equals(s) && cg.apis.get(s).c.equals(cg.apis.get(another).c))
                    flag = true;
            }
            if(flag){
                after_api.remove(s);
                //如果删去此顶点会影响连通，则不能删去
                if(!isConnected(after_api,cg)){
                    after_api.add(s);
                }else{
                    //System.out.println("删去一个节点");
                }
            }
        }
        apis.addAll(after_api);
        //System.out.println(apis);
    }

    //是否连通
    public boolean isConnected(Set<String> now_apis, CompressedGraph cg){
        //单独求一个api的连通图即可
        Set<String> connect  =new HashSet<>();
        Set<String> not_connect  =new HashSet<>();
        not_connect.addAll(now_apis);
        for(String a :now_apis){
            connect.add(a);
            not_connect.remove(a);
            break;
        }

        Boolean flag = true;
        while (flag){
            flag = false;
            Set<String> adj = new HashSet<>();
            for(String been : connect){
                adj.addAll(cg.apis.get(been).adj);
                adj.addAll(cg.apis.get(been).parent);
            }
            for(String not_been : not_connect){
                if(adj.contains(not_been)){
                    flag = true;
                    connect.add(not_been);
                }
            }
            not_connect.removeAll(connect);
        }
        if(not_connect.size()==0 && connect.size()==now_apis.size()){
            return true;
        }else if(not_connect.size()==0){
            System.out.println("不对劲啊！");
            return false;
        }else if(connect.size()==now_apis.size()){
            System.out.println("不对劲啊！");
            return false;
        }else {
            return false;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompressedTree tree = (CompressedTree) o;
        return Objects.equals(nodes, tree.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, num, nodes, edges, needs);
    }
}
