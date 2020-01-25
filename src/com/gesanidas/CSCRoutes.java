package com.gesanidas;

import java.util.ArrayList;

/**
 * Created by gesanidas on 11/28/2017.
 */
public class CSCRoutes
{
    ArrayList<ArrayList<POI>> tours =new ArrayList<>();
    ArrayList<ArrayList<POI>> bestFound =new ArrayList<>();
    int MAX_ITERATIONS = 40;



    public ArrayList<ArrayList<POI>> getBestSolution(ArrayList<POI> locs, double start, double budget, int m)
    {




        ArrayList<ArrayList<POI>> listOfClusters;
        listOfClusters=KMeans.listOfClusters(locs, 10);
        ClusterInsertion clusterInsertion = new ClusterInsertion();
        Shake shake = new Shake();

        while (!listOfClusters.isEmpty())
        {
            tours.clear();
            ArrayList<ArrayList<POI>> theClusterSetIdToInsert = clusterInsertion.selectClusterSet(listOfClusters, m);
            listOfClusters.removeAll(theClusterSetIdToInsert);
            //System.out.println("List of clusters : ");
            //Utilities.printTour(theClusterSetIdToInsert);
            tours = clusterInsertion.simRatioInitPhase(locs, tours, theClusterSetIdToInsert, m);

            int S=1;
            int R=1;
            int notImproved=0;

            while(notImproved<MAX_ITERATIONS)
            {
                while(clusterInsertion.validateInsert(clusterInsertion.selectToInsertRoutes(locs, tours),tours,budget))
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
