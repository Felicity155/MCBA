package com.newCompositionIdea;


import java.io.*;
import java.util.*;

public class CompressedApplication {

    public static  void main(String[] args) throws IOException {
        //计算时间花费
        float sum = 0;
        int size = 1;
        for(int i = 0 ;i<size;i++){
            CompressedApplication a = new CompressedApplication();
            sum = sum + a.aver_time;
        }
        sum = sum / size;
        System.out.println("***********************************************************");
        System.out.println("运行 "+size+" 次后平均花费时间为" + sum);

    }


    public CompressedApplication(){
        System.out.println("Hello!Sunying 这里是压缩图算法");
        firstExp(cg,"F:\\try\\test.txt",
                "F:\\DATA\\result_com.txt",
                "F:\\try\\answer.txt");
    }

    public static CompressedGraph cg = new CompressedGraph("F:\\try\\after_api_nodes.txt",
            "F:\\try\\after_m_a_edges.txt");
    public static float aver_time ;

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
                //System.out.println("解决第几个mashup"+hang);
                //循环读取每一行
                String[] param = data.split("\\t");
                Set<String> keywords = new HashSet<>();
                for(int i = 1;i<param.length;i++){
                    keywords.add(param[i]);
                }
                long startTime = System.currentTimeMillis();    //获取开始时间
                CompressedTree comtree = labs.Stenir(keywords,g,param[1],param[param.length-1]);
                long endTime = System.currentTimeMillis();    //获取结束时间
                total_time = total_time+endTime-startTime;
                aver_time = (float) total_time / hang;
                //单个答案打印

                if(comtree==null) {
                    System.out.println("找不到斯坦纳树！");
                }else if(comtree.nodes.size()!=0){
                    comtree.decompress(cg,keywords);
                    result.put(param[0],comtree.apis);
                    content=content+param[0]+"\t";
                    for(String url : result.get(param[0])){
                        content=content+url+"\t";
                    }
                    content = content.substring(0,content.length()-1);
                }
                content = content+"\r\n";
            }

            Evaluate(hang,result);

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
        System.out.println("firstExp程序平均运行时间： "+aver_time+"ms");
        System.out.println("firstExp结果统计其分布为"+Stat(result));
        System.out.println("所有解决方案有"+result.size());
    }


    //统计评估
    public static   Map<Integer,Integer> Stat(Map<String,Set<String>> e ){
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

    //统计评估
    public static   void Evaluate(int hang,Map<String,Set<String>> e ){
        int total_size = 0;
        for(String m1:e.keySet()){
            int num = e.get(m1).size();
            total_size = total_size + num;
        }
        float aver_size = (float) total_size/e.keySet().size();
        float success_rate = (float) e.size()/ hang;
        System.out.println("平均API数量为 "+ aver_size);
        System.out.println("成功率为 "+ success_rate);
    }

}
