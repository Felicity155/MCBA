package com.Entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

public class Tree {

    public Tree(Boolean direction){
        this.direction = direction;
        num=0;
        nodes = new LinkedList<>();
        needs = new LinkedList<>();
        edges = new HashSet<>();
    }
    public Tree(){

        num=0;
        nodes = new LinkedList<>();
        needs = new LinkedList<>();
        edges = new HashSet<>();
    }
    public Boolean direction ;
    public int num;//所含有用关键词数量
    public LinkedList<String> nodes;
    public Set<Edge> edges;
    public LinkedList<String> needs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree tree = (Tree) o;
        return Objects.equals(nodes, tree.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, num, nodes, edges, needs);
    }
}
