package com.tvcs.homematic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;

import com.homematic.Channel;
import com.homematic.Device;
import com.homematic.Devicelist;
import com.homematic.Notification;
import com.homematic.Room;
import com.homematic.Roomlist;
import com.homematic.Statelist;
import com.homematic.SystemNotification;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HomeMatic
{
	public static boolean myLoaded = false;

	public static Devicelist myDeviceList;
	public static Roomlist myRoomList;
	public static SystemNotification mySystemNotification;
	public static Statelist myStateList;

	public static HashMap<Integer, Device> myDevices = new HashMap();
	public static HashMap<String, Notification> myNotifications = new HashMap();
    public static HashMap<Integer, Channel> myChannels = new HashMap();
    public static HashMap<Integer, Integer> myChannel2Device = new HashMap();

    public static String[] STATE_DEVICES = {"HM-Sec-RHS", "HM-Sec-SCo", "HmIP-SRH"};

	private static SharedPreferences sharedPreferences;

	public static HashMap<String, AsyncTask<Object, Integer, String>> myTasks = new HashMap();
	public static Handler myHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			String devicelist = null;
			String roomlist = null;
			String systemnotification = null;
			String statelist = null;
			int finished = 0;
			Activity act = (Activity) msg.obj;
			switch (msg.what) {
				case 0:
					for (Map.Entry<String, AsyncTask<Object, Integer, String>> task : myTasks.entrySet())
					{
						if(task.getKey() == "devicelist.cgi" && task.getValue().getStatus() == AsyncTask.Status.FINISHED)
						{
							try {
								devicelist = task.getValue().get();
								finished += 1;
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							}
						}
						else if(task.getKey() == "roomlist.cgi" && task.getValue().getStatus() == AsyncTask.Status.FINISHED)
						{
							try {
								roomlist = task.getValue().get();
								finished += 1;
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							}
						}
						else if(task.getKey() == "statelist.cgi" && task.getValue().getStatus() == AsyncTask.Status.FINISHED)
						{
							try {
								statelist = task.getValue().get();
								finished += 1;
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							}
						}
						else if(task.getKey() == "systemNotification.cgi" && task.getValue().getStatus() == AsyncTask.Status.FINISHED)
						{
							try {
								systemnotification = task.getValue().get();
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

			if(finished == 4)
			{
				LoadData(act,  devicelist, roomlist, systemnotification, statelist);
				myTasks.clear();
			}
		}
	};

	public static void main(String[] args)
	{
		float temperature = 10.0f;
		float relhum = 40.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 10.0f;
		relhum = 60.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 10.0f;
		relhum = 80.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 20.0f;
		relhum = 40.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 20.0f;
		relhum = 60.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 20.0f;
		relhum = 70.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 20.0f;
		relhum = 80.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 22.0f;
		relhum = 40.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 22.0f;
		relhum = 60.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 22.0f;
		relhum = 65.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 22.0f;
		relhum = 70.0f;
		System.out.println(GetWarning(relhum, temperature));

		temperature = 22.0f;
		relhum = 80.0f;
		System.out.println(GetWarning(relhum, temperature));

		String devicelist = "";
		String roomlist = "";
		String systemnotification = "";
		String statelist = "";
		if (args.length > 0 && args[0].equals("f"))
		{
			try
			{
				devicelist = readFile(null, "devicelist.cgi.xml");
				roomlist = readFile(null, "roomlist.cgi.xml");
				systemnotification = readFile(null, "systemNotification.cgi.xml");
				statelist = readFile(null, "statelist.cgi.xml");
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				System.exit(1);
			}
		}
		else
		{
			try
			{
				devicelist = getCGI("devicelist.cgi");
				roomlist = getCGI("roomlist.cgi");
				systemnotification = getCGI("systemNotification.cgi");
				statelist = getCGI("statelist.cgi");
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				System.exit(1);
			}
		}

		LoadData(null, devicelist, roomlist, systemnotification, statelist);
	}

	public static void LoadData(Activity activity) throws Throwable
	{
		if(sharedPreferences == null)
		{
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		}

		boolean testMode = sharedPreferences.getBoolean("test_switch", false);

		String devicelist = null;
		String roomlist = null;
		String systemnotification = null;
		String statelist = null;
		if (testMode)
		{
			devicelist = readFile(activity, "devicelist.cgi.xml");
			roomlist = readFile(activity, "roomlist.cgi.xml");
			systemnotification = readFile(activity, "systemNotification.cgi.xml");
			statelist = readFile(activity, "statelist.cgi.xml");
			LoadData(activity, devicelist, roomlist, systemnotification, statelist);
		}
		else
		{
			LoadData performBackgroundTask = new LoadData();
			// PerformBackgroundTask this class is the class that extends AsyncTask
			myTasks.put("devicelist.cgi", performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "devicelist.cgi", activity));
			performBackgroundTask = new LoadData();
			myTasks.put("roomlist.cgi", performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "roomlist.cgi", activity));
			performBackgroundTask = new LoadData();
			myTasks.put("statelist.cgi", performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "statelist.cgi", activity));
			performBackgroundTask = new LoadData();
			myTasks.put("systemNotification.cgi", performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "systemNotification.cgi", activity));

/*
			devicelist = getCGI("devicelist.cgi");
			roomlist = getCGI("roomlist.cgi");
			systemnotification = getCGI("systemNotification.cgi");
			statelist = getCGI("statelist.cgi");
			LoadData( devicelist, roomlist, systemnotification, statelist);
*/
			//System.out.println(devicelist);
			//System.out.println(roomlist);
			//System.out.println(systemnotification);
			//System.out.println(statelist);
		}
	}

	public static void LoadData(Activity act, String devicelist, String roomlist, String systemnotification, String statelist)
	{
		if(devicelist == null || roomlist == null || statelist == null)
			return;
		if(devicelist.isEmpty() || roomlist.isEmpty() || statelist.isEmpty())
			return;

		myLoaded = false;

		myDeviceList = Deserialize(Devicelist.class, devicelist);
        myRoomList = Deserialize(Roomlist.class, roomlist);
        myStateList = Deserialize(Statelist.class, statelist);
        mySystemNotification = Deserialize(SystemNotification.class, systemnotification);

        List<String> statedevices = Arrays.asList(STATE_DEVICES);

        myDevices = new HashMap<Integer, Device>();
        if(myDeviceList != null)
        {
            for (Device dev : myDeviceList.devices) {
                if (!myDevices.containsKey(dev.ise_id)) {
                    myDevices.put(dev.ise_id, dev);
                }
            }
        }

        myChannels = new HashMap<Integer, Channel>();
        myChannel2Device = new HashMap<Integer, Integer>();
        if(myStateList != null)
        {
            for (Device dev : myStateList.devices)
            {
                boolean temp = false;
                for (Channel chan : dev.channels)
                {
                    if(!myChannels.containsKey(chan.ise_id))
                    {
                        myChannels.put(chan.ise_id, chan);
                        myChannel2Device.put(chan.ise_id, dev.ise_id);
                    }
                }
			}
		}

		myNotifications = new HashMap<String, Notification>();
		if(mySystemNotification != null)
		{
			for (Notification notify : mySystemNotification.notifications) {
				if (!myNotifications.containsKey(notify.name) && notify.type == "LOWBAT") {
					myNotifications.put(notify.name, notify);
				}
			}
		}

		Collections.sort(myRoomList.rooms, new Comparator<Room>() {
			@Override
			public int compare(Room room2, Room room1)
			{
				if(room2.name.equals("Aussen"))
				{
					return 1;
				}
				else if(room2.name.contains("UG") && room1.name.contains("UG"))
				{
					return room2.name.compareTo(room1.name);
				}
				else if(room2.name.contains("UG") && !(room1.name.contains("DG") || room1.name.contains("EG") || room1.name.contains("OG") || room1.name.contains("OG")))
				{
					return -1;
				}
				else if(room2.name.contains("UG"))
				{
					return -1;
				}
				else if(room2.name.contains("EG") && room1.name.contains("UG"))
				{
					return 1;
				}
				else if(room2.name.contains("EG") && !(room1.name.contains("DG") || room1.name.contains("EG") || room1.name.contains("OG") || room1.name.contains("OG")))
				{
					return -1;
				}
				else if(room2.name.contains("EG") && room1.name.contains("EG"))
				{
					return room2.name.compareTo(room1.name);
				}
				else if(room2.name.contains("EG"))
				{
					return -1;
				}
				else if(room2.name.contains("OG") && (room1.name.contains("UG") || room1.name.contains("EG")))
				{
					return 1;
				}
				else if(room2.name.contains("OG") && !(room1.name.contains("DG") || room1.name.contains("EG") || room1.name.contains("OG") || room1.name.contains("OG")))
				{
					return -1;
				}
				else if(room2.name.contains("OG") && room1.name.contains("OG"))
				{
					return room2.name.compareTo(room1.name);
				}
				else if(room2.name.contains("OG"))
				{
					return -1;
				}
				else if(room2.name.contains("DG") && (room1.name.contains("UG") || room1.name.contains("EG") || room1.name.contains("OG")))
				{
					return 1;
				}
				else if(room2.name.contains("DG") && !(room1.name.contains("DG") || room1.name.contains("EG") || room1.name.contains("OG") || room1.name.contains("OG")))
				{
					return -1;
				}
				else if(room2.name.contains("DG") && room1.name.contains("DG"))
				{
					return room2.name.compareTo(room1.name);
				}
				else
				{
					return 0;
				}
			}
		});

		myLoaded = true;
		Intent intent = new Intent(MainActivity.UPDATED_DATA);
		// send broadcast
		if(act != null)
			act.sendBroadcast(intent);

	}


	public static <T> T Deserialize(Class<T> type, String source)
	{
		Serializer serializer = new Persister();

		T example = null;
		try
		{
			example = serializer.read(type, source);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return example;
	}

	public static String getCGI(String cgi) throws Throwable
	{
		StringBuilder strB = new StringBuilder();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			String ccuServerName = sharedPreferences.getString("sync_ccu_name", "homematic-ccu2");
			//System.out.println("URL [" + "http://"+ccuServerName+"/addons/xmlapi/" + "] - Name [" + cgi + "]");

			HttpURLConnection con = (HttpURLConnection) (new URL("http://"+ccuServerName+"/addons/xmlapi/" + cgi)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			con.getOutputStream().write(("name=" + cgi).getBytes());

			BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream(), "ISO-8859-1"));

			String str;
			while (null != (str = input.readLine()))
			{
				strB.append(str).append("\r\n");
			}
			input.close();
/*
			InputStream is = con.getInputStream();
			byte[] b = new byte[1024];

			while (is.read(b) != -1)
			{
				baos.write(b);
			}
*/
			con.disconnect();
		} catch (Throwable t)
		{
			t.printStackTrace();
			throw t;
		}

		return strB.toString();//baos.toString();
	}

	public static String readFile(Context current, String filename)
	{
		StringBuffer content = new StringBuffer();
		InputStream in = null;
		BufferedReader reader = null;
		try
		{
			in = current.getAssets().open(filename);
			reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
			char[] chars = new char[1024];
			while(reader.read(chars) >= 0)
			{
				content.append(chars);
			}
			reader.close();
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (reader != null)
			{
				try
				{
					in.close();
					reader.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return content.toString();
	}

	public static int GetWarning(final double relativeHumidity, final double temperature)
	{
//        System.out.println("TEMP = " + temperature + " HUM = " + relativeHumidity);
		double    afw;
		double    afa;
		double    afs;
		double    afc;

		if (temperature < 10.0)
		{ afs = (3.78 + (0.285 * temperature) + (0.0052 * temperature * temperature) + (0.0005 * temperature * temperature * temperature));
		}
		else
		{ afs = (7.62 + (0.524 * (temperature-10.0)) + (0.0131 * (temperature-10.0) * (temperature-10.0)) + (0.00048 * (temperature-10.0) * (temperature-10.0) * (temperature-10.0)));
		}

		afc = (afs * relativeHumidity) / (100.0 + afs * (100.0 - relativeHumidity) / 622);

		final double relativeHumidity2 = 65;
		afw = (afs * relativeHumidity2) / (100.0 + afs * (100.0 - relativeHumidity2) / 622);

		final double relativeHumidity3 = 75;
		afa = (afs * relativeHumidity3) / (100.0 + afs * (100.0 - relativeHumidity3) / 622);

//        System.out.println("AFC = " + afc + " AFW = " + afw + " AFA = " + afa);

		if(afc < afw)
		{
			return 0;
		}
		else if(afc < afa)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}

	public static class LoadData extends AsyncTask<Object, Integer, String> {
		private Activity mAct;

		@Override
		protected String doInBackground(Object... params)
		{
			try
			{
				myLoaded = false;

				mAct = (Activity) params[1];
				return getCGI((String) params[0]);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			Message msg = new Message();
			msg.what = 0;
			msg.obj = mAct;
			myHandler.sendMessage(msg);
		}
	}

}
