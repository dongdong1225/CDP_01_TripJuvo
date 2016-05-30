package com.knucapstone.tripjuvo.TSP;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by JYM on 2016-05-15.
 */
public class AdjMatrix
{
    private int numberOfNodes;
    private ArrayList<Location> locationList;
    private float adjacency_matrix[][];

    public AdjMatrix() //constructor
    {
        this.numberOfNodes = 0;
        this.locationList = new ArrayList<Location>();
    }

    public AdjMatrix(ArrayList<Location> locationList) //constructor
    {
        this.locationList = locationList;
        this.numberOfNodes = locationList.size();
    }

    public AdjMatrix(Location location) //constructor
    {
        this.locationList = new ArrayList<Location>();
        this.locationList.add(location);
        this.numberOfNodes = locationList.size();
    }

    public void addLocation(Location location)
    {
        this.locationList.add(location);
        this.numberOfNodes++;
    }

    public ArrayList<Location> makeAdjMatrix()
    {
        adjacency_matrix = new float[numberOfNodes + 1][numberOfNodes + 1];

        for (int i = 1; i<=numberOfNodes ; i++)
        {
            for (int j = 1; j<=numberOfNodes ; j++)
            {
                adjacency_matrix[i][j] = locationList.get(i - 1).distanceTo(locationList.get(j - 1));
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

        TSP tsp = new TSP(locationList);
        ArrayList<Location> sortedList = new ArrayList<Location>();
        sortedList = tsp.tsp(adjacency_matrix);

        return sortedList;
    }



}