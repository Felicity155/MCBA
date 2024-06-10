package com.newCompositionIdea;



import java.util.*;

//120L020414 孙莹
public class CompressedLab {
    public static CompressedTree minTree;
    public static Set<CompressedTree> allTree;
    public static void updateTree(){
        int min = 2000;
        if(allTree.size()>0){
            for(CompressedTree aTree : allTree){
                if(aTree.nodes.size()<min){
                    min = aTree.nodes.size();
                    minTree = aTree;
                }
            }
        }else{
            minTree = new CompressedTree();
        }

/*        Map<Integer,Integer> frequency =new HashMap<>();
        for(Tree m1:allTree){
            int num = m1.nodes.size();
            if(frequency.keySet().contains(num)){
                Integer last= frequency.get(num);
                frequency.remove(num,last);
                frequency.put(num,last+1);
            }else {
                frequency.put(num,1);
            }
        }
        System.out.println("所有的API解决答案分布为"+frequency);*/
        return;
    }
    public static Boolean TotalDiff(LinkedList<String> k1, LinkedList<String> k2){
        Boolean flag11 = false;
        Boolean flag12 = false;
        Boolean flag21 = false;
        Boolean flag22 = false;
        for(String s1:k1){
            if (!k2.contains(s1))
                flag11 = true;
            if (k2.contains(s1))
                flag12 = true;
        }
        for(String s2:k2){
            if (!k1.contains(s2))
                flag21 = true;
            if (k1.contains(s2))
                flag22 = true;
        }
        if(flag11==true && flag12==true && flag21==true && flag22==true )
            return true;
        return  false;
    }
    public static Boolean shouldMerge(Set<String> k1, LinkedList<String> k2){
        for(String k2_s : k2){
            if(k1.contains(k2_s))
                return true;
        }
        return  false;
    }
    public static Boolean k1containk2(LinkedList<String> k1, Set<String> k2){

        for(String s2:k2){
            if (!k1.contains(s2))
                return false;
        }
        return true;
    }
    public static Boolean shouldGrow(Set<String> u_c ,LinkedList<String> tv_needs ,Set<String> needs){
        for(String m : u_c){
            if(needs.contains(m) && !tv_needs.contains(m))
                return true;
        }
        return false;
    }

    //【动态规划V1.0】给定子集needs，在中求最小斯坦纳树
    public static CompressedTree Stenir(Set<String> needs, CompressedGraph g, String first, String last){
        LinkedList<CompressedTree> LT =new LinkedList<>();
        minTree = new CompressedTree();
        allTree = new HashSet<>();
        int adj_max;//队列
        for (String url : g.comNodes.keySet()) {
            if(g.comNodes.get(url)!=null){
                if (g.comNodes.get(url).c.contains(first)) {
                    CompressedTree temp = new CompressedTree();
                    temp.nodes.add(url);
                    temp.needs.addAll(g.comNodes.get(url).c);
                    temp.num = 1;
                    LT.add(temp);
                }else if(g.comNodes.get(url).c.contains(last)) {
                    CompressedTree temp = new CompressedTree();
                    temp.nodes.add(url);
                    temp.needs.addAll(g.comNodes.get(url).c);
                    temp.num = 1;
                    LT.add(temp);
                }
            }

        }
        Boolean done = false;
        int max_size = needs.size()*2;
        while (!LT.isEmpty()) {
            if(allTree.size()>=200){
                updateTree();
                return minTree;
            }
            done = false;
            CompressedTree tv = LT.poll();
            if(tv.nodes.size() > max_size)
                continue;
            //System.out.println("取出的树tv为"+tv.nodes);
            if (k1containk2(tv.needs,needs) && tv.nodes.size()<=max_size){
                allTree.add(tv);
                return tv;
            }
            else {
                Set<String> Nv = new HashSet<>();
                for (String u : tv.nodes) {
                    Nv.addAll(g.comNodes.get(u).adj);
                    Nv.addAll(g.comNodes.get(u).parent);
                }
                Nv.removeAll(tv.nodes);
                //树的生长
                //System.out.println("树开始生长");
                for (String u : Nv) {
                    if(g.comNodes.get(u)!=null){
                        if (shouldGrow(g.comNodes.get(u).c , tv.needs ,needs)) {
                            done = true;
                            CompressedTree newTree = new CompressedTree();
                            newTree.num = tv.num + 1;
                            newTree.needs.addAll(tv.needs);
                            newTree.nodes.addAll(tv.nodes);
                            newTree.needs.addAll(g.comNodes.get(u).c);
                            newTree.nodes.add(u);
                            LT.offer(newTree);
                            if(k1containk2(newTree.needs,needs) && newTree.nodes.size()<=max_size){
                                allTree.add(newTree);
                                 return newTree;
                            }
                        }
                    }

                }

                //树的合并
                //System.out.println("树开始合并");
                Set<String> k1 = new HashSet<>();
                k1.addAll(tv.needs);
                for ( int i = 0 ;i<LT.size();i++) {
                    CompressedTree tv2 =LT.get(i);
                    if(!tv.nodes.equals(tv2.nodes) && tv.nodes.size()>1){
                        Set<String> node12 = new HashSet<>();
                        node12.addAll(tv.nodes);
                        node12.addAll(tv2.nodes);
                        Set<String> k12 = new HashSet<>();
                        k12.addAll(tv.needs);
                        k12.addAll(tv2.needs);
                        if(node12.size()<=max_size && TotalDiff(tv.nodes,tv2.nodes) &&
                                shouldMerge(Nv,tv2.nodes) && TotalDiff(tv.needs, tv2.needs)){
                            //System.out.println("树合并了一次");
                            CompressedTree merge =new CompressedTree();
                            merge.nodes.addAll(node12);
                            merge.num = node12.size();
                            merge.needs.addAll(k12);
                            done = true;
                            LT.remove(tv2);
                            LT.add(merge);
                            //System.out.println("合并"+merge.nodes);
                            if (k1containk2(merge.needs,needs) && merge.nodes.size()<=max_size){
                                allTree.add(merge);
                                 return merge;
                            }
                        }
                    }

                }

                if(!done){
                    //没有进行操作的话，需要生长
                    //System.out.println("树开始后生长");
                    for (String u : Nv) {
                            done = true;
                            CompressedTree newTree = new CompressedTree();
                            newTree.num = tv.num + 1;
                            newTree.needs.addAll(tv.needs);
                            newTree.nodes.addAll(tv.nodes);
                            newTree.needs.addAll(g.comNodes.get(u).c);
                            newTree.nodes.add(u);
                            LT.add(newTree);
                            //System.out.println("后生长"+newTree.nodes);
                            //QT.get(tv.root).add(tv);
                            if  (k1containk2(newTree.needs,needs) && newTree.nodes.size()<=4){
                                allTree.add(newTree);
                            }
                            break;
                        //}
                    }
                }

            }
        }
        updateTree();
        return minTree;
    }
    public static Set<Set<String>> getAllTree(){
        Set<Set<String>> all = new HashSet<>();
        for(CompressedTree aTree : allTree){
            Set<String> a  =new HashSet<>();
            a.addAll(aTree.nodes);
            all.add(a);
        }
        return all;
    }


}
