package com.gesanidas;

import javax.naming.NamingEnumeration;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gesanidas on 11/27/2017.
 */
public  class KMeans
{
    public static ArrayList<ArrayList<POI>> listOfClusters(ArrayList<POI> allTheLocations,int numOfClusters)
    {
        //init
        //http://mnemstudio.org/clustering-k-means-example-1.htm

        ArrayList<ArrayList<POI>> listOfClusters=new ArrayList<>();

        final double bigNumber = Math.pow(10, 10);
        double minimum;
        double distance;
        int cluster = 0;
        boolean isStillMoving = true;


        final int NUM_CLUSTERS = numOfClusters;    // Total clusters.
        // Add in new data, one at a time, recalculating centroids with each new one.


        ArrayList<POI> toBeClustered =new ArrayList<>(allTheLocations);
        toBeClustered.remove(0);
        toBeClustered.remove(toBeClustered.size()-1);

        final int TOTAL_DATA = toBeClustered.size();      // Total data points.




        ArrayList<POI> centroids=new ArrayList<>();
        ArrayList<POI> dataSet = new ArrayList<>();
        POI newPOI=null;
        int sampleNumber = 0;


        //Collections.shuffle(toBeClustered);

        //System.out.println("The centers are:");

        for(int i=0;i<NUM_CLUSTERS;i++)
        {
            centroids.add(toBeClustered.get(i));
            //System.out.println(toBeClustered.get(i));
        }


        while(dataSet.size()<toBeClustered.size())
        {
            newPOI=toBeClustered.get(sampleNumber);
            dataSet.add(newPOI);
            minimum = bigNumber;
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                distance = newPOI.calcDistance(centroids.get(i));
                if(distance < minimum)
                {
                    minimum = distance;
                    cluster = i;
                }
            }
            newPOI.setmCluster(cluster);

            // calculate new centroids.
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                int totalX = 0;
                int totalY = 0;
                int totalInCluster = 0;
                for(int j = 0; j < toBeClustered.size(); j++)
                {
                    if(toBeClustered.get(j).getmCluster() == i)
                    {
                        totalX += toBeClustered.get(j).getX();
                        totalY += toBeClustered.get(j).getY();
                        totalInCluster++;
                    }
                }
                if(totalInCluster > 0)
                {
                    double newX=totalX / totalInCluster;
                    double newY=totalY / totalInCluster;

                    centroids.set(i,new POI("newcenter",newX,newY));

                    //centroids.get(i).setX(newX);
                    //centroids.get(i).setY(newY);
                }
            }
            sampleNumber++;

        }






        // Now, keep shifting centroids until equilibrium occurs.


        while(isStillMoving)
        {
            // calculate new centroids.
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                int totalX = 0;
                int totalY = 0;
                int totalInCluster = 0;
                for(int j = 0; j < dataSet.size(); j++)
                {
                    if(dataSet.get(j).getmCluster() == i)
                    {
                        totalX += dataSet.get(j).getX();
                        totalY += dataSet.get(j).getY();
                        totalInCluster++;
                    }
                }
                if(totalInCluster > 0)       //red flag///////////////////////////////////////////////////////////
                {
                    double newX=totalX / totalInCluster;
                    double newY=totalY / totalInCluster;
                    centroids.set(i,new POI("new center",newX,newY));


                    //centroids.get(i).setX(newX);
                    //centroids.get(i).setY(newY);
                }
            }

            // Assign all data to the new centroids
            isStillMoving = false;

            for(int i = 0; i < dataSet.size(); i++)
            {
                POI tempPOI = dataSet.get(i);
                minimum = bigNumber;
                for(int j = 0; j < NUM_CLUSTERS; j++)
                {
                    distance = tempPOI.calcDistance(centroids.get(j));

                    if(distance < minimum)
                    {
                        minimum = distance;
                        cluster = j;
                    }
                }
                tempPOI.setmCluster(cluster);
                if(tempPOI.getmCluster() != cluster)
                {
                    tempPOI.setmCluster(cluster);
                    isStillMoving = true;
                }
            }
        }








        // Print out clustering results.
        for(int i = 0; i < NUM_CLUSTERS; i++)
        {
            ArrayList<POI> tempCluster=new ArrayList<>();

            //System.out.println("Cluster " + i + " includes:");
            for(int j = 0; j < dataSet.size(); j++)
            {
                if(dataSet.get(j).getmCluster() == i)
                {
                    //System.out.println("(" + toBeClustered.get(j).getName()  + ")");
                    tempCluster.add(dataSet.get(j));
                }

            }
            listOfClusters.add(i,tempCluster);



        }



        // Print out centroid results.
        //System.out.println("Centroids finalized at:");
        for(int i = 0; i < NUM_CLUSTERS; i++)
        {
            //System.out.println("(" + centroids.get(i) + ", " + centroids.get(i));
        }
        //System.out.print("\n");



        return listOfClusters;

    }
}

