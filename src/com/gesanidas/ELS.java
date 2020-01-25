package com.gesanidas;

import java.util.ArrayList;
import java.util.Random;

public class ELS {
    public ArrayList<ArrayList<POI>> ELS(ArrayList<POI> locs, int m, int ns, int ni, int nc, int kMax, int pMin, int pMax)
    {

    Insertion insertion = new Insertion();
    Heuristics heuristics = new Heuristics();

        ArrayList<ArrayList<POI>> bestFound = new ArrayList<>();

        for (int i = 0; i <ns ; i++)
        {

            ArrayList<ArrayList<POI>> initialSolution = new ArrayList<>();
            ArrayList<ArrayList<POI>> currentSolution = new ArrayList<>();


            if (i==1)
            {
                ArrayList<ArrayList<ArrayList<POI>>> heuristicsResults=new ArrayList<>();
                heuristicsResults.add(heuristics.heuristicOne(locs,m));
                heuristicsResults.add(heuristics.heuristicTwo(locs,m));
                heuristicsResults.add(heuristics.heuristicThree(locs,m));
                heuristicsResults.add(heuristics.sweepOne(locs,m));
                heuristicsResults.add(heuristics.sweepTwo(locs,m));

                double score=0;
                for (int l = 0; l < heuristicsResults.size(); l++)
                {
                    if(insertion.calcTourScore(heuristicsResults.get(l))>score)
                    {
                        score=insertion.calcTourScore(heuristicsResults.get(l));
                        initialSolution=heuristicsResults.get(l);
                    }
                }
            }
            else
            {
                ArrayList<ArrayList<ArrayList<POI>>> heuristicsResults=new ArrayList<>();
                heuristicsResults.add(heuristics.heuristicOne(locs,m));
                heuristicsResults.add(heuristics.heuristicTwo(locs,m));
                heuristicsResults.add(heuristics.heuristicThree(locs,m));
                heuristicsResults.add(heuristics.sweepOne(locs,m));
                heuristicsResults.add(heuristics.sweepTwo(locs,m));

                int min = 0;
                int max = 5;

                Random r = new Random();
                int number = r.nextInt(max);
                initialSolution=heuristicsResults.get(number);


            }

            currentSolution=VND(initialSolution,kMax);
            if(insertion.calcTourScore(currentSolution)>insertion.calcTourScore(bestFound))
            {
                bestFound=currentSolution;
            }

            int p=pMin;

            for(int j=0;j<ni;j++)
            {
                ArrayList<ArrayList<POI>> bestChildSolution = new ArrayList<>();

                for(int c=0;c<nc;c++)
                {

                    ArrayList<ArrayList<POI>> childSolution = perturbation(currentSolution,p);
                    ArrayList<ArrayList<POI>> perturbedSolution=VND(childSolution,kMax);
                    if(insertion.calcTourScore(perturbedSolution)>insertion.calcTourScore(bestChildSolution))
                    {
                        bestChildSolution=perturbedSolution;
                    }

                }
                if(insertion.calcTourScore(bestChildSolution)>insertion.calcTourScore(currentSolution))
                {
                    currentSolution=bestChildSolution;
                    p=pMin;
                }
                else
                {
                    p=Math.min(pMax,p+1);
                }

            }

            if(insertion.calcTourScore(currentSolution)>insertion.calcTourScore(bestFound))
            {
                bestFound=currentSolution;
            }

        }






        Utilities.printTour(bestFound);

        return bestFound;
    }


    public ArrayList<ArrayList<POI>> VND(ArrayList<ArrayList<POI>> initialSolution,int Kmax)
    {
        return initialSolution;
    }


    public ArrayList<ArrayList<POI>> perturbation(ArrayList<ArrayList<POI>> initialSolution,int p)
    {
        return initialSolution;
    }

}
