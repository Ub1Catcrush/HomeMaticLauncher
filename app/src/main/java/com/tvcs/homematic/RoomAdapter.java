package com.tvcs.homematic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.homematic.Channel;
import com.homematic.Datapoint;
import com.homematic.Room;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RoomAdapter extends ArrayAdapter<Room> {
    private final Context context;
    private final List<Room> rooms;

    private static int colCount = 6;
    private HashMap<Integer, Integer> mViews;
    private static LayoutInflater inflater;

    public RoomAdapter(Context context, List<Room> rooms) {
        super(context, -1, rooms);
        this.context = context;
        this.rooms = rooms;

        this.mViews = new HashMap();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        System.out.println("position "+position);
        //long start = System.currentTimeMillis();
        final View rowView = inflater.inflate(R.layout.room_item, parent, false);
        final TableLayout tableLayout = (TableLayout) rowView.findViewById(R.id.item_content);

        final TextView titleTextView = (TextView) rowView.findViewById(R.id.item_title_text);
        //titleTextView.setTypeface(Typeface.createFromAsset(context.getResources().getAssets(), "DejaVuSans.ttf"));
        titleTextView.setText(rooms.get(position).name);

        mViews.put(rooms.get(position).ise_id, R.id.item_layout);

        AddRoomView(tableLayout, rooms.get(position));

        //System.out.println(System.currentTimeMillis() + " position "+position + " " +(System.currentTimeMillis() - start) + "ms");

        return rowView;
    }

    private void AddRoomView(final TableLayout table, final Room aRoom) {
 //       System.out.println("ADD ROOM "+aRoom.name);

        LinkedList<ImageView> availViews = new LinkedList();
        if (aRoom.channels != null && !aRoom.channels.isEmpty()) {
            List<String> statedevices = Arrays.asList(HomeMatic.STATE_DEVICES);
            boolean hasWindows = false;
            for (Channel channel : aRoom.channels) {
                Channel chan = HomeMatic.myChannels.get(channel.ise_id);

                for (Datapoint data : chan.datapoints) {
                    switch (data.type) {
                        case Datapoint.TYPE_STATE:
                            if (HomeMatic.myDevices.containsKey(HomeMatic.myChannel2Device.get(chan.ise_id)) &&
                                    !statedevices.contains(HomeMatic.myDevices.get((HomeMatic.myChannel2Device.get(chan.ise_id))).device_type)) {
                                continue;
                            }

                            hasWindows = true;
                            break;
                    }
                }
            }

            TableRow row = new TableRow(context);
            row.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (!aRoom.name.equals("Aussen") && hasWindows) {
                TextView tv = new TextView(context);
                tv.setId(View.generateViewId());
                tv.setText("Fenster");
                //tv.setMinWidth(100);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
                tv.setTextColor(Color.WHITE);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                row.addView(tv);

                ImageView iv = new ImageView(context);
                iv.setId(View.generateViewId());
                //iv.setBackground(new ColorDrawable(0xFFFF0000));
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMaxHeight(10);
                iv.setMaxWidth(10);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(10);

                availViews.add(iv);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMaxHeight(10);
                iv.setMaxWidth(10);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(10);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                //iv.setBackground(new ColorDrawable(0xFFFF0000));
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMaxHeight(10);
                iv.setMaxWidth(10);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(10);

                availViews.add(iv);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMaxHeight(10);
                iv.setMaxWidth(10);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(10);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                //iv.setBackground(new ColorDrawable(0xFFFF0000));
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMaxHeight(10);
                iv.setMaxWidth(10);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(10);

                availViews.add(iv);

                row.addView(iv);

                table.addView(row);
            } else {
                ImageView iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMinimumHeight(1);
                iv.setMinimumWidth(100);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMinimumHeight(1);
                iv.setMinimumWidth(10);

                availViews.add(iv);

                row.addView(iv);

                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMinimumHeight(1);
                iv.setMinimumWidth(10);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMinimumHeight(1);
                iv.setMinimumWidth(10);

                availViews.add(iv);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMinimumHeight(1);
                iv.setMinimumWidth(10);

                row.addView(iv);

                iv = new ImageView(context);
                iv.setId(View.generateViewId());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setMinimumHeight(1);
                iv.setMinimumWidth(10);

                availViews.add(iv);

                row.addView(iv);

                table.addView(row);
            }

            double temperature = 0.0f;
            double relhum = 0.0f;

            boolean temp = false;
            boolean temp2 = false;
            boolean lowBat = false;
            for (Channel channel : aRoom.channels) {
                Channel chan = HomeMatic.myChannels.get(channel.ise_id);

                for (Datapoint data : chan.datapoints) {
                    row = null;
                    switch (data.type) {
                        case Datapoint.TYPE_LOWBAT:
                            lowBat = (data.value.equalsIgnoreCase("true"));
                            break;
                        case Datapoint.TYPE_SET_TEMPERATURE:
                            if (!temp) {
                                row = CreateRow(channel.ise_id, "Soll Temperatur", String.format("%.1f", Float.parseFloat(data.value)) + data.valueunit);
//                            System.out.println(dev.name + " TEMP " + data.value + data.valueunit);
                            }
                            temp = true;
                            break;
                        case Datapoint.TYPE_TEMPERATURE:
                        case Datapoint.TYPE_ACTUAL_TEMPERATURE:
                            if (!temp2) {
                                row = CreateRow(channel.ise_id, "Akt. Temperatur", String.format("%.1f", Float.parseFloat(data.value)) + data.valueunit);
                                temperature = Float.parseFloat(data.value);
//                              System.out.println(dev.name + " TEMP " + data.value + data.valueunit);
                            }
                            temp2 = true;
                            break;
                        case Datapoint.TYPE_HUMIDITY:
                            row = CreateRow(channel.ise_id, "Feuchtigkeit", data.value + data.valueunit);
                            relhum = Float.parseFloat(data.value);
//                          System.out.println(dev.name + " HUMIDITY " + data.value + data.valueunit);
                            break;
                        case Datapoint.TYPE_STATE:
                            if (HomeMatic.myDevices.containsKey(HomeMatic.myChannel2Device.get(chan.ise_id)) && !statedevices.contains(HomeMatic.myDevices.get((HomeMatic.myChannel2Device.get(chan.ise_id))).device_type)) {
                                continue;
                            }

                            ImageView view = availViews.removeLast();

                            mViews.put(chan.ise_id, view.getId());

                            String state = "geschlossen";
                            switch (data.value) {
                                case "0":
                                case "false":
                                    view.setBackground(new ColorDrawable(0xFF00FF00));
                                    state = "geschlossen";
                                    break;
                                case "1":
                                    view.setBackground(new ColorDrawable(0xFFFFD700));
                                    state = "gekippt";
                                    break;
                                case "2":
                                case "true":
                                    view.setBackground(new ColorDrawable(0xFFFF0000));
                                    state = "offen";
                                    break;

                            }
                            //row = CreateRow(channel.ise_id, "Status", state);
//                        System.out.println(dev.name + " STATE " + data.value + data.valueunit);
                            break;
                    }

                    if (row != null) {
                        table.addView(row);
                    }
                }
            }

            if (!aRoom.name.equals("Aussen") && relhum > 0) {
                int warn = HomeMatic.GetWarning(relhum, temperature);
                UpdateWarning(table, aRoom.ise_id, warn >= 1 || lowBat);
//                innertr = CreateRow(aRoom.ise_id, "LÜFTEN", warn == 0 ? "UNNÖTIG" : (warn == 1 ? "BITTE" : "DRINGEND"));
//              innerTableLayout.addView(innertr);
            }
            else{
                UpdateWarning(table, aRoom.ise_id, lowBat);
            }
        }
    }

    private TableRow CreateRow(int ise_id, String name, String value)
    {
        TableRow innertr = new TableRow(context);
//        TableLayout.LayoutParams tlop= new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        innertr.setLayoutParams(tlop);
        innertr.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView tv = new TextView(context);
        tv.setId(View.generateViewId());
        tv.setText(name);
        //tv.setMinWidth(100);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
        tv.setTextColor(Color.WHITE);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

        innertr.addView(tv);

        tv = new TextView(context);
        tv.setId(View.generateViewId());
        tv.setText(value);
        //tv.setWidth(85);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
        tv.setTextColor(Color.WHITE);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

        mViews.put(ise_id, tv.getId());

        innertr.addView(tv);

        TableRow.LayoutParams params = (TableRow.LayoutParams)tv.getLayoutParams();
        params.span = colCount-1;
        tv.setLayoutParams(params);

        return innertr;
    }

    private void UpdateWarning(View itemLayoutView, int ise_id, boolean warning)
    {
        Integer viewId = mViews.get(ise_id);
        if(viewId == null) {
            return;
        }
        View parent = (View) itemLayoutView.getParent();
        View view = itemLayoutView.findViewById(viewId);;
        if (view == null && parent != null) {
            view = parent.findViewById(viewId);
        }

        if(view != null)
        {
            if (warning)
            {
                if(view.getAnimation() == null)
                {
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(50); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    view.startAnimation(anim);
                }
                //view.setTextColor(Color.RED);
            }
            else
            {
                if(view.getAnimation() != null)
                {
                    view.clearAnimation();
                }
                //view.setTextColor(Color.WHITE);
            }
        }
    }
}

