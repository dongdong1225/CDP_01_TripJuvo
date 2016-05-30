package com.knucapstone.tripjuvo.TSP;

import java.util.ArrayList;

/**
 * Created by JYM on 2016-05-15.
 */
public class AdjMatrix
{
    private int numberOfNodes;
    private ArrayList<Spot> spotArrayList;
    private float adjacency_matrix[][];

    public AdjMatrix() //constructor
    {
        this.numberOfNodes = 0;
        this.spotArrayList = new ArrayList<Spot>();
    }

    public AdjMatrix(ArrayList<Spot> spotArrayList) //constructor
    {
        this.spotArrayList = spotArrayList;
        this.numberOfNodes = spotArrayList.size();
    }

    public AdjMatrix(Spot spot) //constructor
    {
        this.spotArrayList = new ArrayList<>();
        this.spotArrayList.add(spot);
        this.numberOfNodes = spotArrayList.size();
    }

    public void addSpot(Spot spot)
    {
        this.spotArrayList.add(spot);
        this.numberOfNodes++;
    }

    public ArrayList<Spot> makeAdjMatrix()
    {
        adjacency_matrix = new float[numberOfNodes + 1][numberOfNodes + 1];

        for (int i = 1; i<=numberOfNodes ; i++)
        {
            for (int j = 1; j<=numberOfNodes ; j++)
            {
                adjacency_matrix[i][j] = spotArrayList.get(i - 1).distanceTo(spotArrayList.get(j - 1));
            }
        }

        for (int i = 1; i <= numberOfNodes; i++)
        {
            for (int j = 1; j <= numberOfNodes; j++)
            {
                if (adjacency_matrix[i][j] == 1 && adjacency_matrix[j][i] == 0)
                {
                    adjacency_matrix[j][i] = 1;
                }
            }
        }

//        for (int i = 0; i<=numberOfNodes ; i++)
//        {
//            for (int j = 0; j<=numberOfNodes ; j++)
//            {
//                System.out.printf("%15f ", adjacency_matrix[i][j]);
//            }
//            System.out.println("");
//        }

        TSP tsp = new TSP(spotArrayList);
        ArrayList<Spot> sortedList = new ArrayList<>();
        sortedList = tsp.tsp(adjacency_matrix);

        return sortedList;
    }



}