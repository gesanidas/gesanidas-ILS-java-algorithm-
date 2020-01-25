package com.gesanidas;

import java.util.ArrayList;
import java.util.Random;

public class GRASP {

    ArrayList<POI> allTheLocations;
    ArrayList<POI> solution;
    double bestScore;
    Random random;

    public GRASP(ArrayList<POI> allTheLocations) {
        this.allTheLocations = allTheLocations;
        this.solution = new ArrayList<>();
        this.bestScore = 0;
        this.random = new Random();
    }

    public ArrayList<POI> initSolutionPOIs(int m, ArrayList<POI> allPOIs) {
        ArrayList<POI> tour = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            tour = new ArrayList<>();
            tour.add(new POI(allPOIs.get(0)));
            tour.add(new POI(allPOIs.get(allTheLocations.size() - 1)));
            allPOIs.remove(0);
            allPOIs.remove(allPOIs.size() - 1);
        }
        return tour;
    }

    public ArrayList<POI> generateCL(ArrayList<POI> solutionGRASP, ArrayList<POI> allTheLocations) {
        ArrayList<POI> candidateList = new ArrayList<>();
        for (POI poi:allTheLocations) {
            if (solutionGRASP.get(solutionGRASP.size() - 1).getArrival() +
                    calcPOIShift(solutionGRASP.get(solutionGRASP.size() - 2),
                    solutionGRASP.get(solutionGRASP.size() - 1) ,
                    poi) <= solutionGRASP.get(solutionGRASP.size() - 1).getClosing())
            {
                candidateList.add(poi);
            }
        }
        return candidateList;
    }

    public double findMin(ArrayList<POI> localCL, ArrayList<POI> solutionGRASP){
        double min = Double.MAX_VALUE;
        for (int i = 1; i < localCL.size(); i++) {
            double minFX = calcHeuristicGRASP(solutionGRASP.get(solutionGRASP.size() - 2),
                    solutionGRASP.get(solutionGRASP.size() - 1) ,
                    localCL.get(i));
            if (minFX < min) {
                min = minFX;
            }
        }
        return min;
    }

    public double findMax(ArrayList<POI> localCL, ArrayList<POI> solutionGRASP) {
        double max = Double.MIN_VALUE;
        for (int i = 1; i < localCL.size(); i++) {
            double maxFX = calcHeuristicGRASP(solutionGRASP.get(solutionGRASP.size() - 2),
                    solutionGRASP.get(solutionGRASP.size() - 1) ,
                    localCL.get(i));
            if (maxFX > max) {
                max = maxFX;
            }
        }
        return max;
    }

    public void updateArrival(ArrayList<POI> solutionGRASP) {
        for (int i = 1; i < solutionGRASP.size(); i++) {
            solutionGRASP.get(i).setArrival(solutionGRASP.get(i - 1).getArrival() + solutionGRASP.get(i - 1).calcDistance(solutionGRASP.get(i)));
        }
    }

    public ArrayList<POI> getGRASPSolution(double alpha, int m)
    {
        for (int i = 0; i < 10; i++) {
            ArrayList<POI> allPOIs = (ArrayList<POI>) this.allTheLocations.clone();
            ArrayList<POI> solutionGRASP = initSolutionPOIs(m, allPOIs);
            ArrayList<POI> CL = generateCL(solutionGRASP, allPOIs);
            while (CL.size() != 0) {
                double min = findMin(CL, solutionGRASP);
                double max = findMax(CL, solutionGRASP);
                double threshold = min + (alpha * (max - min));
                ArrayList<POI> RCL = new ArrayList<>();
                for (POI poi:CL)
                {
                    if (calcHeuristicGRASP(solutionGRASP.get(solutionGRASP.size() - 2),
                            solutionGRASP.get(solutionGRASP.size() - 1) ,
                            poi) >= threshold)
                    {
                        RCL.add(new POI(poi));
                    }
                }
                if (RCL.size() != 0) {
                    int index = random.nextInt(RCL.size());
                    solutionGRASP.add(solutionGRASP.size() - 1, RCL.get(index));
                    allPOIs.remove(RCL.get(index));
                    updateArrival(solutionGRASP);
                    CL = generateCL(solutionGRASP, allPOIs);
                } else {
                    break;
                }
            }
            solutionGRASP = localSearch(solutionGRASP);
            double solutionScoreGRASP = scoreFromTour(solutionGRASP);
            if (solutionScoreGRASP > bestScore) {
                bestScore = solutionScoreGRASP;
                this.solution = (ArrayList<POI>) solutionGRASP.clone();
            }
        }


        return solution;
    }

    public ArrayList<POI> localSearch(ArrayList<POI> solutionGRASP) {
        int improve = 0;
        double newCost = 0;
        ArrayList<POI> newSolutionForGRASP;
        while (improve < 20) {
            double bestCost = getScore(solutionGRASP);
            for (int i = 1; i < solutionGRASP.size() - 2; i++) {
                for (int j = i + 1; j < solutionGRASP.size() - 1; j++) {
                    newSolutionForGRASP = twoOptSwap(solutionGRASP, i, j);
                    double newScore = getScore(newSolutionForGRASP);
                    if (newScore > bestCost) {
                        improve = 0;
                        solutionGRASP = (ArrayList<POI>) newSolutionForGRASP.clone();
                        bestCost = newCost;
                    }
                }
            }
            improve++;
        }
        return solutionGRASP;
    }

    public ArrayList<POI> twoOptSwap(ArrayList<POI> solutionGRASP, int i, int j) {
        ArrayList<POI> newFoundedSolution = new ArrayList<>();
        for (int k = 0; k <= i - 1; k++) {
            newFoundedSolution.add(new POI(solutionGRASP.get(k)));
        }
        for (int k = i; k <= j ; k++) {
            newFoundedSolution.add(new POI(solutionGRASP.get(k)));
        }
        for (int k = j + 1; k < solutionGRASP.size() - 1; k++) {
            newFoundedSolution.add(new POI(solutionGRASP.get(k)));
        }
        return newFoundedSolution;
    }

    public double getScore(ArrayList<POI> solutionGRASP) {
        double score = 0;
        for (int i = 0; i < solutionGRASP.size() - 2; i++) {
            score =+ solutionGRASP.get(i).calcDistance(solutionGRASP.get(i + 1));
        }
        return score;
    }

    public double scoreFromTour(ArrayList<POI> solutionGRASP) {
        double score = 0;
        for (POI poi:solutionGRASP) {
            score += poi.getScore();
        }
        return score;
    }

    public double calcPOIShift(POI i, POI j, POI l) {
        return i.calcTime(l) + l.calcTime(j) - i.calcTime(j);
    }

    public double calcHeuristicGRASP(POI i, POI j, POI l) {
        return l.getScore()/(i.calcTime(l) + l.calcTime(j) - i.calcTime(j));
    }
}
