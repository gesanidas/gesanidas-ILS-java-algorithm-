package com.gesanidas;

import com.gesanidas.InsertionELS;
import com.gesanidas.POI;

import java.util.ArrayList;

public class Heuristics
{

    InsertionELS insertionELS = new InsertionELS();



    public ArrayList<ArrayList<POI>> heuristicOne(ArrayList<POI> locs, int m)
    {
        ArrayList<ArrayList<POI>> tours = insertionELS.initPoi(locs, m);
        insertionELS.initialCalculations(tours,0);

        for(int i = 0; i < m; i++)
        {
            System.out.println("running");

            while(insertionELS.validateInsert(tours.get(i), insertionELS.selectToInsertInSingleTour(locs, tours.get(i)),locs))
            {
                System.out.println("Succesful insertion in tour "+i);
            }
        }
        Utilities.printTour(tours);

        return tours;
    }

    public ArrayList<ArrayList<POI>> heuristicTwo(ArrayList<POI> locs, int m)
    {
        ArrayList<ArrayList<POI>> tours = insertionELS.initPoi(locs, m);
        tours = insertionELS.sortLocsAndAddLoc(locs, tours);
        insertionELS.initialCalculationsTwo(tours,0);

        System.out.println("running");

        while(insertionELS.validateInsertTwo(tours, insertionELS.selectToInsertInHeuristicTwo(locs, tours), locs))
        {
            System.out.println("Succesful insertion in tour ");
        }

        Utilities.printTour(tours);

        return tours;
    }

    public ArrayList<ArrayList<POI>> heuristicThree(ArrayList<POI> locs, int m)
    {
        ArrayList<ArrayList<POI>> tours = insertionELS.initPoi(locs, m);
        insertionELS.removerInitPOIs(locs);
        insertionELS.initialCalculationsTwo(tours,0);
        locs = insertionELS.sortedOnScore(locs);

        for (int i = locs.size() - 1; i >= 0 ; i--) {
            if (insertionELS.validateInsertTwo(tours, insertionELS.selectToInsertInHeuristicThree(locs.get(i), tours), locs))
            {
                System.out.println("Successful insertion in tour");
            }
        }

        Utilities.printTour(tours);

        return tours;
    }

    public ArrayList<ArrayList<POI>> sweepOne(ArrayList<POI> locs, int m)
    {
        ArrayList<ArrayList<POI>> tours = insertionELS.initPoi(locs, m);
        insertionELS.removerInitPOIs(locs);
        insertionELS.initialCalculationsTwo(tours,0);
        locs = insertionELS.sortedOnScore(locs);

        ArrayList<ArrayList<POI>> clusteredLos = insertionELS.clusterLocations(locs, m);

        for (int i = 0; i < m; i++)
        {
            tours.get(i).add(1,clusteredLos.get(i).get(0));
        }
        insertionELS.initialCalculationsTwo(tours,0);





        for (int i = 0; i < m; i++)
        {
            // Needs improvement
            while(insertionELS.validateInsertTwo(tours, insertionELS.selectToInsertInHeuristicTwo(clusteredLos.get(i), tours), clusteredLos.get(i)))
            {
                System.out.println("Succesful insertion in tour ");
            }
        }

        Utilities.printTour(tours);

        return tours;
    }


    public ArrayList<ArrayList<POI>> sweepTwo(ArrayList<POI> locs, int m)
    {
        ArrayList<ArrayList<POI>> tours = insertionELS.initPoi(locs, m);
        insertionELS.removerInitPOIs(locs);
        insertionELS.initialCalculationsTwo(tours,0);
        locs = insertionELS.sortedOnScore(locs);

        ArrayList<ArrayList<POI>> clusteredLos = insertionELS.clusterLocations(locs, m);


        for (int i = 0; i < m; i++)
        {
            for (int j = clusteredLos.get(i).size() - 1; j >= 0 ; j--) {
                if (insertionELS.validateInsertSweepTwo(tours.get(i), insertionELS.selectToInsertInSweepTwo(clusteredLos.get(i).get(j), tours.get(i))))
                {
                System.out.println("Successful insertion in tour");
                }
            }
        }

        Utilities.printTour(tours);

        return tours;
    }




}
