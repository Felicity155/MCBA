package com.newCompositionIdea;


import java.io.*;
import java.util.*;

public class Application {

    public static  void main(String[] args) throws IOException {
        System.out.println("Hello!Sunying");
        firstExp(cg,"F:\\try\\test.txt",
                "F:\\try\\result.txt",
                "F:\\try\\answer.txt");

       /* //单个例子测试
        Set<String> keywords = new HashSet<>();
        keywords.add("Travel");
        keywords.add("Blogging");
        keywords.add("Music");
        TreeLab Lab = new TreeLab();
        //lab Lab = new lab();
        CompressedTree Stree = Lab.Stenir(keywords,cg,"Travel","Music");
        System.out.println("需要的功能关键词为"+keywords);
        System.out.println("用到的API有");
        if(Stree==null) {
            System.out.println("找不到此功能关键词下的斯坦纳树");
        }else if(Stree.nodes!=null){
            for (String p:Stree.nodes){
                System.out.println("adafse");
                //System.out.println(p+"  对应的类型为 "+g.nodes.get(p).c+" 相邻的顶点有 "+g.nodes.get(p).adj);
                System.out.println(p+"  对应的类型为 "+cg.comNodes.get(p).c+" 相邻的顶点有 "+cg.comNodes.get(p).adj);
            }
        }*/

    }

    public static CompressedGraph cg = new CompressedGraph("F:\\try\\after_api_nodes.txt",
            "F:\\try\\after_all_pairs.txt");

    //动态规划V1.0
    public static void firstExp(CompressedGraph g,String test_path,String result_path,String sta_path){
        //多个例子测试
        Map<String, Set<String>> result = new HashMap<>();
        String content = new String();//单个答案

        long total_time = 0;
        //输入问题并求解
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            int hang = 0;
            CompressedLab labs = new CompressedLab();
            File file = new File(test_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = new String();
            while ((data = br.readLine()) != null) {
                hang++;
                System.out.println("解决第几个mashup"+hang);
                //循环读取每一行
                String[] param = data.split("\\t");
                Set<String> keywords = new HashSet<>();
                for(int i = 1;i<param.length;i++){
                    keywords.add(param[i]);
                }
                long startTime = System.currentTimeMillis();    //获取开始时间
                CompressedTree comtree = labs.Stenir(keywords,g,param[1],param[param.length-1]);
                comtree.decompress(cg,keywords);
                long endTime = System.currentTimeMillis();    //获取结束时间
                total_time = total_time+endTime-startTime;

                //单个答案打印

                if(comtree==null) {
                    System.out.println("找不到斯坦纳树！");
                }else if(comtree.nodes!=null){
                    result.put(param[0],comtree.apis);
                    content=content+param[0]+"\t";
                    for(String url : result.get(param[0])){
                        content=content+url+"\t";
                    }
                    content = content.substring(0,content.length()-1);
                }
                content = content+"\r\n";
            }

            File f =new File(result_path);
            if(!f.exists()){
                f.createNewFile();
            }
            //使用true，即进行append file
            FileWriter fileWritter = new FileWriter(f.getName());
            fileWritter.write(content);
            fileWritter.close();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        System.out.println("firstExp程序运行时间： "+total_time+"ms");
        System.out.println("firstExp结果统计其分布为"+Evaluate(result));
        System.out.println("所有解决方案有"+result.size());
    }


    //统计评估
    public static   Map<Integer,Integer> Evaluate(Map<String,Set<String>> e ){
        Map<Integer,Integer> frequency =new HashMap<>();
        for(String m1:e.keySet()){
            int num = e.get(m1).size();
            if(frequency.keySet().contains(num)){
                Integer last= frequency.get(e.get(m1).size());
                frequency.remove(num,last);
                frequency.put(num,last+1);
            }else {
                frequency.put(num,1);
            }
        }
        return frequency;
    }
}
