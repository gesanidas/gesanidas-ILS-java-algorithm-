package com.gesanidas;

import java.util.*;

public class InsertionELS
{


    public ArrayList<ArrayList<POI>> simRandomTour(ArrayList<POI> pois, int m)
    {

        ArrayList<ArrayList<POI>> tours =new ArrayList<>();

        for (int i = 0; i <m ; i++)
        {

            ArrayList<POI> tour = new ArrayList<>();

            tour.add(new POI(pois.get(0)));
            tour.add(new POI(pois.get(pois.size() - 1)));
            tours.add(tour);

        }



        return tours;
    }


    public double calcPOIRatio(POI poi,double shift,double cardinality)  // calculates ratio of POI per page 3283, should be called after calcShift
    {
        if (shift!=0)
        {
            return cardinality*poi.getScore()/shift;
        }
        return 0;
    }


    public double calcTourRatio(ArrayList<POI> tour)  // calculates whole tour ratio , to evaluate if this is the best possible tour found
    {
        double ratio=0;

        for (POI poi:tour)
        {
                ratio += poi.getRatio();
        }

        return ratio;
    }


    public double calcTourScore(ArrayList<POI> tour)  // calculates whole tour ratio , to evaluate if this is the best possible tour found
    {
        double score=0;

        for (POI poi:tour)
        {
                score += poi.getScore();
        }



        return score;
    }


    public boolean tourContains(ArrayList<POI> tour, POI poi)
    {
        ArrayList<String> allNames = new ArrayList<>();

        for (POI poiss:tour)
        {
                allNames.add(poiss.getName());
        }

        int itContainsThePOI = allNames.indexOf(poi.getName());
        if (itContainsThePOI != -1)
        {
            return true;
        }
        else
            { return false; }

    }

    public ArrayList<POI> copyLocs(ArrayList<POI> locs)
    {
        ArrayList<POI> copiedLocs = new ArrayList<>();
        for (POI poi:locs)
        {
            copiedLocs.add(new POI(poi));
        }
        return copiedLocs;
    }


    public double calcShift(ArrayList<POI> pois, POI j)   // function 11 of page 3283
    {
        POI i=pois.get(pois.size()-2);
        POI k=pois.get(pois.size()-1);
        j.setArrival(i.getLeave() + i.calcTime(j));
        j.setWait(calcWait(j));
        j.setStart(j.getArrival() + j.getWait());
        j.setLeave(calcLeave(j));
        double shift = i.calcTime(j)+j.getWait()+j.getStayDuration()+j.calcTime(k)-i.calcTime(k);

        if (i.getLeave()+shift+k.getWait()+k.stayDuration<=k.getClosing()
                && j.getArrival() + j.getStayDuration() <= j.getClosing())
        {
            j.setShift(shift);
            return shift;
        }

        return Double.MAX_VALUE;
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

    public double calcArrival(ArrayList<POI> pois, int index)   //this is a question mark- tries to estimate arrival time to POI at position index
    {
        return (pois.get(index-1).getLeave() + pois.get(index - 1).calcTime(pois.get(index)));
    }

    public double calcLeave(POI poi)      //this is a question mark- tries to estimate departure time from POI ,]. should be called after calcArrival,calcMaxShift,calcShift and calcWait
    {
        return (Math.min(poi.getClosing(),poi.getStart()+poi.getStayDuration()));
    }

    public double calcWait(POI poi)  // function 10 of page 3283 . it calculates the wait of an already established route
    {
        return Math.max(0, poi.getOpening() - poi.getArrival());
    }


    public POI selectToInsertInSingleTour (ArrayList<POI> pois, ArrayList<POI> tour)
    {
        double percentage = 1;
        double sumScore = 0;
        ArrayList<POI> sortedPOIS=new ArrayList<>();

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

            if (poi.getScore() > selectionPoint && !tourContains(tour, poi))
            {
                potentialAdditions.add(poi);
            }
        }

        ArrayList<POI> potentialCopy = copyLocs(potentialAdditions);
        // For each POI into the potentialAdditions calculate the best Shift and position
        for (POI poi: potentialCopy)
        {

            double shift = calcShift(tour, poi);
            poi.setShift(shift);
            poi.setRatio(calcPOIRatio(poi, shift, potentialCopy.size()));
            sortedPOIS.add(poi);
        }

        Comparator<POI> ratioComp = new Comparator<POI>()
        {
            @Override
            public int compare(POI poi1, POI poi2)
            {
                return Double.compare(poi1.getRatio(), poi2.getRatio());
            }
        };

        ArrayList<POI> toBeRemoved = new ArrayList<>();

//        for (POI poi:sortedPOIS) {
//            if (poi.shift == Double.MAX_VALUE) {
//                toBeRemoved.add(poi);
//            }
//        }
//
//        if (toBeRemoved.size() != sortedPOIS.size()) {
//            sortedPOIS.removeAll(toBeRemoved);
//        }

        Collections.sort(sortedPOIS,ratioComp);

        return sortedPOIS.get(sortedPOIS.size() - 1);
    }

    public TreeMap<POI,HashMap<Integer, Integer>> selectToInsertInHeuristicTwo(ArrayList<POI> pois, ArrayList<ArrayList<POI>> tours){
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
//                HashSet<Integer> idsOfClusters = Utilities.returnIDsOfCluster(tours.get(z));

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
            poi.setRatio(calcPOIRatio(poi, poi.getShift(), 1));

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

    public TreeMap<POI,HashMap<Integer, Integer>> selectToInsertInHeuristicThree(POI poi, ArrayList<ArrayList<POI>> tours)
    {
        int bestPosition = 0;
        int bestTour = 0;
        double bestShift = Double.MAX_VALUE;
        // For each POI into the potentialAdditions calculate the best Shift and position
        for (int z = 0; z < tours.size(); z++) {

            // CSCRoutes get the list with clusters name
            //HashSet<Integer> idsOfClusters = Utilities.returnIDsOfCluster(tours.get(z));

            for (int i = 1; i <= tours.get(z).size() - 1; i++) {
                double shift = calcShift(tours.get(z), i, poi);
                if (shift < bestShift) {
                    bestShift = shift;
                    bestPosition = i;
                    bestTour = z;
                    //System.out.println("Calculated shift is :" + shift);
                }
            }
        }

        poi.setShift(bestShift);
        TreeMap<POI, HashMap<Integer, Integer>> bestPoiAndPosition = new TreeMap<>();
        HashMap<Integer, Integer> bestPositionsAndTour = new HashMap<>();
        bestPositionsAndTour.put(bestPosition, bestTour);
        bestPoiAndPosition.put(poi, bestPositionsAndTour);
        //System.out.println("-----------------------------");
        //System.out.println("POI to be added is "+bestPoiAndPosition.firstKey());
        //System.out.println("Position to be inserted: "+bestPoiAndPosition.get(bestPoiAndPosition.firstKey()));



        return bestPoiAndPosition;

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

    public boolean validateInsert(ArrayList<POI> tour, POI toBeAdded, ArrayList<POI> potentialAdditions)
    {
        if(toBeAdded.getShift() == Double.MAX_VALUE)
        {
            return false;
        }
        else
        {
            tour.add(tour.size()-1,toBeAdded);
            potentialAdditions.remove(toBeAdded);
            return true;
        }
    }



    public ArrayList<POI> sortedOnScore(ArrayList<POI> tour)
    {
        Comparator<POI> profitComp = new Comparator<POI>()
        {
            @Override
            public int compare(POI poi1, POI poi2)
            {
                return Double.compare(poi1.getScore(), poi2.getScore());
            }
        };

        Collections.sort(tour,profitComp);

        return tour;

    }


    public ArrayList<ArrayList<POI>> initiliazeTours(ArrayList<POI> potentialAdditions, int m)
    {
        ArrayList<ArrayList<POI>> tours=new ArrayList();
        ArrayList<POI> sorted=sortedOnScore(potentialAdditions);
        for (int i = 0; i <m ; i++)
        {
            tours.get(i).add(sorted.get(0));
            sorted.remove(0);

        }
        return tours;
    }

    public ArrayList<ArrayList<POI>> initPoi(ArrayList<POI> locs, int m)
    {
        ArrayList<ArrayList<POI>> tours = new ArrayList();

        for (int i = 0; i < m; i++)
        {
            ArrayList<POI> tempTour = new ArrayList<>();
            tempTour.add(new POI(locs.get(0)));
            tempTour.add(new POI(locs.get(locs.size() - 1)));
            tours.add(tempTour);
        }

        return tours;
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
        }

        Utilities.printTour(tours);
    }

    /* --------------------------------------
    *  Heuristic Two methods
    *  --------------------------------------*/

    public void initialCalculationsTwo(ArrayList<ArrayList<POI>> tours, double start)
    {


        for (ArrayList<POI> pois:tours)
        {
            for (int i = 0; i < pois.size(); i++)
            {
                if (i == 0) {
                    pois.get(i).setArrival(start);
                } else {
                    pois.get(i).setArrival(calcArrival(pois, i));
                }
                pois.get(i).setWait(calcWait(pois.get(i)));
                pois.get(i).setStart(calcStart(pois.get(i)));
                pois.get(i).setLeave(calcLeave(pois.get(i)));
            }
            calcMaxShift(pois);
        }

        Utilities.printTour(tours);
    }

    public ArrayList<ArrayList<POI>> sortLocsAndAddLoc(ArrayList<POI> locs, ArrayList<ArrayList<POI>> tours){
        locs = sortedOnScore(locs);
        for (int i = 0; i < tours.size(); i++) {
            tours.get(i).add(tours.get(i).size() - 1, locs.get(locs.size() - 1));
            locs.remove(locs.size() - 1);
        }
        return tours;
    }

    public boolean validateInsertTwo(ArrayList<ArrayList<POI>> tours, TreeMap<POI, HashMap<Integer, Integer>> poiShifts, ArrayList<POI> potentialAdditions)
    {
            Integer pos = 0, tour = 0;
            POI toBeAdded = poiShifts.firstKey();
            //System.out.println("POI from validate "+toBeAdded);
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
                calcShift(tours.get(tour), pos, toBeAdded);
                updateTourAfterInsertion(tours.get(tour), pos);
                //Utilities.printTour(tours);
                return true;
            }
    }

    public int insertPOI (TreeMap<POI, Integer> poiShifts, ArrayList<POI> tour)
    {
        //calcShift(tour,poiShifts.get(poiShifts.lastKey()),poiShifts.lastKey());
        // Add the POI into the tour
        tour.add(poiShifts.get(poiShifts.lastKey()),poiShifts.lastKey());
        //System.out.println("Succesfully inserted");

        return poiShifts.get(poiShifts.lastKey());
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

    /* --------------------------------------
     *  Heuristic Three methods
     *  --------------------------------------*/

    public void removerInitPOIs(ArrayList<POI> locs){
        locs.remove(0);
        locs.remove(locs.size() - 1);
    }

    /* --------------------------------------
     *  Sweep One methods
     *  --------------------------------------*/
    public ArrayList<ArrayList<POI>> clusterLocations(ArrayList<POI> locs, int m){
        ArrayList<ArrayList<POI>> clusteredPOIs = new ArrayList<>();
        int sizeOfClusters = locs.size() / m;

        for (int i = 1; i <= m; i++) {
            ArrayList<POI> tempCluster = new ArrayList<>();
            if (i == m)
            {
                for (int j = 0; j < locs.size(); j++) {
                    tempCluster.add(locs.get(j));
                }
            }
            else {
                for (int j = 0; j < sizeOfClusters; j++) {
                    tempCluster.add(locs.get(j));
                }
            }
            clusteredPOIs.add((ArrayList<POI>) tempCluster.clone());
            locs.removeAll(tempCluster);
        }

        return clusteredPOIs;
    }

    /* --------------------------------------
     *  Sweep Two methods
     *  --------------------------------------*/

    public HashMap<POI, Integer> selectToInsertInSweepTwo(POI poi, ArrayList<POI> tours){

        int bestPositions;
        ArrayList<Double> shifts = new ArrayList<>();
        // For each POI into the potentialAdditions calculate the best Shift and position

        for (int i = 1; i <= tours.size() - 1; i++) {
            double shift = calcShift(tours, i, poi);
            shifts.add(shift);
            //System.out.println("Calculated shift is :" + shift);
        }


        bestPositions = shifts.indexOf(Collections.min(shifts)) + 1;

        // Get the minimum shift of the tour
        //System.out.println("Best position for " + poi.getName() + " is " + bestPosition);
        poi.setShift(shifts.get(bestPositions - 1));
        poi.setRatio(calcPOIRatio(poi, poi.getShift(), 1));
        //System.out.println("-----------------------------");
        //System.out.println("POI to be added is "+bestPoiAndPosition.firstKey());
        //System.out.println("Position to be inserted: "+bestPoiAndPosition.get(bestPoiAndPosition.firstKey()));

        HashMap<POI, Integer> POIAndPosition = new HashMap<>();

        POIAndPosition.put(poi, bestPositions);

        return POIAndPosition;

    }

    public boolean validateInsertSweepTwo(ArrayList<POI> tours, HashMap<POI, Integer> poiShifts)
    {
        Integer pos = 0;
        POI toBeAdded = null;
        //System.out.println("POI from validate "+toBeAdded);
        for (POI poi:poiShifts.keySet()) {
            toBeAdded = poi;
        }

        pos = poiShifts.get(toBeAdded);
        //System.out.println("pos from alidate "+pos);

        if(toBeAdded.getShift() == Double.MAX_VALUE)
        {
            return false;
        }
        else
        {
            TreeMap<POI, Integer> poiPos = new TreeMap<>();
            poiPos.put(toBeAdded, pos);
            insertPOI(poiPos, tours);
            calcShift(tours, pos, toBeAdded);
            updateTourAfterInsertion(tours, pos);
            //Utilities.printTour(tours);
            return true;
        }
    }

}
