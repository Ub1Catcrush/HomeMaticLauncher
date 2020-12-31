package com.tvcs.homematic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.grid.StaggeredGridView;
import com.homematic.Room;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    private static final boolean LOAD_PUBLIC_TRANSPORT = false;

    public static final String UPDATED_DATA = "com.tvcs.homematic.updated_data";
    public static final String UPDATE_TIME = "com.tvcs.homematic.update_time";
    public static final String RELOAD_DATA = "com.tvcs.homematic.reload_data";
    public static final String RELOAD_PUBTRANS = "com.tvcs.homematic.reload_public_transport";

    private View activity;
    private View decorView;

    private RoomAdapter mAdapter;
    private ArrayList<Room> mRooms = new ArrayList();
    private boolean mPaused = false;
    private long mDisplayOn = 0;
    private Timer mReloadDataTimer = new Timer();
    private Timer mReloadPTTimer = new Timer();
    private Timer mReloadWeatherTimer = new Timer();
    private Timer mUpdateTimeTimer = new Timer();
    private static SharedPreferences sharedPreferences;
    private static long lastReload = 0;
    private static int oldSyncInterval = -1;
    private static final SimpleDateFormat df = new SimpleDateFormat("EEEE dd.MM.yyyy HH:mm:ss  ",
            Locale.GERMANY );

    private final Handler loadCCUDataHandler = new Handler();
    private MainActivity.LoadCCUDataRunner loadCCUDataRunner = null;
    private final Handler loadPubTransDataHandler = new Handler();
    private MainActivity.LoadPublicTransportDataRunner loadPubTransDataRunner = null;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean showNotification = sharedPreferences.getBoolean("notifications_show_reload_popups", true);
            if(intent.getAction().equals(UPDATED_DATA))
            {
                mRooms.clear();
                if(HomeMatic.myRoomList != null && HomeMatic.myRoomList.rooms != null)
                    mRooms.addAll(HomeMatic.myRoomList.rooms);

                mAdapter.notifyDataSetChanged();

                if(showNotification)
                    showMessage("Updating UI", Toast.LENGTH_SHORT);

                StaggeredGridView gv = findViewById(R.id.grid_view);
                gv.invalidate();
            }
            else if(intent.getAction().equals(RELOAD_DATA))
            {
                lastReload = System.currentTimeMillis();
                loadCCUDataHandler.post(loadCCUDataRunner);

                if(showNotification)
                    showMessage("Reloading data", Toast.LENGTH_SHORT);

                StaggeredGridView gv = findViewById(R.id.grid_view);
                gv.invalidate();
            }
            else if(intent.getAction().equals(RELOAD_PUBTRANS))
            {
                if(LOAD_PUBLIC_TRANSPORT)
                    loadPubTransDataHandler.post(loadPubTransDataRunner);

                StaggeredGridView gv = findViewById(R.id.grid_view);
                gv.invalidate();
            }
            else if(intent.getAction().equals(UPDATE_TIME))
            {
                Date date = Calendar.getInstance().getTime();
                String text = df.format(date);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(text);

                StaggeredGridView gv = findViewById(R.id.grid_view);
                gv.invalidate();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mMessageReceiver);

        mReloadDataTimer.cancel();
        mReloadDataTimer.purge();

        mReloadPTTimer.cancel();
        mReloadPTTimer.purge();

        mUpdateTimeTimer.cancel();
        mUpdateTimeTimer.purge();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(UPDATED_DATA);
        intentfilter.addAction(UPDATE_TIME);
        intentfilter.addAction(RELOAD_DATA);
        intentfilter.addAction(RELOAD_PUBTRANS);
        this.registerReceiver(mMessageReceiver, intentfilter);

        int syncInterval = Integer.parseInt(sharedPreferences.getString("sync_frequency", "30"));
        if(syncInterval >= 0)
        {
            scheduleReloadData();
        }
        scheduleUpdateTime();
        scheduleReloadPT();
    }

/*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
        */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadCCUDataRunner = new MainActivity.LoadCCUDataRunner(MainActivity.this);
        if(LOAD_PUBLIC_TRANSPORT)
            loadPubTransDataRunner = new MainActivity.LoadPublicTransportDataRunner(MainActivity.this);

        //onWindowFocusChanged(true);

        if(sharedPreferences == null)
        {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }

        SharedPreferences.OnSharedPreferenceChangeListener changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("sync_frequency"))
                {
                    int syncInterval = Integer.parseInt(sharedPreferences.getString("sync_frequency", "30"));
                    if(syncInterval != oldSyncInterval)
                    {
                        scheduleReloadData();
                        oldSyncInterval = syncInterval;
                    }
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(changeListener);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("HomeMatic");
        //getActionBar().setIcon(R.drawable.);
        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Transparent status bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Transparent navigation bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_main);
        activity = findViewById(R.id.activity_main);
        decorView = getWindow().getDecorView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleMargin(0, 0, 0, 0);
        toolbar.setContentInsetsAbsolute(6, 6);
        setSupportActionBar(toolbar);

        //GridView
        // Get the widgets reference from XML layout
        StaggeredGridView gv = findViewById(R.id.grid_view);
        // Data bind GridView with ArrayAdapter (String Array elements)
        mAdapter = new RoomAdapter(this, mRooms);
        gv.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Will launch doorbell video view", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        int syncInterval = Integer.parseInt(sharedPreferences.getString("sync_frequency", "30"));
        if(syncInterval != oldSyncInterval)
        {
            scheduleReloadData();
            oldSyncInterval = syncInterval;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Drawable drawable = menu.findItem(R.id.action_settings).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.tvcs.homematic.SettingsActivity");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showMessage(final String message, final int displayDuration) {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, message, displayDuration).show();
            }
        });
    }

    public void scheduleReloadData() {
        final int syncInterval = Integer.parseInt(sharedPreferences.getString("sync_frequency", "30"));

        showMessage("New intervall " + syncInterval, Toast.LENGTH_SHORT);

        if(mReloadDataTimer != null)
        {
            mReloadDataTimer.cancel();
            mReloadDataTimer.purge();
        }

        loadCCUDataHandler.removeCallbacks(loadCCUDataRunner);
        mReloadDataTimer = new Timer();

        mReloadDataTimer.schedule(new TimerTask() {
            public void run() {
                //lastReload = System.currentTimeMillis();
                if(!mPaused)
                {
                    sendBroadcast(new Intent(RELOAD_DATA));
                    /*
                    loadCCUDataHandler.post(loadCCUDataRunner);

                    boolean showNotification = sharedPreferences.getBoolean("notifications_show_reload_popups", true);
                    if(showNotification)
                        showMessage("Reloading data", Toast.LENGTH_SHORT);
                        */
                }
            }
        }, 1000, syncInterval*1000);
/*
        runOnUiThread(new Runnable() {
            public void run()
            {
            }
        });
*/
    }

    public void scheduleReloadPT() {
        if(mReloadPTTimer != null)
        {
            mReloadPTTimer.cancel();
            mReloadPTTimer.purge();
        }

        if(LOAD_PUBLIC_TRANSPORT)
            loadPubTransDataHandler.removeCallbacks(loadPubTransDataRunner);

        mReloadPTTimer = new Timer();

        mReloadPTTimer.schedule(new TimerTask() {
            public void run() {
                if(!mPaused)
                {
                    sendBroadcast(new Intent(RELOAD_PUBTRANS));
                }
            }
        }, 1000, 120000);
    }

    public void scheduleUpdateTime() {
        if(mUpdateTimeTimer != null)
        {
            mUpdateTimeTimer.cancel();
            mUpdateTimeTimer.purge();
        }

        mUpdateTimeTimer = new Timer();
        mUpdateTimeTimer.schedule(new TimerTask() {
            public void run() {
                if(!mPaused) {
                    sendBroadcast(new Intent(UPDATE_TIME));
                }
/*
                final int syncInterval = Integer.parseInt(sharedPreferences.getString("sync_frequency", "30"));

                runOnUiThread(new Runnable() {
                    public void run() {
                        if(!mPaused) {
                            Date date = Calendar.getInstance().getTime();
                            String text = df.format(date);
                            Toolbar toolbar = findViewById(R.id.toolbar);
                            toolbar.setTitle(text);

                            StaggeredGridView gv = findViewById(R.id.grid_view);
                            gv.invalidate();

                            if(lastReload < (System.currentTimeMillis() - (syncInterval * 1000 * 2)))
                            {
                                scheduleReloadData();
                            }

                            boolean isOn = isDisplayOn();
                            if(isOn && mDisplayOn == 0)
                            {
                                mDisplayOn = System.currentTimeMillis();
                            }
                            else if (isOn)
                            {
                                boolean disableDisplay = sharedPreferences.getBoolean("disable_display_switch", true);
                                if(disableDisplay)
                                {
                                    int period = Integer.parseInt(sharedPreferences.getString("disable_display_period", "120"));
                                    if(mDisplayOn < (System.currentTimeMillis() - (period*1000)))
                                    {
                                        turnScreenOff();
                                    }
                                }
                            }
                            else if(mDisplayOn != 0 && !isOn)
                            {
                                mDisplayOn = 0;
                            }
                        }
                    }
                });
                    */
            }
        }, 0, 1000);
    }

    boolean isDisplayOn()
    {
        final Context context = getApplicationContext();
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }

    void turnScreenOff() {
        /*
        showMessage("Screen off", Toast.LENGTH_LONG);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getLocalClassName());
        wl.acquire();
        wl.release();
        */
/*
        final Context context = getApplicationContext();
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, DeviceAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            showMessage("Not an admin", Toast.LENGTH_LONG);
            Intent intent = new Intent(DeviceAdminReceiver.ACTION_DEVICE_ADMIN_ENABLED);
            context.startActivity(intent);
        }
        */
    }

    private class LoadCCUDataRunner implements Runnable {
        public MainActivity mActivity;

        public LoadCCUDataRunner(MainActivity activity) {
            mActivity = activity;
        }

        public void run() {
            try {
                LoadCCUData performBackgroundTask = new LoadCCUData();
                // PerformBackgroundTask this class is the class that extends AsyncTask
                performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mActivity).get();
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage(), Toast.LENGTH_LONG);
            }
        }
    }

    private class LoadPublicTransportDataRunner implements Runnable {
        public MainActivity mActivity;

        public LoadPublicTransportDataRunner(MainActivity activity) {
            mActivity = activity;
        }

        public void run() {
            try {
                LoadPublicTransportData performBackgroundTask = new LoadPublicTransportData();
                // PerformBackgroundTask this class is the class that extends AsyncTask
                performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mActivity).get();
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage(), Toast.LENGTH_LONG);
            }
        }
    }
}
