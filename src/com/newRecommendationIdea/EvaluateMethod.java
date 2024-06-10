package com.newRecommendationIdea;

import com.Entity.Graph;
import com.Entity.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EvaluateMethod {
    //用于评价实验获取到前topK个列表的指标
    public static  void main(String[] args){
        //初始化
        g = new Graph("F:\\try\\after_api_nodes.txt", "F:\\try\\after_all_pairs.txt");
        g.secondinit();
        g.count("F:\\try\\after_all_pairs.txt");
        read_realMashup("F:\\try\\answer.txt");

        //读入结果并评估，控制台会打印实验指标信息
        System.out.println();
        read_allMashup("F:\\try\\temp.txt");//待评价的推荐列表所在文件地址
        EvaluateAll();

        //需要批量评价结果，只需要重复 read_allMashup 和EvaluateAll()即可
    }
    static Map<String, LinkedList<Result>> top_mashup_result;
    static Map<String, LinkedList<String>> real_mashup_result;
    static Graph g;
    public EvaluateMethod(String path){

    }

    public static void init(){
        //初始化
        g = new Graph("F:\\try\\after_api_nodes.txt", "F:\\try\\after_all_pairs.txt");
        g.secondinit();
        g.count("F:\\try\\after_all_pairs.txt");
        read_realMashup("F:\\try\\answer.txt");

        //读入结果并评估，控制台会打印实验指标信息
        System.out.println();
        read_allMashup("F:\\try\\temp.txt");//待评价的推荐列表所在文件地址
        EvaluateAll();

        //需要批量评价结果，只需要重复 read_allMashup 和EvaluateAll()即可
    }

    //读入文件里的答案
    public static void read_allMashup(String path){
        //读入
        top_mashup_result = new LinkedHashMap<>();
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流
        int num = 0;
        try {
            File file2 = new File(path);
            fr2 = new FileReader(file2);
            br2 = new BufferedReader(fr2);
            String data2 = null;

            while ((data2 = br2.readLine()) != null) {
                //循环读取每一行
                String[] param2 = data2.split("\\t");
                LinkedList<String> newresult = new LinkedList<>();
                for(int i =1 ;i<param2.length;i++){
                    newresult.add(param2[i]);
                }
                //int id = id_mashup.get(param2[0]);
                if(!top_mashup_result.containsKey(param2[0])){
                    top_mashup_result.put(param2[0],new LinkedList<>());
                    top_mashup_result.get(param2[0]).add(new Result(newresult));
                }else{
                    top_mashup_result.get(param2[0]).add(new Result(newresult));
                }
                num++;
            }
            System.out.println(path+"读入的mashup一共有"+top_mashup_result.size()+"条，解决方案一共有"+num+"条");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    //读入实际历史调用记录答案
    public static void read_realMashup(String path){
        real_mashup_result = new HashMap<>();
        //读入标准答案
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流
        int num = 0;
        try {
            File file2 = new File(path);
            fr2 = new FileReader(file2);
            br2 = new BufferedReader(fr2);
            String data2 = null;

            while ((data2 = br2.readLine()) != null) {
                //循环读取每一行
                String[] param2 = data2.split("\\t");
                //多读入一些也可以 if(top_mashup_result.containsKey(param2[0])){}
                LinkedList<String> newresult = new LinkedList<>();
                for(int i =1 ;i<param2.length;i++){
                    newresult.add(param2[i]);
                }
                real_mashup_result.put(param2[0],newresult);
                num++;
            }
            System.out.println(path+"读入的mashup一共有"+real_mashup_result.size()+"条，解决方案一共有"+num+"条");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    //遍历所有的指标
    public static void EvaluateAll(){
        MeanAccuracy();
        MeanCompatibility();
        MeanDiversity();
        Coverage();
        MeanPrecision_MeanRecall();
    }
    //指标一：平均精度：正确推荐的API组合在所有推荐的API组合所占比例
    public static double MeanAccuracy(){
        double index = 0.0;
        int n = top_mashup_result.size();
        int same = 0;
        int diff = 0;
        for(String mashup : top_mashup_result.keySet()){
            for(Result r : top_mashup_result.get(mashup)){
                if(k1equalsk2(r.detail,real_mashup_result.get(mashup))){
                    same++;
                }else{
                    diff++;
                }
            }
        }
        index =((double) same )/ ((double) n );
        int sum = diff + same;
        System.out.println("符合历史调用的答案有"+same+"条，不符合的答案有"+diff+"条，总共有"+sum+"条");
        System.out.println(n+"个mashup的答案"+"平均精度"+index+"");
        return index;
    }
    //指标二：平均兼容性
    public static double MeanCompatibility(){
        double index = 0.0;
        int n = top_mashup_result.size();
        for(String mashup : top_mashup_result.keySet()){
            LinkedList<Result> results = top_mashup_result.get(mashup);
            double score = 0.0;
            for(int p = 0 ; p < results.size() ;p++ ){
                Result aResult = results.get(p);
                double e_score = 0.0;
                //对于mashup m的一个组合方案aResult，计算其得分
                //边得分
                for(int i = 0; i <aResult.detail.size() ;i++){
                    for(int j = 0 ; j <aResult.detail.size();j++){
                        if(g.pairs.get(aResult.detail.get(i)).get(aResult.detail.get(j)) == null){
                            //System.out.println(aResult.detail.get(i)+"  "+aResult.detail.get(i+1)+"找不到其历史调用次数");
                        }else{
                            e_score = e_score + (double) (g.pairs.get(aResult.detail.get(i)).get(aResult.detail.get(j)) );
                        }
                    }
                }
                e_score = e_score/(aResult.detail.size()*(aResult.detail.size()-1));
               // System.out.print(score);
                score = score + e_score;
               // System.out.print("+"+e_score+"="+score);
               // System.out.println();
            }
            index = index + score/(double) results.size();
            //System.out.println(mashup + "的兼容性得分为"+score/(double) results.size());
        }
        //平均
        index = index/(double) n;
        System.out.println(n+"个mashup的答案"+"平均兼容性"+index+"");
        return index;
    }
    //指标三：列表间多样性：汉明距离
    public static double MeanDiversity(){
        double index = 0.0;
        int n = top_mashup_result.size();
        for(String mashup : top_mashup_result.keySet()){
            LinkedList<Result> results = top_mashup_result.get(mashup);
            double score = 0.0;
            for(int p = 0 ; p < results.size()-1 ;p++ ){
                for(int q = p+1 ; q < results.size() ;q++ ){
                    double e_score = HM_distance(results.get(p).detail,results.get(q).detail);
                     //System.out.print(score);
                     score = score + e_score;
                     //System.out.print("+"+e_score+"="+score);
                     //System.out.println();
                }
            }
            double d = 1;
            //System.out.println("score/d : "+score +" / " +d);
            if(results.size()-1!=0){
                d = (double) (results.size() * (results.size()-1))/2;
            }
            index = index + score/d;
           // System.out.println(mashup + "的兼容性得分为"+score/d);
        }
        //平均
        index = index/(double) n;
        System.out.println(n+"个mashup的答案"+"平均多样性"+index+"");
        return index;
    }
    //指标四：覆盖率
    public static double Coverage(){
        double index = 0.0;
        Set<String> been_api = new HashSet<>();
        for(String mashup : top_mashup_result.keySet()){
            for(Result r : top_mashup_result.get(mashup)){
                for(String api : r.detail){
                    been_api.add(api);
                }
            }
        }
        System.out.println("被调用过的API有"+been_api.size()+"个");
        index =( (double) been_api.size() )/1141.0;
        System.out.println(top_mashup_result.size()+"个mashup的答案"+"平均覆盖率"+index+"");
        return index;
    }
    //指标五：可控多样性：？？？
    //待思考：是否还有其他的指标？比如排名关联打分

    //指标：平均精度与平均召回率
    public static void MeanPrecision_MeanRecall(){
        double MP = 0.0;
        double MR = 0.0;
        int n = top_mashup_result.size();

        for(String mashup : top_mashup_result.keySet()){
            double a = 0.0;
            double b = 0.0;
            for(Result r : top_mashup_result.get(mashup)){
                int same =  sameNum(r.detail,real_mashup_result.get(mashup));
                  a = a + (double) same / r.detail.size();
                 b = b + (double) same / real_mashup_result.get(mashup).size();
            }
            a = a / top_mashup_result.get(mashup).size();
            b = b / top_mashup_result.get(mashup).size();
            MP = MP + a;
            MR = MR + b;
        }
        MP = MP / ((double) n );
        MR = MR / ((double) n );
        System.out.println(n+"个mashup的答案"+"平均精度"+MP+"");
        System.out.println(n+"个mashup的答案"+"平均精度"+MR+"");
    }


    //计算汉明距离
    public static Double HM_distance(LinkedList<String> a ,LinkedList<String> b){
        Set<String> union = new HashSet<>();
        int total_size = a.size()+ b.size();
        int same = 0;
        for(String temp:a){
            if(b.contains(temp)){
                same++;
            }
        }
        double index = 1.0 - ((double) same)/((double) total_size);
        return index;
    }
    public static Boolean k1equalsk2(LinkedList<String> k1, LinkedList<String> k2){
        for(String s1:k1){
            if (!k2.contains(s1))
                return false;
        }
        for(String s2:k2){
            if (!k1.contains(s2))
                return false;
        }
        return true;
    }
    public static Integer sameNum(LinkedList<String> k1, LinkedList<String> k2){
        int num =0;
        for(String s1:k1){
            if (k2.contains(s1))
                num++;
        }
        return num;
    }
}
