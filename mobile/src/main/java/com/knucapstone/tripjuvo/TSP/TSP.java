package com.knucapstone.tripjuvo.TSP;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by JYM on 2016-05-15.
 */
public class TSP {

    private int numberOfNodes;
    private Stack<Integer> stack;
    private ArrayList<Spot> spotArrayList;
    private ArrayList<Spot> sortedList;

    public TSP()
    {
        stack = new Stack<>();
    }

    public TSP(ArrayList<Spot> spotArrayList)
    {
        this.stack = new Stack<>();
        this.spotArrayList = spotArrayList;
        this.sortedList = new ArrayList<>();
    }

    public ArrayList<Spot> tsp(float adjacencyMatrix[][])
    {
        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        float min = Integer.MAX_VALUE;
        boolean minFlag = false;
        sortedList.add(spotArrayList.get(0)); //시작 지점은 고정

        while (!stack.isEmpty()) {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes) {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0) {
                    if (min > adjacencyMatrix[element][i]) {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag) {
                visited[dst] = 1;
                stack.push(dst);
                sortedList.add(spotArrayList.get(dst-1));
                minFlag = false;
                continue;
            }
            stack.pop();
        }

        return sortedList;
    }

}
