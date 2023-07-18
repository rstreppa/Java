/**
An important concept in Object-Oriented Programming is the open/closed principle, which means writing code that is open to extension but closed to modification. In other words, new functionality should be added by writing an extension for the existing code rather than modifying it and potentially breaking other code that uses it. This challenge simulates a real-life problem where the open/closed principle can and should be applied.

A Tree class implementing a rooted tree is provided in the editor. It has the following publicly available methods:

getValue(): Returns the value stored in the node.
getColor(): Returns the color of the node.
getDepth(): Returns the depth of the node. Recall that the depth of a node is the number of edges between the node and the tree's root, so the tree's root has depth  and each descendant node's depth is equal to the depth of its parent node .
In this challenge, we treat the internal implementation of the tree as being closed to modification, so we cannot directly modify it; however, as with real-world situations, the implementation is written in such a way that it allows external classes to extend and build upon its functionality. More specifically, it allows objects of the TreeVis class (a Visitor Design Pattern) to visit the tree and traverse the tree structure via the accept method.

There are two parts to this challenge.

Part I: Implement Three Different Visitors
Each class has three methods you must write implementations for:

getResult(): Return an integer denoting the , which is different for each class:

The SumInLeavesVisitor implementation must return the sum of the values in the tree's leaves only.
The ProductRedNodesVisitor implementation must return the product of values stored in all red nodes, including leaves, computed modulo . Note that the product of zero values is equal to .
The FancyVisitor implementation must return the absolute difference between the sum of values stored in the tree's non-leaf nodes at even depth and the sum of values stored in the tree's green leaf nodes. Recall that zero is an even number.
visitNode(TreeNode node): Implement the logic responsible for visiting the tree's non-leaf nodes such that the getResult method returns the correct  for the implementing class' visitor.

visitLeaf(TreeLeaf leaf): Implement the logic responsible for visiting the tree's leaf nodes such that the getResult method returns the correct  for the implementing class' visitor.
Part II: Read and Build the Tree
Read the -node tree, where each node is numbered from  to . The tree is given as a list of node values (), a list of node colors (), and a list of edges. Construct this tree as an instance of the Tree class. The tree is always rooted at node number .

Your implementations of the three visitor classes will be tested on the tree you built from the given input.

Input Format

The first line contains a single integer, , denoting the number of nodes in the tree. The second line contains  space-separated integers describing the respective values of .
The third line contains  space-separated binary integers describing the respective values of . Each  denotes the color of the  node, where  denotes red and  denotes green.
Each of the  subsequent lines contains two space-separated integers,  and , describing an edge between nodes  and .

Constraints

It is guaranteed that the tree is rooted at node 1.
*/

import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

import java.util.ArrayList;
import java.util.Scanner;

enum Color {
    RED, GREEN
}

abstract class Tree {

    private int value;
    private Color color;
    private int depth;

    public Tree(int value, Color color, int depth) {
        this.value = value;
        this.color = color;
        this.depth = depth;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public int getDepth() {
        return depth;
    }

    public abstract void accept(TreeVis visitor);
}

class TreeNode extends Tree {

    private ArrayList<Tree> children = new ArrayList<>();

    public TreeNode(int value, Color color, int depth) {
        super(value, color, depth);
    }

    public void accept(TreeVis visitor) {
        visitor.visitNode(this);

        for (Tree child : children) {
            child.accept(visitor);
        }
    }

    public void addChild(Tree child) {
        children.add(child);
    }
}

class TreeLeaf extends Tree {

    public TreeLeaf(int value, Color color, int depth) {
        super(value, color, depth);
    }

    public void accept(TreeVis visitor) {
        visitor.visitLeaf(this);
    }
}

abstract class TreeVis
{
    public abstract int getResult();
    public abstract void visitNode(TreeNode node);
    public abstract void visitLeaf(TreeLeaf leaf);

}

class SumInLeavesVisitor extends TreeVis {

    int sumInLeaves = 0;

    public int getResult() {
        return sumInLeaves;
    }

    public void visitNode(TreeNode node) {
        // empty return
    }

    public void visitLeaf(TreeLeaf leaf) {
        sumInLeaves += leaf.getValue();
    }
}

class ProductOfRedNodesVisitor extends TreeVis {
    long productOfRedNodes = 1L;

    public int getResult() {
        return (int) (productOfRedNodes);
    }

    void multiply(Tree tree) {
        if (tree.getColor() == Color.RED)
            productOfRedNodes = (productOfRedNodes * tree.getValue()) % (1000000007);
    }

    public void visitNode(TreeNode node) {
        multiply(node);
    }

    public void visitLeaf(TreeLeaf leaf) {
        multiply(leaf);
    }
}

class FancyVisitor extends TreeVis {
    int sumOfValuesNonLeafEvenDepth = 0;
    int sumOfValuesGreenLeaf = 0;

    public int getResult() {

        return Math.abs(sumOfValuesGreenLeaf - sumOfValuesNonLeafEvenDepth);
    }

    public void visitNode(TreeNode node) {
        if (node.getDepth() % 2 != 0) return;
        sumOfValuesNonLeafEvenDepth += node.getValue();
    }

    public void visitLeaf(TreeLeaf leaf) {
        if (leaf.getColor() != Color.GREEN) return;
        sumOfValuesGreenLeaf += leaf.getValue();
    }
}

public class Solution {
    static Map<Integer, Tree> tree = new HashMap<>();

    public static Tree solve() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Map<Integer, Object[]> nodeAtts = new HashMap<Integer, Object[]>();

        for (int i = 0; i < n; i++)
            nodeAtts.put(i + 1, new Object[]{sc.nextInt(), null});

        for (int i = 0; i < n; i++)
            nodeAtts.get(i + 1)[1] = sc.nextInt() == 0 ? Color.RED : Color.GREEN;

        Map<Integer, ArrayList<Integer>> edges = new HashMap<Integer, ArrayList<Integer>>();

        for (int i = 1; i <= n; i++)
            edges.put(i, new ArrayList<Integer>());

        for (int i = 1; i < n; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            edges.get(u).add(v);
            edges.get(v).add(u);
        }
        Tree root = new TreeNode((Integer) nodeAtts.get(1)[0], (Color) nodeAtts.get(1)[1], 0);
        tree.put(1, root);

        DFS(n, edges, nodeAtts);
        return tree.get(1);
    }

    private static void DFS(int n, Map<Integer, ArrayList<Integer>> edges, Map<Integer, Object[]> nodeAtts) {
        boolean[] visited = new boolean[n + 1];
        TreeNode parent = (TreeNode) tree.get(1);
        DFSUtil(parent, 1, visited, edges, nodeAtts);

    }

    private static void DFSUtil(TreeNode parent, int v, boolean[] visited, Map<Integer, ArrayList<Integer>> edges, Map<Integer, Object[]> nodeAtts) {
        visited[v] = true;
        if (edges.get(v).size() == 1 && v != 1) {
            TreeLeaf treeLeaf = new TreeLeaf((Integer) nodeAtts.get(v)[0], (Color) nodeAtts.get(v)[1], parent.getDepth() + 1);
            parent.addChild(treeLeaf);
            tree.put(v, treeLeaf);
            return;
        }

        TreeNode treeNode;
        if (v != 1) {
            treeNode = new TreeNode((Integer) nodeAtts.get(v)[0], (Color) nodeAtts.get(v)[1], parent.getDepth() + 1);
            parent.addChild(treeNode);
            tree.put(v, treeNode);

        } else
            treeNode = (TreeNode) tree.get(1);

        Iterator<Integer> iterator = edges.get(v).iterator();
        while (iterator.hasNext()) {
            int n = iterator.next();
            if (!visited[n]) {
                DFSUtil(treeNode, n, visited, edges, nodeAtts);
            }
        }
    }


    public static void main(String[] args) {
        Tree root = solve();
    SumInLeavesVisitor vis1 = new SumInLeavesVisitor();
        ProductOfRedNodesVisitor vis2 = new ProductOfRedNodesVisitor();
        FancyVisitor vis3 = new FancyVisitor();

        root.accept(vis1);
        root.accept(vis2);
        root.accept(vis3);

        int res1 = vis1.getResult();
        int res2 = vis2.getResult();
        int res3 = vis3.getResult();

        System.out.println(res1);
      System.out.println(res2);
      System.out.println(res3);
  }
}





