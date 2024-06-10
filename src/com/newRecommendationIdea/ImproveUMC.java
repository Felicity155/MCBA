package com.newRecommendationIdea;

import com.Entity.Graph;
import com.Entity.Mashup;
import com.Entity.Result;

import java.io.*;
import java.util.*;

public class ImproveUMC {
    static Graph g;
    static node2vecDistance node2vec = new node2vecDistance();
    static Map<String, Mashup> all_mashup = new LinkedHashMap<>();
    //填写参数
    static Integer K = 5;//获取前K个答案
    static int k_cluster = 20; // 聚类数目
    static double LAMBDA =0.6;//关联度（质量得分）所占比例
    static String all_result_path = "F:\\try\\result_first_500.txt";    //待推荐所有API组合所在文件地址
    static String topK_result_path = "F:\\try\\temp.txt";    //推荐完毕后的topK个API组合写入文件地址

    public static  void main(String[] args) throws IOException {
        //初始化
        g = new Graph("F:\\try\\after_api_nodes.txt", "F:\\try\\after_all_pairs.txt");
        g.secondinit();
        g.count("F:\\try\\after_all_pairs.txt");
        System.out.println("你好");

        //循环获取每个mashup的前topK个答案并写入文件
/*        read_allMashup(all_result_path);
        double[][] similarityMatrix = Sim3(all_mashup.get("Mashup: Kickdash").results); // 初始化相似性矩阵
        int k_cluster = 5; // 聚类数目
        ItemCluster itemCluster = new ItemCluster(similarityMatrix, k_cluster);
        Map<Integer, double[]> centers = itemCluster.getCenters();
        Map<Integer, Set<Integer>> clusters = itemCluster.getClusters();
        System.out.println(clusters);
        System.out.println(clusters.get(0).size());
        System.out.println(clusters.get(1).size());
        System.out.println(clusters.get(2).size());
        System.out.println(clusters.get(3).size());
        System.out.println(clusters.get(4).size());*/
        read_allMashup(all_result_path);
        String topString = new String();
        int num = 0;
        for(String aMashup : all_mashup.keySet()){
            //获取
            LinkedList<Result> temp = get_topK_result(all_mashup.get(aMashup).results,aMashup);
            //打印
            for(Result r :temp){
                topString = topString + aMashup + r.getString() +"\r\n";
            }
            num++;
            System.out.println("处理第几个mashup "+num);
        }
        File f =new File(topK_result_path);
        if(!f.exists()){
            f.createNewFile();
        }
        //使用true，即进行append file
        FileWriter fileWritter = new FileWriter(f.getName());
        fileWritter.write(topString);
        fileWritter.close();

        EvaluateMethod e = new EvaluateMethod(" ");
        e.init();
    }

    //读入所有的mashup对应所有的答案result
    public static void read_allMashup(String path){
        //读入所有答案
        FileReader fr2 = null;//创建FileReader流
        BufferedReader br2 = null;//创建BufferedReader流
        int num = 1;
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
                if(!all_mashup.containsKey(param2[0])){
                    all_mashup.put(param2[0],new Mashup(param2[0]));
                    all_mashup.get(param2[0]).results.add(new Result(newresult));
                }else{
                    all_mashup.get(param2[0]).results.add(new Result(newresult));
                }
                num++;
            }
            System.out.println("reasult.txt的all_mashup一共有"+all_mashup.size()+"条");
            System.out.println("reasult.txt的mashup解决方案一共有"+num+"条");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    //计算得分，获取前K个
    public static LinkedList<Result> get_topK_result(LinkedList<Result> results ,String aMashup){
        double[][] similarityScores = Sim2(results);
        double[] relevanceScores =  Sim1(results);
        LinkedList<Integer> topId = Greedy(aMashup,similarityScores,relevanceScores, LAMBDA);
        LinkedList<Result> topResult = new LinkedList<>();
        for(Integer i = 0 ; i < topId.size();i++){
            Integer id = topId.get(i);
            topResult.add(results.get(id));
        }
        return topResult;
    }
    // 使用贪心算法选出前K个
    public static LinkedList<Integer> Greedy(String aMashup,double[][] similarityScores, double[] relevanceScores, double lambda) {

        //聚类
        double[][] similarityMatrix = Sim3(all_mashup.get(aMashup).results); // 初始化相似性矩阵

        ItemCluster itemCluster = new ItemCluster(similarityMatrix, k_cluster);
        List<List<Integer>> clusters = itemCluster.clusters;
        Map<Integer, Integer> itemToCluster = itemCluster.itemToCluster;

        //按照评分开始处理
        Integer[] indexArr = new Integer[relevanceScores.length];
        for (int i = 0; i < relevanceScores.length; ++i) {
            indexArr[i] = i;
        }
        Arrays.sort(indexArr, (i1, i2) -> Double.compare(relevanceScores[i2], relevanceScores[i1]));

        //拟阵约束下的局部搜索，结合MMR计算得分方式
        LinkedList<Integer> selectedSet = new LinkedList<>();
        Set<Integer> cluster_been  = new HashSet<>();
        selectedSet.add(indexArr[0]);
        cluster_been.add(itemToCluster.get(indexArr[0]));
        for(int id_i = 1 ; id_i < indexArr.length ;id_i ++){
            int i = indexArr[id_i];
            if( selectedSet.size()<K &&!cluster_been.contains(itemToCluster.get(i))){
                selectedSet.add(i);
                cluster_been.add(itemToCluster.get(i));
            }else{
                //System.out.println("ddddddddddddd");
                for(int id_j = 0; id_j < selectedSet.size() ;id_j++){
                    int j = indexArr[id_j];
                    LinkedList<Integer> con_Set = new LinkedList<>(selectedSet);
                    con_Set.remove(id_j);
                    if(mmrScore(relevanceScores, similarityScores, con_Set, i, lambda)
                            > mmrScore(relevanceScores, similarityScores, con_Set, j, lambda)
                            && (itemToCluster.get(i) == itemToCluster.get(j) || !cluster_been.contains(itemToCluster.get(i)) ) ){
                        selectedSet.remove(id_j);
                        selectedSet.add(i);
                        System.out.println("替换一次");
                        break;
                    }
                }
            }
        }
/*        int max_id = 0;
        double max_score = relevanceScores[0];
        for (int i = 0; i < relevanceScores.length; i++) {
            if(relevanceScores[i]>max_score){
                max_id = i;
                max_score = relevanceScores[i];
            }
        }
        System.out.println("第一个加入的答案得分为 "+mmrScore(relevanceScores, similarityScores, selectedSet, max_id, lambda));
        selectedSet.add(max_id);

        for(int o=0 ;o < k_cluster ; o++){
            if(o != itemToCluster.get(max_id) && clusters.get(o).size()!=0){
                PriorityQueue<Integer> pq ;
                pq = new PriorityQueue<>( (i, j) ->
                        Double.compare(mmrScore(relevanceScores, similarityScores, selectedSet, j, lambda), mmrScore(relevanceScores, similarityScores, selectedSet, i, lambda)));
                for (int i : clusters.get(o)) {
                    pq.offer(i);
                }
                System.out.println("PQ的大小"+pq.size());
                int x = pq.poll();
                selectedSet.add(x);
            }
        }*/
        System.out.println("selectedSet大小: "+selectedSet.size());
        return selectedSet;
    }
    // 计算MMR得分
    private static double mmrScore(double[] relevanceScores, double[][] similarityScores, LinkedList<Integer> selectedSet, int i, double lambda) {
        double relevanceScore = relevanceScores[i];
        double maxSimilarityScore = 0;
        //使用余弦相似度的话，是越大越相似
        //想要找的是不相似的
        for (int j : selectedSet) {
            double similarityScore = similarityScores[i][j];
            if (similarityScore > maxSimilarityScore) {
                maxSimilarityScore = similarityScore;
            }
        }
        //maxSimilarityScore越大，说明越相似，所以这里是减去
        return lambda * relevanceScore - (1 - lambda) * maxSimilarityScore;
    }
    //计算MSD得分
    private static double msdScore(double[] relevanceScores, double[][] similarityScores, LinkedList<Integer> selectedSet, int i, double lambda) {
        double relevanceScore = relevanceScores[i];
        double maxSimilarityScore = 0;
        //使用余弦相似度的话，是越大越相似
        //想要找的是不相似的
        for (int j : selectedSet) {
            double similarityScore = similarityScores[i][j];
            if (similarityScore > maxSimilarityScore) {
                maxSimilarityScore += similarityScore;
            }
        }
        maxSimilarityScore = maxSimilarityScore / selectedSet.size();
        //maxSimilarityScore越大，说明越相似，所以这里是减去
        return lambda * relevanceScore - (1 - lambda) * maxSimilarityScore;
    }

    //计算相关性得分（即质量得分）
    public static double[] Sim1(LinkedList<Result> results){
        int n = results.size();
        double[] scores = new double[n];
        double max = -1.0;
        double min = 100000000.0;
        for(int p = 0 ; p < results.size() ;p++ ){
            Result aResult = results.get(p);
            double e_score = 0.0;
            double n_score = 0.0;
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
            e_score = e_score/(aResult.detail.size()*(aResult.detail.size()));
            if(e_score > max){
                max = e_score;
            }else if(e_score < min){
                min = e_score;
            }
            scores[p] = e_score;
        }

        //System.out.println("最大和最小分别为"+max+" "+min);
        for(int q = 0 ;q < results.size();q++){
            //System.out.println("均一化之前之后为"+scores[q]+" "+(scores[q]-min)/(max-min));
            scores[q] = (scores[q]-min)/(max-min);
        }
        return scores;
    }
    //计算相似性得分（即负相关性）
    public static double[][] Sim2(LinkedList<Result> results){
        int n = results.size();
        double[][] scores = new double[n][n];
        double max = -1.0;
        double min = 100000000.0;
        for(int i = 0 ;i < n ;i++){
            for(int j = 0;j < n ; j++){
                double temp_score = node2vec.Distance(results.get(i).detail,results.get(j).detail);
                scores[i][j] = temp_score;
                if(temp_score > max){
                    max = temp_score;
                }else if(temp_score < min){
                    min = temp_score;
                }
            }
        }
        //System.out.println("相似度最大和最小分别为"+max+" "+min);

        for(int i = 0 ;i < n ;i++){
            double m = 0.0;
            for(int j = 0;j < n ; j++){
                // System.out.println("相似度均一化之前之后为"+scores[i][j]+" "+(scores[i][j]-min)/(max-min));
                scores[i][j] = (scores[i][j]-min)/(max-min);
                if( scores[i][j]>m && i!=j)
                    m =  scores[i][j];
            }
            //System.out.println(" scores[i][j]"+ m);
        }

        return scores;
    }
    //计算API组合的特征向量矩阵
    public static double[][] Sim3(LinkedList<Result> results){
        int n = results.size();
        double[][] scores = new double[n][128];

        for(int i = 0 ;i < n ;i++){
            scores[i] = node2vec.getVector(results.get(i).detail);
        }
        return scores;
    }
    // 计算矩阵的值
    public static double MatrixCalculate(double[][] matrix) {
        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("Invalid matrix: not square");
        }
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }
        double value = 0;
        for (int j = 0; j < n; j++) {
            double[][] minor = new double[n - 1][n - 1];
            for (int i = 1; i < n; i++) {
                System.arraycopy(matrix[i], 0, minor[i - 1], 0, j);
                System.arraycopy(matrix[i], j + 1, minor[i - 1], j, n - j - 1);
            }
            double cofactor = ((j % 2 == 0) ? 1 : -1) * MatrixCalculate(minor);
            value += matrix[0][j] * cofactor;
        }
        return value;
    }
    //获取double数组中最大值的位置
    public static int getMaxIndex(double[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("数组不能为空！");
        }
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}
