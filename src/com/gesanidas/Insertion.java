package com.gesanidas;

import java.util.*;

public class Insertion
{
    //1.initial calculations
    //////////////////////////////////////////////////////////////////////////////////
    ////Here begin the functions covered in left side of page 3283. they compute starting variables of the first randomly chosen tour

    public ArrayList<POI> simRandomTour(ArrayList<POI> pois, ArrayList<ArrayList<POI>> tours)
    {

        ArrayList<POI> tour = new ArrayList<>();

        tour.add(new POI(pois.get(0)));
        tour.add(new POI(pois.get(pois.size() - 1)));

        return tour;
    }



    public double calcWait(POI poi)  // function 10 of page 3283 . it calculates the wait of an already established route
    {
        return Math.max(0, poi.getOpening() - poi.getArrival());
    }



    public void calcMaxShift(ArrayList<POI> pois)   // function 9 of page 3283 -question mark :calculates maxShift for entire array
    {

        pois.get(pois.size()-1).setMaxShift(pois.get(pois.size() - 1).getClosing() - pois.get(pois.size()-1).getStart());
        for (int index=pois.size()-2;index>0;index--)
        {
            double maxShift=Math.min(pois.get(index).getClosing()-pois.get(index).getStart()-pois.get(index).getStayDuration(),
                    pois.get(index+1).getWait()+pois.get(index+1).getMaxShift());
            pois.get(index).setMaxShift(maxShift);
        }

    }



    public double calcArrival(ArrayList<POI> pois,int index)   //this is a question mark- tries to estimate arrival time to POI at position index
    {
        return (pois.get(index-1).getLeave() + pois.get(index - 1).calcTime(pois.get(index)));
    }

    public double calcLeave(POI poi)      //this is a question mark- tries to estimate departure time from POI ,]. should be called after calcArrival,calcMaxShift,calcShift and calcWait
    {
        return (Math.min(poi.getClosing(),poi.getStart()+poi.getStayDuration()));
    }

    public double calcStart(POI poi)
    {
        return (poi.getArrival()+poi.getWait());
    }

    public void initialCalculations(ArrayList<ArrayList<POI>> tours, double start)
    {


        for (ArrayList<POI> pois:tours)
        {
            pois.get(0).setArrival(start);
            pois.get(0).setWait(calcWait(pois.get(0)));
            pois.get(0).setStart(calcStart(pois.get(0)));
            pois.get(0).setLeave(calcLeave(pois.get(0)));
            pois.get(pois.size()-1).setMaxShift(pois.get(pois.size()-1).getClosing());

            calcTourRatio(tours);
        }

        Utilities.printTour(tours);
    }








    public double calcShift(ArrayList<POI> pois, int index, POI j)   // function 11 of page 3283
    {
        if (index >= 1 && index <= pois.size() - 1)
        {
            POI i=pois.get(index-1);
            POI k=pois.get(index);
            j.setArrival(i.getLeave() + i.calcTime(j));
            j.setWait(calcWait(j));
            j.setStart(j.getArrival() + j.getWait());
            j.setLeave(calcLeave(j));
            double shift= i.calcTime(j)+j.getWait()+j.getStayDuration()+j.calcTime(k)-i.calcTime(k);
            if (shift <= k.getWait()+k.getMaxShift()-k.getStayDuration()  &&
                    j.getLeave() - j.getStart() >= j.getStayDuration() &&
                    i.getStart() + i.getStayDuration()+ i.calcTime(j) <= j.getClosing() &&
                    j.getStart() + j.getStayDuration() + j.calcTime(k) <= k.getClosing()

//                    j.getOpening() - i.getOpening() >= j.getStayDuration() &&
//                    i.getLeave()+ i.calcTime(j) <= j.getClosing() - j.getStayDuration() &&
//                    j.getLeave()+ j.calcTime(k) <= k.getClosing() - k.getStayDuration()



                    )
            {
                j.setShift(shift);
                return shift;
            }

            return Double.MAX_VALUE;
        }


        return Double.MAX_VALUE;   //???????????

    }



    //2. Ratio calculations
    ///////////////here are the functions that compute the ratios///////////





    public double calcPOIRatio(POI poi,double shift)  // calculates ratio of POI per page 3283, should be called after calcShift
    {
        if (shift!=0)
        {
            return Math.sqrt(poi.getScore())/shift;
        }
        return 0;
    }


    public double calcTourRatio(ArrayList<ArrayList<POI>> pois)  // calculates whole tour ratio , to evaluate if this is the best possible tour found
    {
        double ratio=0;
        for (ArrayList<POI> tour:pois)
        {
            for (POI poi:tour){
                ratio += poi.getRatio();
            }
        }


        return ratio;
    }

    public double calcTourScore(ArrayList<ArrayList<POI>> pois)  // calculates whole tour ratio , to evaluate if this is the best possible tour found
    {
        double score=0;
        for (ArrayList<POI> tour:pois)
        {
            for (POI poi:tour){
                score += poi.getScore();
            }
        }


        return score;
    }









    //3. Insertion step
    ///////////////////////////////////////////////////////////////////////////////////

    /*
     * This method adds the POI into the tour
     *
     * @param poiShifts : TreeMap<POI, Integer> A Map with the best POI and the best position
     * @param tour : ArrayList<POI> The tour with the initial POIs
     *
     * @return int : Return the position
     *
     */

    public int insertPOI (TreeMap<POI, Integer> poiShifts, ArrayList<POI> tour)
    {
        calcShift(tour,poiShifts.get(poiShifts.lastKey()),poiShifts.lastKey());
        // Add the POI into the tour
        tour.add(poiShifts.get(poiShifts.lastKey()),poiShifts.lastKey());
        //System.out.println("Succesfully inserted");

        return poiShifts.get(poiShifts.lastKey());
    }


    /*
     * This method calculates the best position in order to insert a POI
     * into the tour
     *
     * @param pois : ArrayList<POI> ArrayList with all the possible locations
     * @param tour : ArrayList<POI> The tour with the initial POIs
     *
     * @return bestPOIAndPosition : TreeMap<POI, Integer> Return a TreeMap with the POI and the position
     *
     */

    public TreeMap<POI,HashMap<Integer, Integer>> selectToInsert (ArrayList<POI> pois, ArrayList<ArrayList<POI>> tours)
    {
        double percentage = 1;
        double sumScore = 0;

        // Sum the score of POIs
        for (POI poi:pois)
        {
            sumScore += poi.getScore();
        }

        // Calculate the mean score
        double selectionPoint = sumScore * percentage / 100;
        ArrayList<POI> potentialAdditions = new ArrayList<>();
        for (POI poi:pois)
        {
            // Keep only the POIs with score greater than the mean
            if (poi.getScore() > selectionPoint && !toursContain(tours, poi))
            {
                potentialAdditions.add(poi);
            }
        }
        //System.out.println("Potential Addition Size is :" + potentialAdditions.size());


        HashMap<POI, Integer> poiShifts = new HashMap<>();
        HashMap<POI, Integer> poiTour = new HashMap<>();
        ArrayList<Integer> bestPositions = new ArrayList<>();
        ArrayList<Double> shiftsOfBestPositions = new ArrayList<>();


        ArrayList<Double> shifts = new ArrayList<>();
        ArrayList<ArrayList<Double>> shiftsTour = new ArrayList<>();
        ArrayList<POI> potentialCopy = copyLocs(potentialAdditions);
        // For each POI into the potentialAdditions calculate the best Shift and position
        for (POI poi: potentialCopy)
        {
            for (int z = 0; z < tours.size(); z++) {

                // CSCRoutes get the list with clusters name
                HashSet<Integer> idsOfClusters = Utilities.returnIDsOfCluster(tours.get(z));

                for (int i = 1; i <= tours.get(z).size() - 1; i++) {
                    double shift = calcShift(tours.get(z), i, poi);
                    shifts.add(shift);
                    //System.out.println("Calculated shift is :" + shift);
                }
                shiftsTour.add(new ArrayList<>(shifts));
                shifts.clear();
            }


            for (int j = 0; j < tours.size(); j++) {
                bestPositions.add(j, shiftsTour.get(j).indexOf(Collections.min(shiftsTour.get(j))) + 1);
                shiftsOfBestPositions.add(j, shiftsTour.get(j).get(bestPositions.get(j) - 1));
            }

            int bestTour = 0;

            for (int i = 1; i < shiftsOfBestPositions.size(); i++) {
                if (shiftsOfBestPositions.get(i) < shiftsOfBestPositions.get(bestTour)) {
                    bestTour = i;
                }
            }

            // Get the minimum shift of the tour
            //System.out.println("Best position for " + poi.getName() + " is " + bestPosition);
            poi.setShift(shiftsOfBestPositions.get(bestTour));
            poi.setRatio(calcPOIRatio(poi, poi.getShift()));

            //System.out.println("Minimum shifts is "+poi.getShift());
            poiShifts.put(poi, bestPositions.get(bestTour));
            poiTour.put(poi, bestTour);

            //poi.setShift(Collections.min(shifts));
            //poi.setRatio(calcPOIRatio(poi,Collections.min(shifts)));
            //poiShifts.put(poi,bestPosition);
            shifts.clear();
            bestPositions.clear();
            shiftsOfBestPositions.clear();
            shiftsTour.clear();
        }



        Comparator<POI> ratioComp = new Comparator<POI>()
        {
            @Override
            public int compare(POI poi1, POI poi2)
            {
                return Double.compare(poi1.getRatio(), poi2.getRatio());
            }
        };

        POI maxRatio = Collections.max(poiShifts.keySet(), ratioComp);
        //System.out.println("Biggest ratio is "+maxRatio+" "+maxRatio.getRatio());
        //System.out.println("Pos is "+poiShifts.get(maxRatio));

        TreeMap<POI, HashMap<Integer, Integer>> bestPoiAndPosition = new TreeMap<>();
        HashMap<Integer, Integer> bestPositionsAndTour = new HashMap<>();
        bestPositionsAndTour.put(poiShifts.get(maxRatio), poiTour.get(maxRatio));
        bestPoiAndPosition.put(maxRatio, bestPositionsAndTour);
        //System.out.println("-----------------------------");
        //System.out.println("POI to be added is "+bestPoiAndPosition.firstKey());
        //System.out.println("Position to be inserted: "+bestPoiAndPosition.get(bestPoiAndPosition.firstKey()));



        return bestPoiAndPosition;

    }



    public boolean validateInsert(TreeMap<POI, HashMap<Integer, Integer>> poiShifts, ArrayList<ArrayList<POI>> tours, double budget)
    {
        Integer pos = 0, tour = 0;
        POI toBeAdded = poiShifts.firstKey();
        //System.out.println("POI from alidate "+toBeAdded);
        for ( Map.Entry<Integer, Integer> entry : poiShifts.get(poiShifts.firstKey()).entrySet())
        {
            pos = entry.getKey();
            tour = entry.getValue();
        }
        //System.out.println("pos from alidate "+pos);

        if(toBeAdded.getShift() == Double.MAX_VALUE)
        {
            return false;
        }
        else
        {
            TreeMap<POI, Integer> poiPos = new TreeMap<>();
            poiPos.put(toBeAdded, pos);
            insertPOI(poiPos, tours.get(tour));
            updateTourAfterInsertion(tours.get(tour),pos);
            //Utilities.printTour(tours);
            return true;
        }

    }


    //4.  Utility functions for insertion
    //////////////////////////////////////////////////////////////////////////////////////


    public int toursSize(ArrayList<ArrayList<POI>> tours) {
        int totalSize = 0;
        for (ArrayList<POI> tour:tours)
        {
            totalSize += tour.size();
        }
        return totalSize-2* tours.size();
    }

    public int smallestTour(ArrayList<ArrayList<POI>> tours)
    {
        int smallestTourSize = 0;
        for (int i = 1; i < tours.size(); i++)
        {
            if (tours.get(i).size() < tours.get(smallestTourSize).size())
            {
                smallestTourSize = i;
            }
        }
        return tours.get(smallestTourSize).size() - 2;
    }






    public boolean toursContain(ArrayList<ArrayList<POI>> tours, POI poi)
    {
        ArrayList<String> allNames = new ArrayList<>();
        for (ArrayList<POI> tour:tours){
            for (POI poiss:tour){
                allNames.add(poiss.getName());
            }
        }
        int itContainsThePOI = allNames.indexOf(poi.getName());
        if (itContainsThePOI != -1){
            return true;
        } else {
            return false;
        }
//        for (ArrayList<POI> tour:tours) {
//            if (tour.contains(poi)) {
//                return true;
//            }
//        }
//        return false;
    }


    public ArrayList<ArrayList<POI>> copyTourToBestFound(ArrayList<ArrayList<POI>> tours, ArrayList<ArrayList<POI>> bestFound){
        ArrayList<POI> tourToBeAdded = new ArrayList<>();
        for (int o = 0; o < tours.size(); o++) {
            for (POI poi:tours.get(o)){
                tourToBeAdded.add(new POI(poi));
            }
            bestFound.add(new ArrayList<>(tourToBeAdded));
            tourToBeAdded.clear();
        }
        return bestFound;
    }

    public ArrayList<POI> copyLocs(ArrayList<POI> locs){
        ArrayList<POI> copiedLocs = new ArrayList<>();
        for (POI poi:locs){
            copiedLocs.add(new POI(poi));
        }
        return copiedLocs;
    }





    //4.  After Insertion//////////////////////////////////////


    public double updateShiftAfterInsertion(ArrayList<POI> pois, int index)   //returns correct shift of POI k
    {
        double shift;
        shift = Math.max(0, pois.get(index - 1).getShift() - pois.get(index).getWait());
        pois.get(index).setShift(shift);
        return shift;
    }

    public void updateLeaveAfterInsertion(ArrayList<POI> pois,int index) //question mark
    {
        pois.get(index).setLeave(Math.min(pois.get(index).getClosing(),pois.get(index).getStart() + pois.get(index).getStayDuration()));
    }


    public void updateWaitAfterInsertion(ArrayList<POI> pois,int index)    //looks correct
    {
        pois.get(index).setWait(Math.max(0, pois.get(index).getWait() - pois.get(index - 1).getShift()));
    }

    public void updateArrivalAfterInsertion(ArrayList<POI> pois,int index)     //looks correct
    {
        pois.get(index).setArrival(pois.get(index).getArrival() + pois.get(index - 1).getShift());
    }


    public void updateStartAfterInsertion(ArrayList<POI> pois,int index)     //looks correct
    {
        pois.get(index).setStart(pois.get(index).getStart() + pois.get(index).getShift());
    }

    public void updateMaxShiftAfterInsertion(ArrayList<POI> pois, int index)
    {
        pois.get(index).setMaxShift(pois.get(index).getMaxShift() - pois.get(index).getShift());
    }


    public void updateMaxShiftAfterInsertionForPreviousPOIs(ArrayList<POI> pois, int index)
    {

        pois.get(index).setMaxShift(Math.min(pois.get(index).getClosing()-pois.get(index).getStart(),pois.get(index+1).getWait()+pois.get(index+1).getMaxShift()));

        //pois.get(index).setMaxShift(Math.min(pois.get(index).getClosing() - pois.get(index).getStart(), pois.get(index + 1).getWait() + pois.get(index + 1).getMaxShift()));
    }







    public void updateTourAfterInsertion(ArrayList<POI> pois,int index)   //question mark. updates all values after inserted position
    {
        for (int i = index + 1; i < pois.size(); i++)
        {
            if (updateShiftAfterInsertion(pois, i) != 0)
            {
                updateWaitAfterInsertion(pois, i);
                updateArrivalAfterInsertion(pois, i);
                updateStartAfterInsertion(pois, i);
                updateLeaveAfterInsertion(pois, i);
                updateMaxShiftAfterInsertion(pois, i);
            }
            else
            {
                updateWaitAfterInsertion(pois, i);
                updateArrivalAfterInsertion(pois, i);
                updateStartAfterInsertion(pois, i);
                updateLeaveAfterInsertion(pois, i);
                updateMaxShiftAfterInsertion(pois, i);
                break;
            }
        }
        for (int i = index;i>=1; i--)
        {
            updateMaxShiftAfterInsertionForPreviousPOIs(pois, i);
        }
    }


    public double calcNumberOfVisits(ArrayList<ArrayList<POI>> pois)  // calculates whole tour ratio , to evaluate if this is the best possible tour found
    {
        int numVisits=0;
        for (ArrayList<POI> tour:pois)
        {
            numVisits+=tour.size();

        }
        return numVisits-2*pois.size();
    }

}
