package com.tvcs.mainzermobilitaet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PublicTransportLiveData
{
    public static final SimpleDateFormat df = new SimpleDateFormat("HH:mm");

    private int visitnumber;
    private String lineid;
    private String linename;
    private int directionid;
    private String destinationtext;
    private String destinationname;
    private String vehicleid;
    private long tripid;
    private long estimatedtime;
    private long expiretime;

    public PublicTransportLiveData(int visitnumber, String lineid, String linename, int directionid, String destinationtext, String destinationname, String vehicleid, long tripid, long estimatedtime, long expiretime) {
        this.visitnumber = visitnumber;
        this.lineid = lineid;
        this.linename = linename;
        this.directionid = directionid;
        this.destinationtext = destinationtext;
        this.destinationname = destinationname;
        this.vehicleid = vehicleid;
        this.tripid = tripid;
        this.estimatedtime = estimatedtime;
        this.expiretime = expiretime;
    }

    public int getVisitnumber() {
        return visitnumber;
    }

    public String getLineid() {
        return lineid;
    }

    public String getLinename() {
        return linename;
    }

    public int getDirectionid() {
        return directionid;
    }

    public String getDestinationtext() {
        return destinationtext;
    }

    public String getDestinationname() {
        return destinationname;
    }

    public String getVehicleid() {
        return vehicleid;
    }

    public long getTripid() {
        return tripid;
    }

    public long getEstimatedtime() {
        return estimatedtime;
    }

    public long getEstimatedTimeInMinutes() {
        return ((estimatedtime-System.currentTimeMillis())/60000);
    }

    public long getExpiretime() {
        return expiretime;
    }

    public String getExpiretimeAsString() {
        Date date = new Date(estimatedtime);
        return df.format(date);
    }

    @Override
    public String toString() {

        return linename + " nach " + destinationtext +
                (directionid==1?": Abfahrt in ":": Ankunft in ") + getEstimatedTimeInMinutes() + " Minuten ("+getExpiretimeAsString()+")\n";
    }
}
