package com.gesanidas;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gesanidas on 7/15/2017.
 */

public class ILS
{




    ArrayList<ArrayList<POI>> tours =new ArrayList<>();
    ArrayList<ArrayList<POI>> bestFound =new ArrayList<>();
    Insertion insertion = new Insertion();
    Shake shake=new Shake();




    public ArrayList<ArrayList<POI>> getBestSolution(ArrayList<POI> locs, double start, double budget, int m)
    {

        /*
         * insertion : Insertion - Instantiate an Insertion object
         * tour : ArrayList<POI> - The tour with the POIs
         */

        for (int i = 0; i < m; i++)
        {
            tours.add(insertion.simRandomTour(locs, tours));
        }


        insertion.initialCalculations(tours,0);






        int S = 1;
        int R = 1;


        int noImprovementCounter = 0;

        while (noImprovementCounter < 150)
        {
            System.out.println(" Started inserting");

            while (insertion.validateInsert(insertion.selectToInsert(locs,tours),tours,budget))
            {


                //System.out.println("Ratio of new tour");
                //System.out.println(insertion.calcTourRatio(tours));
                //System.out.println("Ratio of best found");
                //System.out.println(insertion.calcTourRatio(bestFound));
                Utilities.printTour(tours);



            }
            if (insertion.calcTourScore(tours) > insertion.calcTourScore(bestFound))
            {
                bestFound.clear();
                bestFound = insertion.copyTourToBestFound(tours, bestFound);
                R = 1;
                noImprovementCounter = 0;
            }
            else
            {
                noImprovementCounter++;
            }
            System.out.println("finished inserting");
            System.out.println("Printing tour after last insertion");

            Utilities.printTour(tours);

            //System.out.println("Ratio of new tour");
            //System.out.println(insertionStep.calcTourRatio(tours));
            //System.out.println("Ratio of best found");
            //System.out.println(insertionStep.calcTourRatio(bestFound));
            System.out.println("started shaking");
            System.out.println("R is " + R + " S is "+ S);
            shake.shake(tours, R, S, budget);


            System.out.println("finished shaking");
            System.out.println("Printing tour after shaking");
            Utilities.printTour(tours);

            //System.out.println("Ratio of new tour");
            //System.out.println(insertionStep.calcTourRatio(tours));
            //System.out.println("Ratio of best found");
            //System.out.println(insertionStep.calcTourRatio(bestFound));

            S = S + R;
            R++;

            int sizeST = insertion.smallestTour(tours);
            if (S > sizeST)
            {
                S = S - sizeST;
            }
            if (R >= insertion.toursSize(tours) / 3 * m)
            {
                R = 1;
            }
        }

        System.out.println("best found");
        Utilities.printTour(bestFound);

        return bestFound;

    }
}
