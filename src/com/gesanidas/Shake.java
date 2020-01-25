package com.gesanidas;

import java.util.ArrayList;

public class Shake
{

    Insertion insertion = new Insertion();



    public ArrayList<ArrayList<POI>> shake(ArrayList<ArrayList<POI>> tours, int R, int S, double budget)  //ignore-for now
    {
        System.out.println("R is " + R + " S is " + S);
        for (ArrayList<POI> tour:tours)
        {
            ArrayList<POI> toBeRemoved = new ArrayList<>();
            if (S >= tour.size()) {
                S = 1;
            }
            for (int i = S; i < R + S ; i++)
            {
                if (tour.get(i).getId() == tour.get(tour.size() - 1).getId())
                {
                    i = 1;
                    S = R - toBeRemoved.size();
                    R = 1;
                    continue;
                }
                else if (i == 0)
                {
                    i = 1;
                    continue;
                }
                else
                {
                    toBeRemoved.add(tour.get(i));
                }
            }


            tour.removeAll(toBeRemoved);
            shakeCalculations(tours);
        }



        return tours;
    }



    public void shakeCalculations(ArrayList<ArrayList<POI>> tours)
    {
        for (ArrayList<POI> pois:tours)
        {
            for (int i = 1; i < pois.size(); i++)
            {
                pois.get(i).setArrival(insertion.calcArrival(pois,i));
                pois.get(i).setWait(insertion.calcWait(pois.get(i)));
                pois.get(i).setStart(insertion.calcStart(pois.get(i)));
                pois.get(i).setLeave(insertion.calcLeave(pois.get(i)));
            }
            insertion.calcMaxShift(pois);
            insertion.calcTourRatio(tours);
        }
    }











}
