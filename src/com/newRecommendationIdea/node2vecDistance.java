package com.newRecommendationIdea;

import org.apache.commons.collections.map.HashedMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class node2vecDistance {

    public node2vecDistance(){
        api_id("F:\\try\\after_api_nodes.txt");
        init("F:\\try\\api_node2vec.txt");
    }
    public static void main(String[] args){
        new node2vecDistance();
    }

    public static Map<String , double[]> node2vec = new HashMap<>();
    public static Map<Integer,String> id = new HashedMap();
    public static int vectorSize = 128;

    //读入所有的node2vec数据
    public static  void api_id(String path){
        int line = 1;
        //加入所有节点
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split("\\t");
                id.put(line,param[1]);
                line++;
            }
            System.out.println("node2vecDIstance初始化中API一共有"+id.size()+"个");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void init(String word2vec_path){
        FileReader fr = null;//创建FileReader流
        BufferedReader br = null;//创建BufferedReader流
        try {
            File file = new File(word2vec_path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = null;
            data = br.readLine();
            while ((data = br.readLine()) != null) {
                //循环读取每一行
                String[] param = data.split(" ");
                String s_temp = id.get(Integer.parseInt(param[0]));
                //System.out.println(s_temp);
                // 初始化数组
                List<Double> list_temp = new ArrayList<>();
                for(int i = 1;i<param.length;i++){
                    list_temp.add(Double.parseDouble(param[i]));
                }
                // 转换为数组
                Double[] arr = list_temp.toArray(new Double[0]);
                double[] doubleArr = new double[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    doubleArr[i] = arr[i];
                }
                node2vec.put(s_temp,doubleArr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("node2vecDIstance初始化中读入向量的数目为"+node2vec.size());
    }


    public static double Distance(LinkedList<String> words1, LinkedList<String> words2){
        List<double[]> vectors1 = new ArrayList<>();
        for (String word : words1) {
            String w_temp = word.toLowerCase(Locale.ROOT);
            double[] vector = node2vec.get(w_temp);
            if (vector != null) {
                vectors1.add(vector);
            }
        }
        List<double[]> vectors2 = new ArrayList<>();
        for (String word : words2) {
            String w_temp = word.toLowerCase(Locale.ROOT);
            double[] vector = node2vec.get(w_temp);
            if (vector != null) {
                vectors2.add(vector);
            }
        }

        double[] Aver1 = calculateCentroid(vectors1);
        double[] Aver2 = calculateCentroid(vectors2);
        //double words_distance = euclideanDistance(Aver1,Aver2);
        double words_distance = (cosineSimilarity(Aver1,Aver2)+1)/2;
        //System.out.println("word_distance: "+words_distance);
        return words_distance;
    }

    //获取某个API组合的特征向量
    public static double[] getVector(LinkedList<String> words1){
        List<double[]> vectors1 = new ArrayList<>();
        for (String word : words1) {
            String w_temp = word.toLowerCase(Locale.ROOT);
            double[] vector = node2vec.get(w_temp);
            if (vector != null) {
                vectors1.add(vector);
            }
        }
        double[] Aver1 = calculateCentroid(vectors1);
        //System.out.println("word_distance: "+words_distance);
        return Aver1;
    }

    private static double[] calculateCentroid(List<double[]> vectors) {
        int size = vectors.size();

        double[] centroid = new double[vectorSize];
        for (double[] vector : vectors) {
            for (int i = 0; i < vectorSize; i++) {
                centroid[i] += vector[i];
                //System.out.println("calculateCentroid过程"+vector[i]);
            }
        }
        for (int i = 0; i < vectorSize; i++) {
            centroid[i] /= size;
           // System.out.println("calculateCentroid过程"+centroid[i]);
        }
        return centroid;
    }
    //值的范围在-1到1之间，当两个向量完全相同时余弦相似度为1，当两个向量没有共同的元素时余弦相似度为0，当两个向量的夹角接近直角时余弦相似度为-1。
    // 因此余弦相似度越大，说明两个向量越相似。
    private static double cosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }
        double similarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return similarity;
    }
    // 计算两个向量的欧式距离
    //欧式距离衡量的是多维空间中两个点的绝对距离，即它们之间的距离越近，欧式距离越小，相似度越大。因此，欧式距离越小，表示两个向量越相似
    public static double euclideanDistance(double[] vector1, double[] vector2) {
        double distance = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            distance += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(distance);
    }

}
