package com.tvcs.mainzermobilitaet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tvcs.homematic.MainActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainzerMobilitaetAPI {
    public static boolean myLoaded = false;

    public static ArrayList<PublicTransportLiveData> dataList = new ArrayList<PublicTransportLiveData>();
    public static ArrayList<PublicTransportLiveMessages> messageList = new ArrayList<PublicTransportLiveMessages>();

    public static HashMap<String, AsyncTask<Object, Integer, ArrayList>> myTasks = new HashMap();
    public static Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<PublicTransportLiveData> livedatalist = null;
            ArrayList<PublicTransportLiveMessages> livemessagelist = null;

            int finished = 0;
            Activity act = (Activity) msg.obj;
            switch (msg.what) {
                case 0:
                    for (Map.Entry<String, AsyncTask<Object, Integer, ArrayList>> task : myTasks.entrySet())
                    {
                        if(task.getKey() == "data" && task.getValue().getStatus() == AsyncTask.Status.FINISHED)
                        {
                            try {
                                livedatalist = task.getValue().get();
                                finished += 1;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(task.getKey() == "messages" && task.getValue().getStatus() == AsyncTask.Status.FINISHED)
                        {
                            try {
                                livemessagelist = task.getValue().get();
                                finished += 1;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

            if(finished == 2)
            {
                dataList = livedatalist;
                messageList = livemessagelist;
                myTasks.clear();

                myLoaded = true;
                Intent intent = new Intent(MainActivity.UPDATED_DATA);
                // send broadcast
                if(act != null)
                    act.sendBroadcast(intent);

            }
        }
    };

    public static void main(String[] args)
    {
        try
        {
            ArrayList<PublicTransportLiveData> list1 = getLiveData();
            ArrayList<PublicTransportLiveMessages> list2 = getLiveMessages();

            System.out.println(list1);
            System.out.println(list2);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void LoadData(Activity activity) throws Throwable {
        LoadData performBackgroundTask = new LoadData();
        // PerformBackgroundTask this class is the class that extends AsyncTask
        myTasks.put("data", performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "data", activity));
        performBackgroundTask = new LoadData();
        myTasks.put("messages", performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "messages", activity));
        performBackgroundTask = new LoadData();
    }

    public static ArrayList<PublicTransportLiveData> getLiveData() throws Throwable
    {
        ArrayList<PublicTransportLiveData> list = new ArrayList<PublicTransportLiveData>();

        String livedata = "http://ura.itcs.mvg-mainz.de/interfaces/ura/instant_V1?StopAlso=false&ReturnList=visitnumber,lineid,linename,directionid,destinationtext,destinationname,vehicleid,tripid,estimatedtime,expiretime&stopId=404";
        try
        {
            String ld = "["+getJSON(livedata)+"]";
            ld = ld.replaceAll("\r\n", "");
            ld = ld.replaceAll("\\]\\[", "\\],\\[");

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode actualLD = (ArrayNode) mapper.readTree(ld);
            for (int i=1; i<actualLD.size(); i+=2)
            {
                try
                {
                    ArrayNode dataset = (ArrayNode) actualLD.get(i);
                    if(dataset.size() > 10)
                    {
                        PublicTransportLiveData oneLiveDataset = new PublicTransportLiveData(dataset.get(1).asInt(),
                                dataset.get(2).asText(),
                                dataset.get(3).asText(),
                                dataset.get(4).asInt(),
                                dataset.get(5).asText(),
                                dataset.get(6).asText(),
                                dataset.get(7).asText(),
                                dataset.get(8).asLong(),
                                dataset.get(9).asLong(),
                                dataset.get(10).asLong());
                        list.add(oneLiveDataset);
                    }
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<PublicTransportLiveMessages> getLiveMessages() throws Throwable
    {
        String livemessages = "http://ura.itcs.mvg-mainz.de/interfaces/ura/instant_V1?ReturnList=messageuuid,messagepriority,messagetext,starttime,expiretime&StopAlso=false&stopId=404";

        ArrayList<PublicTransportLiveMessages> list = new ArrayList<PublicTransportLiveMessages>();
        try
        {
            String ld = "["+getJSON(livemessages)+"]";
            ld = ld.replaceAll("\r\n", "");
            ld = ld.replaceAll("\\]\\[", "\\],\\[");

//            [2,"212_Bitte beachten Sie noch bis zum 05.Januar den reduzierten Ferienfahrplan. Die Fahrpläne finden Sie unter: www.mainzer-mobilitaet.de/aktuell/aenderungen/ oder als Link auf der Startseite.",1,"Bitte beachten Sie noch bis zum 05.Januar den reduzierten Ferienfahrplan. Die Fahrpläne finden Sie unter: www.mainzer-mobilitaet.de/aktuell/aenderungen/ oder als Link auf der Startseite.",0,1514917517830]

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode actualLD = (ArrayNode) mapper.readTree(ld);
            for (int i=1; i<actualLD.size(); i+=2)
            {
                try
                {
                    ArrayNode dataset = (ArrayNode) actualLD.get(i);
                    if(dataset.size() > 5)
                    {
                        PublicTransportLiveMessages oneLiveDataset = new PublicTransportLiveMessages(dataset.get(1).asText(),
                                dataset.get(2).asInt(),
                                dataset.get(3).asText(),
                                dataset.get(4).asLong(),
                                dataset.get(5).asLong());
                        list.add(oneLiveDataset);
                    }
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return list;
    }

    private static String getJSON(String url) throws Throwable
    {
        StringBuilder strB = new StringBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
//            con.getOutputStream().write(("name=" + url).getBytes());

            BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));

            String str;
            while (null != (str = input.readLine()))
            {
                strB.append(str).append("\r\n");
            }
            input.close();

            con.disconnect();
        } catch (Throwable t)
        {
            t.printStackTrace();
            throw t;
        }

        return strB.toString();
    }

    public static class LoadData extends AsyncTask<Object, Integer, ArrayList> {
        private Activity mAct;

        @Override
        protected ArrayList doInBackground(Object... params)
        {
            try
            {
                myLoaded = false;
                mAct = (Activity) params[1];
                if(((String) params[0]) == "data")
                    return getLiveData();
                else if(((String) params[0]) == "messages")
                    return getLiveMessages();
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList a) {
            super.onPostExecute(a);

            Message msg = new Message();
            msg.what = 0;
            msg.obj = mAct;
            myHandler.sendMessage(msg);
        }
    }
}