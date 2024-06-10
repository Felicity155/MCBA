package com.newRecommendationIdea;
import java.util.*;

public class ItemCluster {
    public double[][] similarityMatrix;
    public int n;
    public int d = 128;
    public int k;
    public List<List<Integer>> clusters;
    public Map<Integer, Integer> itemToCluster;
    public Map<Integer, Integer> clusterToCenter;



    public ItemCluster(double[][] similarityMatrix, int k) {
        this.similarityMatrix = similarityMatrix;
        this.n = similarityMatrix.length;
        this.k = k;
        System.out.println("k"+k);
        System.out.println("n"+n);
        init();
        cluster();
    }


    private void init() {
        clusters = new ArrayList<>();
        itemToCluster = new HashMap<>();
        clusterToCenter = new HashMap<>();

        // Initialize the cluster centers randomly
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            List<Integer> cluster = new ArrayList<>();
            int center = random.nextInt(n);
            cluster.add(center);
            clusters.add(cluster);
            itemToCluster.put(center, i);
            clusterToCenter.put(i,center);
        }
    }

    public void cluster() {
        while (true) {
            // Assign data points to clusters
            boolean isChange = false;
            for (int i = 0; i < n; i++) {
                int currentCluster = itemToCluster.containsKey(i) ? itemToCluster.get(i) : -1;
                int nearestCluster = getNearestCluster(i);
                if (currentCluster != nearestCluster) {
                    if (currentCluster != -1) {
                        clusters.get(currentCluster).remove(Integer.valueOf(i));
                    }
                    clusters.get(nearestCluster).add(i);
                    itemToCluster.put(i, nearestCluster);
                    isChange = true;
                }
            }

            // Check if converged
            if (!isChange) {
                break;
            }

            // Update cluster centers
            for (int i = 0; i < k; i++) {
                double[] center = new double[d];
                List<Integer> items = clusters.get(i);
                if (items.isEmpty()) {
                    continue;
                }
                for (int j : items) {
                    for (int l = 0; l < d; l++) {
                        center[l] += similarityMatrix[j][l];
                    }
                }
                for (int l = 0; l < d; l++) {
                    center[l] /= items.size();
                }

                // Find the data point closest to the new center
                int newCenter = getNearestDataPoint(center, items);
                itemToCluster.put(newCenter, i);
                clusterToCenter.remove(i);
                clusterToCenter.put(i,newCenter);
            }
            System.out.println("聚类迭代一次");
        }
    }

    private int getNearestCluster(int dataPoint) {
        int nearestCluster = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            double dist = getDistance(dataPoint, clusterToCenter.get(i));
            if (dist < minDist) {
                nearestCluster = i;
                minDist = dist;
            }
        }
        return nearestCluster;
    }

    private int getNearestDataPoint(double[] center, List<Integer> items) {
        int nearestDataPoint = items.get(0);
        double maxSimilarity = 0;
        for (int i : items) {
            double similarity = getSimilarity(center, similarityMatrix[i]);
            if (similarity > maxSimilarity) {
                nearestDataPoint = i;
                maxSimilarity = similarity;
            }
        }
        return nearestDataPoint;
    }

    private double getDistance(int i, int j) {
        double sum = 0;
        for (int k = 0; k < d; k++) {
            sum += Math.pow(similarityMatrix[i][k] - similarityMatrix[j][k], 2);
        }
        return Math.sqrt(sum);
    }

    private double getSimilarity(double[] a, double[] b) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < d; i++) {
            dotProduct += a[i] * b[i];
            normA += Math.pow(a[i], 2);
            normB += Math.pow(b[i], 2);
        }
        if (normA == 0 || normB == 0) {
            return 0;
        } else {
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        }
    }

    public Map<Integer, double[]> getCenters() {
        Map<Integer, double[]> centers = new HashMap<>();
        for (int i = 0; i < k; i++) {
            double[] center = new double[d];
            List<Integer> items = clusters.get(i);
            if (items.isEmpty()) {
                continue;
            }
            for (int j : items) {
                for (int l = 0; l < d; l++) {
                    center[l] += similarityMatrix[j][l];
                }
            }
            for (int l = 0; l < d; l++) {
                center[l] /= items.size();
            }
            centers.put(i, center);
        }
        return centers;
    }

    public Map<Integer, Set<Integer>> getClusters() {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        for (int i = 0; i < k; i++) {
            Set<Integer> items = new HashSet<>(clusters.get(i));
            result.put(i, items);
        }
        return result;
    }
}
