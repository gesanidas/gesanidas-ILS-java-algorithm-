package com.gesanidas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class Utilities
{

    public static ArrayList<Integer[]> combinationClustersID = new ArrayList<>();

    public static double[][] createTimes(ArrayList<POI> pois)
    {
        double[][] times=new double[pois.size()][pois.size()];
        for (int i=0;i<pois.size();i++)
        {
            for (int j=0;j<pois.size();j++)
            {
                times[i][j]=pois.get(i).calcTime(pois.get(j));
            }

        }
        return times;
    }

    public static ArrayList<POI> returnAllPOIsOfClusters(ArrayList<ArrayList<POI>> listOfClusters){
        ArrayList<POI> allPOIs = new ArrayList<>();
        for (ArrayList<POI> cluster:listOfClusters)
        {
            for (POI poi:cluster)
            {
                allPOIs.add(poi);
            }
        }
        return allPOIs;
    }

    public static HashSet<Integer> returnIDsOfCluster(ArrayList<POI> tour)
    {
        HashSet<Integer> idsOfCluster = new HashSet<>();
        for (POI poi:tour)
        {
            idsOfCluster.add(poi.getmCluster());
        }
        return idsOfCluster;
    }

    public static Integer[] keepIDsOfBestClusters(ArrayList<ArrayList<POI>> bestClusters, int number)
    {
        Integer[] ids = new Integer[number];
        int i = 0;
        for (ArrayList<POI> cluster:bestClusters)
        {
            ids[i] = cluster.get(0).getmCluster();
            i++;
        }
        return ids;
    }

    public static ArrayList<ArrayList<ArrayList<POI>>> combinationSet(ArrayList<ArrayList<POI>> listOfClusters, ArrayList<Integer[]> ids)
    {
        ArrayList<POI> toBeInserted = new ArrayList<>();
        ArrayList<ArrayList<POI>> listCl = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<POI>>> sets = new ArrayList<>();
        for (Integer[] combination:ids)
        {
            for (int i = 0; i < combination.length; i++)
            {
                for (ArrayList<POI> pois:listOfClusters)
                {
                    for (POI poi:pois)
                    {
                        if (poi.getmCluster() == combination[i]){
                            toBeInserted.add(poi);
                        }
                    }
                }
                listCl.add(new ArrayList<>(toBeInserted));
                toBeInserted.clear();
            }
            sets.add(new ArrayList<>(listCl));
            listCl.clear();
        }
        return sets;
    }

    /*
     * This method prints the POIs
     *
     * @param pois : ArrayList<POI> The list with the POIs
     *
     */

    public static void printTour(ArrayList<ArrayList<POI>> pois)
    {
        int name = 0;
        for (ArrayList<POI> tour: pois)
        {
            System.out.println("Tour " + name);
            for (POI poi:tour)
            {
                System.out.println(poi);
            }
            name++;
        }
    }


    public static void writeTour(ArrayList<ArrayList<POI>> pois,String fileName,String method)
    {

        try
        {

            FileWriter writer = new FileWriter(fileName + "_" + method + "_tour.txt");

            //FileWriter writer = new FileWriter(new File("src/com/gesanidas/results", fileName + "_" + method + "_tour.txt"));

            int name = 0;
            writer.write(System.getProperty("line.separator"));


            for (ArrayList<POI> tour: pois)
            {
                writer.write("Tour " + name);
                writer.write(System.getProperty("line.separator"));
                for (POI poi:tour)
                {
                    writer.write(poi.toString());
                    writer.write(System.getProperty("line.separator"));
                }
                name++;
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }






    /*
     * This method reads the text file that contains the locations
     *
     * @param fileName : String The name of the file
     *
     * @return lines : ArrayList<ArrayList<String>> Return the lines that we read from the file
     *
     */

    public static ArrayList<ArrayList<String>> readFile(String fileName) {

        /*
         * in : BufferedReader - Object needed in order to read the file
         * lines : ArrayList<ArrayList<String>> - The lines we read from the file
         */

        BufferedReader in = null;
        ArrayList<ArrayList<String>> lines = new ArrayList<>();

        // Start the process of reading the file
        try {
            in = new BufferedReader(new FileReader(fileName));
            String line;

            // We don't need the first two lines of the file
            in.readLine();
            in.readLine();

            while((line = in.readLine()) != null)
            {
                String[] splited = line.split(" ");
                lines.add(arrToList(splited));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the buffer
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Return the lines
        return lines;
    }

    /*
     * Static method that calculates the euclidean distance and return it
     *
     * @param poiOne : The first POI
     * @param poiTwo : The second POI
     *
     * @return euclideanDis : double Return the euclidean distance
     *
     */

    public static double euclideanDistance(POI poiOne, POI poiTwo)
    {
        double x1 = poiOne.getX();
        double y1 = poiOne.getY();
        double x2 = poiTwo.getX();
        double y2 = poiTwo.getY();

        double  xSqr  = Math.pow(x1 - x2, 2);
        double ySqr = Math.pow(y1 - y2, 2);

        double euclideanDis   = Math.sqrt(xSqr + ySqr);

        // Return the euclidean distance
        return euclideanDis;
    }

    public static ArrayList<String> arrToList(String[] array)
    {
        ArrayList<String> line = new ArrayList<>();
        for (int i=0; i < array.length; i++) {
            if (!array[i].isEmpty())
                line.add(array[i]);
        }
        return line;
    }


    public static ArrayList<POI> initPois(ArrayList<ArrayList<String>> lines, int n, double start, double end) {

        /*
         * pois : ArrayList<POI> - List with all the POIs
         */
        ArrayList<POI> pois = new ArrayList<>();

        // Add the first POI which is the start POI
        pois.add(new POI(Integer.valueOf(lines.get(0).get(0)),"Start",start, end,Double.valueOf(lines.get(0).get(4)),Double.valueOf(lines.get(0).get(1)), Double.valueOf(lines.get(0).get(2)), Double.valueOf(lines.get(0).get(3))));

        // Add the rest based on file
        for (int i = 1; i < n + 1; i++) {
            pois.add(new POI(Integer.valueOf(lines.get(i).get(0)),"Loc"+lines.get(i).get(0),Double.valueOf(lines.get(i).get(8)), Double.valueOf(lines.get(i).get(9)),Double.valueOf(lines.get(i).get(4)),Double.valueOf(lines.get(i).get(1)), Double.valueOf(lines.get(i).get(2)), Double.valueOf(lines.get(i).get(3))));
        }

        // Add the last POI which is the start POI for us
        pois.add(new POI(n+1,"End",start, end,Double.valueOf(lines.get(0).get(4)),Double.valueOf(lines.get(0).get(1)), Double.valueOf(lines.get(0).get(2)), Double.valueOf(lines.get(0).get(3))));

        // Return the POIs
        return pois;
    }



    public static void createJson(ArrayList<POI> tour)
    {
        try (Writer writer = new FileWriter("tour.json"))
        {
            Gson gson = new GsonBuilder().create();
            gson.toJson(tour, writer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void test(String fileName,double budget,int m)
    {
        ArrayList<POI> allTheLocations = initPois(Utilities.readFile(fileName),100,0, budget);
        ArrayList<ArrayList<POI>> tours;
        ILS ils=new ILS();
        CSCRatio cscRatio=new CSCRatio();
        CSCRoutes cscRoutes=new CSCRoutes();
        GRASP grasp = new GRASP(allTheLocations);
        Heuristics heuristics=new Heuristics();


        Insertion insertion = new Insertion();
        long startTime,endTime;
        NumberFormat formatter = new DecimalFormat("#0.00000");

        /*

        // Start the GRASP Solution
        ArrayList<POI> tour;
        tour = grasp.getGRASPSolution(0.01, 1);
        System.out.println(tour);


        */

        System.out.println("----- Commencing ILS example of "+fileName +" -----");
        startTime = System.currentTimeMillis();
        tours = ils.getBestSolution(allTheLocations,0, budget, m);
        endTime   = System.currentTimeMillis();
        System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds");
        System.out.println("Score of best found: "+insertion.calcTourScore(tours));
        System.out.println("Number of visits of best found: "+insertion.calcNumberOfVisits(tours));
        writeTour(tours,fileName,"ils");
        System.out.println("----- Finishing ILS example of "+fileName +" -----");







        /*
        System.out.println("----- Commencing CSCRatio example of "+fileName +" -----");
        startTime = System.currentTimeMillis();
        tours = cscRatio.getBestSolution(allTheLocations,0, budget, m);
        endTime   = System.currentTimeMillis();
        System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds");
        System.out.println("Ratio of best found: "+insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"cscratio");
        System.out.println("----- Finishing CSCRatio example of "+fileName +" -----");
        */


        /*

        System.out.println("----- Commencing CSCRoutes example of "+fileName +" -----");
        startTime = System.currentTimeMillis();
        tours = cscRoutes.getBestSolution(allTheLocations,0, budget, m);
        endTime   = System.currentTimeMillis();
        System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds");
        System.out.println("Ratio of best found: "+insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"cscroutes");
        System.out.println("----- Finishing CSCRoutes example of "+fileName +" -----");




        System.out.println("----- Commencing heuristicOne example of "+fileName +" -----");

        tours = heuristics.heuristicOne(allTheLocations,m);
        System.out.println("Ratio of best found: " + insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"heuristicOne");
        System.out.println("----- Finishing heuristicOne example of "+fileName +" -----");





        System.out.println("----- Commencing heuristicTwo example of "+fileName +" -----");

        tours = heuristics.heuristicTwo(allTheLocations,m);
        System.out.println("Ratio of best found: " + insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"heuristicTwo");
        System.out.println("----- Finishing heuristicTwo example of "+fileName +" -----");




        System.out.println("----- Commencing heuristicThree example of "+fileName +" -----");

        tours = heuristics.heuristicThree(allTheLocations, m);
        System.out.println("Ratio of best found: " + insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"heuristicThree");
        System.out.println("----- Finishing heuristicThree example of "+fileName +" -----");



        System.out.println("----- Commencing sweepOne example of "+fileName +" -----");

        tours = heuristics.sweepOne(allTheLocations, m);
        System.out.println("Ratio of best found: " + insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"sweepOne");
        System.out.println("----- Finishing sweepOne example of "+fileName +" -----");

        */


        /*

        System.out.println("----- Commencing sweepTwo example of "+fileName +" -----");

        tours = heuristics.sweepTwo(allTheLocations, m);
        System.out.println("Ratio of best found: " + insertion.calcTourRatio(tours));
        writeTour(tours,fileName,"sweepTwo");
        System.out.println("----- Finishing sweepTwo example of "+ fileName +" -----");



         */
    }

    static void combinations(Integer[] arr, int len, int startPosition, Integer[] result)
    {

        ArrayList res=new ArrayList<>();
        if (len == 0)
        {
            //System.out.println(Arrays.toString(result));
            combinationClustersID.add(result.clone());
            return;
        }
        for (int i = startPosition; i <= arr.length-len; i++)
        {
            result[result.length - len] = arr[i];
            combinations(arr, len-1, i+1, result);
        }

    }


}
