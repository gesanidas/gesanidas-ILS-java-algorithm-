package com.gesanidas;

import java.util.ArrayList;

/**
 * Created by gesanidas on 11/29/2017.
 */
public class CSCRatio
{
    ArrayList<ArrayList<POI>> tours =new ArrayList<>();
    ArrayList<ArrayList<POI>> bestFound =new ArrayList<>();
    ArrayList<ArrayList<POI>> bestClusters = new ArrayList<>();
    int MAX_ITERATIONS = 20;
    int numOfClusters = 10;
    int keepOnlyTheBestClusters = 5;


    public ArrayList<ArrayList<POI>> getBestSolution(ArrayList<POI> locs, double start, double budget, int m)
    {



        Shake shake = new Shake();
        ClusterInsertion clusterInsertion = new ClusterInsertion();
        ArrayList<ArrayList<POI>> listOfClusters;
        listOfClusters = KMeans.listOfClusters(locs, numOfClusters);
        bestClusters = clusterInsertion.selectClusterSet(listOfClusters, keepOnlyTheBestClusters);
        Integer[] arr = Utilities.keepIDsOfBestClusters(bestClusters, keepOnlyTheBestClusters);
        Utilities.combinations(arr, 3, 0, new Integer[3]);
        ArrayList<ArrayList<ArrayList<POI>>> clusterSets = Utilities.combinationSet(listOfClusters, Utilities.combinationClustersID);




        double it1=MAX_ITERATIONS/4;
        double it2=2*MAX_ITERATIONS/4;
        double it3=3*MAX_ITERATIONS/4;

        double clusterParameter;


        while (!clusterSets.isEmpty())
        {
            tours.clear();
            ArrayList<ArrayList<POI>> theClusterSetIdToInsert = clusterSets.get(clusterSets.size() - 1);
            clusterSets.remove(theClusterSetIdToInsert);
            //System.out.println("List of clusters : ");
            //Utilities.printTour(theClusterSetIdToInsert);
            tours = clusterInsertion.simRatioInitPhase(locs, tours, theClusterSetIdToInsert, m);

            int S=1;
            int R=1;
            int notImproved=0;

            while(notImproved<MAX_ITERATIONS)
            {
                if(notImproved<it2)
                {
                    if(notImproved<it1)
                    {
                        clusterParameter=1.3;
                    }
                    else
                    {
                        clusterParameter=1.2;
                    }
                }
                else
                {
                    if(notImproved<it3)
                    {
                        clusterParameter=1.1;
                    }
                    else
                    {
                        clusterParameter=1;
                    }
                }

                while(clusterInsertion.validateInsert(clusterInsertion.selectToInsert(locs,tours,clusterParameter),tours,budget))
                {
                    //System.out.println("------ Tour ------");
                    //Utilities.printTour(tours);
                    //System.out.println("Ratio : ");
                    //System.out.println(insertionStep.calcTourRatio(tours));
                    //System.out.println("Ratio of best found : ");
                    //System.out.println(insertionStep.calcTourRatio(bestFound));

                }
                if (clusterInsertion.calcTourRatio(tours)>clusterInsertion.calcTourRatio(bestFound))
                {
                    bestFound.clear();
                    bestFound = clusterInsertion.copyTourToBestFound(tours, bestFound);
                    R=1;
                    notImproved=0;
                }
                else
                {
                    notImproved++;
                }
                if(R>clusterInsertion.biggestTour(tours)/2)
                {
                    R=1;
                }
                shake.shake(tours, R, S, budget);
                S+=R;
                R++;
                if(S>=clusterInsertion.smallestTour(tours))
                {
                    S-=clusterInsertion.smallestTour(tours);
                }

            }
        }







        return bestFound;

    }
}
