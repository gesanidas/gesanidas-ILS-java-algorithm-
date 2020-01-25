package com.gesanidas;



public class POI implements Comparable
{
    int id;
    String name;
    double opening;
    double closing;
    double score;
    double wait;
    double maxShift;
    double shift;
    double ratio;
    double arrival;
    double start;
    double leave;
    double x;
    double y;
    double stayDuration;
    int mCluster;




    public POI(int id, String name, double opening, double closing, double score, double x, double y, double stayDuration)
    {
        this.id = id;
        this.name = name;
        this.opening = opening;
        this.closing = closing;
        this.score = score;
        this.x = x;
        this.y = y;
        this.stayDuration = stayDuration;
    }

    public POI(POI poi) {
        this.id = poi.id;
        this.name = poi.name;
        this.opening = poi.opening;
        this.closing = poi.closing;
        this.score = poi.score;
        this.wait = poi.wait;
        this.maxShift = poi.maxShift;
        this.shift = poi.shift;
        this.ratio = poi.ratio;
        this.arrival = poi.arrival;
        this.start = poi.start;
        this.leave = poi.leave;
        this.x = poi.x;
        this.y = poi.y;
        this.stayDuration = poi.stayDuration;
        this.mCluster = poi.mCluster;
    }

    public POI(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return "ID: "+String.valueOf(id)+" Name: "+name+" Opening: "+String.valueOf(opening)+" Closing: "+String.valueOf(closing)+" Score: "+String.valueOf(score)+
                " Wait: "+String.valueOf(wait)+" Max Shift: "+String.valueOf(maxShift)+" Shift: "+String.valueOf(shift)+" Ratio: "+String.valueOf(ratio)+
                " Arrival: "+String.valueOf(arrival)+" Start: "+String.valueOf(start)+ " Leave: "+String.valueOf(leave)+" X: "+String.valueOf(x)+" Y: "+ String.valueOf(y)+" Stay Duration: "+String.valueOf(stayDuration) + "Cluster :" + String.valueOf(mCluster);
    }


    public double calcDistance(POI other)   //This function returns the distance to another POI
    {

        double deltaX = x - other.x;
        double deltaY = y - other.y;
        double result = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return result;
    }

    public double calcTime(POI other) // This function return the time needed to travel to another point, just a silly calculation
    {
        return calcDistance(other);
        //return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        POI other = (POI) obj;
        if (id != other.getId())
            return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOpening() {
        return opening;
    }

    public void setOpening(double opening) {
        this.opening = opening;
    }

    public double getClosing() {
        return closing;
    }

    public void setClosing(double closing) {
        this.closing = closing;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getWait() {
        return wait;
    }

    public void setWait(double wait) {
        this.wait = wait;
    }

    public double getMaxShift() {
        return maxShift;
    }

    public void setMaxShift(double maxShift) {
        this.maxShift = maxShift;
    }

    public double getShift() {
        return shift;
    }

    public void setShift(double shift) {
        this.shift = shift;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getArrival() {
        return arrival;
    }

    public void setArrival(double arrival) {
        this.arrival = arrival;
    }

    public double getLeave() {
        return leave;
    }

    public void setLeave(double leave) {
        this.leave = leave;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(double stayDuration) {
        this.stayDuration = stayDuration;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public int getmCluster() {
        return mCluster;
    }

    public void setmCluster(int mCluster) {
        this.mCluster = mCluster;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
