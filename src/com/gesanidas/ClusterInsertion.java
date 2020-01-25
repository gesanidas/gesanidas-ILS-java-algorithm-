package com.gesanidas;

import java.util.*;

public class ClusterInsertion {

    //1.initial calculations
    //////////////////////////////////////////////////////////////////////////////////
    ////Here begin the functions covered in left side of page 3283. they compute starting variables of the first randomly chosen tour


    public double calcWait(POI poi)  // function 10 of page 3283 . it calculates the wait of an already established route
    {
        return Math.max(0,poi.getOpening()-poi.getArrival());
    }



    public void calcMaxShift(ArrayList<POI> pois)   // function 9 of page 3283 -question mark :calculates maxShift for entire array
    {

        pois.get(pois.size()-1).setMaxShift(pois.get(pois.size() - 1).getClosing() - pois.get(pois.size()-1).getStart());
        for (int index=pois.size()-2;index>0;index--)
        {
            double maxShift=Math.min(pois.get(index).getClosing()-pois.get(index).getStart(),pois.get(index+1).getWait()+pois.get(index+1).getMaxShift());
            pois.get(index).setMaxShift(maxShift);
        }

    }



    public double calcArrival(ArrayList<POI> pois,int index)   //this is a question mark- tries to estimate arrival time to POI at position index
    {
        return (pois.get(index-1).getLeave()+pois.get(index-1).calcTime(pois.get(index)));
    }

    public double calcLeave(POI poi)      //this is a question mark- tries to estimate departure time from POI ,]. should be called after calcArrival,calcMaxShift,calcShift and calcWait
    {
        return (Math.min(poi.getClosing(),poi.getStart()+poi.getStayDuration()));
    }

    public double calcStart(POI poi)
    {
        return (poi.getArrival()+poi.getWait());
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
            if (shift<=k.getWait()+k.getMaxShift()-k.getStayDuration()  &&

                    j.getLeave() - j.getStart() >= j.getStayDuration() &&
                    i.getStart() + i.getStayDuration()+ i.calcTime(j) <= j.getClosing() &&
                    j.getStart() + j.getStayDuration() + j.calcTime(k) <= k.getClosing()  &&
                    j.getOpening() - i.getOpening() >= j.getStayDuration() &&
                    i.getLeave()+ i.calcTime(j) <= j.getClosing() - j.getStayDuration() &&
                    j.getLeave()+ j.calcTime(k) <= k.getClosing() - k.getStayDuration()



                    )
            {
                j.setShift(shift);
                return shift;
            }

            return Double.MAX_VALUE;
        }


        return Double.MAX_VALUE;   //???????????

    }


    /////////////////////// Update Tour After Insertion

    public void updateTourAfterInsertion(ArrayList<POI> pois,int index,double budget)   //question mark. updates all values after inserted position
    {
        for (int i = index + 1; i < pois.size(); i++) {
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
                break;
            }
        }
        for (int i = index-1; i >= 0; i--) {
            updateMaxShiftAfterInsertionForPreviousPOIs(pois, i);
        }
    }


    public void updateLeaveAfterInsertion(ArrayList<POI> pois,int index) //question mark
    {
        pois.get(index).setLeave(pois.get(index).getLeave() + pois.get(index).getShift());
    }


    public double updateShiftAfterInsertion(ArrayList<POI> pois, int index)   //returns correct shift of POI k
    {
        double shift;
        shift = Math.max(0, pois.get(index - 1).getShift() - pois.get(index).getWait());
        pois.get(index).setShift(shift);
        return shift;
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


    public void updateMaxShiftAfterInsertion(ArrayList<POI> pois, int index){
        pois.get(index).setMaxShift(pois.get(index).getMaxShift() - pois.get(index).getShift());
    }


    public void updateMaxShiftAfterInsertionForPreviousPOIs(ArrayList<POI> pois, int index) {
        pois.get(index).setMaxShift(Math.min(pois.get(index).getClosing() - pois.get(index).getStart(), pois.get(index + 1).getWait() + pois.get(index + 1).getMaxShift()));
    }

    ////////////////////// Insert
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
            updateTourAfterInsertion(tours.get(tour), pos, budget);
            //Utilities.printTour(tours);
            return true;
        }

    }

    public int insertPOI (TreeMap<POI, Integer> poiShifts, ArrayList<POI> tour)
    {
        calcShift(tour,poiShifts.get(poiShifts.lastKey()),poiShifts.lastKey());
        // Add the POI into the tour
        tour.add(poiShifts.get(poiShifts.lastKey()),poiShifts.lastKey());
        //System.out.println("Succesfully inserted");

        return poiShifts.get(poiShifts.lastKey());
    }

    public TreeMap<POI,HashMap<Integer, Integer>> selectToInsert (ArrayList<POI> pois, ArrayList<ArrayList<POI>> tours, double clusterParameter)
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

        // For each POI into the potentialAdditions calculate the best Shift and position
        for (POI poi: potentialAdditions)
        {
            for (int z = 0; z < tours.size(); z++) {

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
            poi.setRatio(calcPOIRatio(poi, poi.getShift()/clusterParameter));

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

    public TreeMap<POI,HashMap<Integer, Integer>> selectToInsertRoutes (ArrayList<POI> pois, ArrayList<ArrayList<POI>> tours)
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

        // For each POI into the potentialAdditions calculate the best Shift and position
        for (POI poi: potentialAdditions)
        {
            for (int z = 0; z < tours.size(); z++) {

                // CSCRoutes get the list with clusters name
                HashSet<Integer> idsOfClusters = Utilities.returnIDsOfCluster(tours.get(z));

                for (int i = 1; i <= tours.get(z).size() - 1; i++) {
                    if (checkTheCluster(poi, tours.get(z), i, idsOfClusters)){
                        double shift = calcShift(tours.get(z), i, poi);
                        shifts.add(shift);
                        //System.out.println("Calculated shift is :" + shift);
                    } else {
                        shifts.add(Double.MAX_VALUE);
                    }
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



    ////////////////////////////////////// Help functions
    public boolean toursContain(ArrayList<ArrayList<POI>> tours, POI poi){
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

    public ArrayList<ArrayList<POI>> simRatioInitPhase(ArrayList<POI> locs, ArrayList<ArrayList<POI>> tours, ArrayList<ArrayList<POI>> allClusters, int m)
    {
        Integer[] positions = new Integer[allClusters.size()];
        int j = 0;
        ArrayList<POI> tour = new ArrayList<>();
        for (ArrayList<POI> cluster:allClusters) {
            int maxRatioOfCluster = 0;

            for (int i = 1; i < cluster.size(); i++) {
                if (cluster.get(i).getScore() > cluster.get(maxRatioOfCluster).getScore()) {
                    maxRatioOfCluster = i;
                }
            }
            positions[j] = maxRatioOfCluster;
            j++;
        }

        List<POI> bestPOIs = new ArrayList<>();

        for (int i = 0; i < positions.length; i++) {
            bestPOIs.add(allClusters.get(i).get(positions[i]));
        }

        for (int z = 0; z < m; z++) {
            tour.add(locs.get(0));
            tour.add(getBestPOI(bestPOIs));
            tour.add(locs.get(locs.size() - 1));
            tours.add(new ArrayList<>(tour));
            tour.clear();
        }

        return tours;
    }

    public POI getBestPOI(List<POI> bestPOIs){
        POI best = bestPOIs.get(0);
        for (POI poi:bestPOIs) {
            if (poi.getScore() > best.getScore()){
                best = poi;
            }
        }
        bestPOIs.remove(best);
        return best;
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

    public int biggestTour(ArrayList<ArrayList<POI>> tours)
    {
        int biggestTourSize = 0;
        for (int i = 1; i < tours.size() - 1; i++)
        {
            if (tours.get(i).size() > tours.get(biggestTourSize).size())
            {
                biggestTourSize = i;
            }
        }
        return tours.get(biggestTourSize).size(); ///red flag
    }

    public double calcClusterRatio(ArrayList<POI> cluster)  // calculates whole tour ratio , to evaluate if this is the best possible tour found
    {
        double ratio = 0;
        for (POI poi:cluster){
            ratio += poi.getScore();
        }

        return ratio;
    }


    public ArrayList<ArrayList<POI>> selectClusterSet (ArrayList<ArrayList<POI>> listOfClusters, int m)
    {
        HashMap<ArrayList<POI>, Double> sortedClusters = new HashMap<>();
        ArrayList<ArrayList<POI>> biggestClusters = new ArrayList<>();
        for (int i = 0; i < listOfClusters.size(); i++) {
            sortedClusters.put(listOfClusters.get(i), calcClusterRatio(listOfClusters.get(i)));
        }
        for (int i = 0; i < m; i++) {
            biggestClusters.add(maxClusterRatio(sortedClusters));
        }
        return biggestClusters;
    }

    public ArrayList<POI> maxClusterRatio(HashMap<ArrayList<POI>, Double> ratioCluster)
    {
        Map.Entry<ArrayList<POI>, Double> maxEntry = null;

        for(Map.Entry<ArrayList<POI>, Double> entry : ratioCluster.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        ratioCluster.remove(maxEntry.getKey());

        return maxEntry.getKey();
    }

    //Used only for CSCRoutes
    public boolean checkTheCluster(POI candidatePOI, ArrayList<POI> tour, int i, HashSet<Integer> idsOfClusters)
    {
        if (candidatePOI.getmCluster() == tour.get(0).getmCluster() &&
                idsOfClusters.size() == 1 &&
                idsOfClusters.contains(candidatePOI.getmCluster()))
        {
            return true;
        }
        else if (candidatePOI.getmCluster() == tour.get(0).getmCluster() &&
                idsOfClusters.size() > 1)
        {
            if (candidatePOI.getmCluster() == tour.get(i - 1).getmCluster() || candidatePOI.getmCluster() == tour.get(i).getmCluster()) {
                return true;
            } else {
                return false;
            }
        }
        else if (candidatePOI.getmCluster() != tour.get(0).getmCluster() &&
                idsOfClusters.size() == 1 &&
                idsOfClusters.contains(tour.get(0).getmCluster()))
        {
            return true;
        }
        else if (candidatePOI.getmCluster() != tour.get(0).getmCluster() &&
                idsOfClusters.size() > 1 &&
                !idsOfClusters.contains(candidatePOI.getmCluster()))
        {
            if (candidatePOI.getmCluster() != tour.get(i - 1).getmCluster() &&
                    candidatePOI.getmCluster() != tour.get(i).getmCluster() &&
                    tour.get(i - 1).getmCluster() != tour.get(i).getmCluster())
            {
                return true;
            } else {
                return false;
            }
        }
        else if (candidatePOI.getmCluster() != tour.get(0).getmCluster() &&
                idsOfClusters.size() > 1 &&
                idsOfClusters.contains(candidatePOI.getmCluster()))
        {
            if (candidatePOI.getmCluster() == tour.get(i - 1).getmCluster() || candidatePOI.getmCluster() == tour.get(i).getmCluster()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public double calcPOIRatio(POI poi,double shift)  // calculates ratio of POI per page 3283, should be called after calcShift
    {
        if (shift!=0)
        {
            return Math.sqrt(poi.getScore())/shift;
        }
        return 0;
    }
}
