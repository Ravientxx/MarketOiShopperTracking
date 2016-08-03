package dsv.shopertracking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SetTrackingActivity extends Activity {

    private static String url = "http://catalogue.marketoi.com/index.php/api/App/workflow";
    private static String url2 = "http://catalogue.marketoi.com/index.php/api/App/track";
    private String url3 = "http://catalogue.marketoi.com/index.php/api/App/available";
    private static OrderListChapter item;
    final public String STT_CREATE = "1";
    final public String STT_ACKNOWLEDGE = "2";
    final public String STT_RUN = "3";
    final public String STT_SHOPPED = "4";
    final public String STT_DELIVER = "9";
    TextView approveTime;
    TextView startTime;
    TextView stopTime;
    TextView deliverTime;
    GPSTracker gps;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tracking);
        System.out.println(CheckPointActivity.orderID);
        final Button startRun = (Button) findViewById(R.id.startRun);
        final Button backBtn = (Button) findViewById(R.id.backBtn);
        final Button deliverBtn = (Button) findViewById(R.id.delivered_btn);
        final Button approve = (Button) findViewById(R.id.acknowledge_btn);
        final Button shopped = (Button) findViewById(R.id.shopped_btn);
        approveTime = (TextView) findViewById(R.id.dateAck);
        startTime = (TextView) findViewById(R.id.dateStart);
        stopTime = (TextView) findViewById(R.id.dateShoped);
        deliverTime = (TextView) findViewById(R.id.dateDeliver);
        getInitTimer();
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeStart();
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeApprove();
            }
        });
        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeDelivered();
            }
        });
        shopped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFile(CheckPointActivity.orderID);
                changeShopped();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.routePath ="CheckPoint";
                finish();
            }
        });
        changeStartField();
    }
    private void changeStartField(){
        final View circle = (View) findViewById(R.id.circle);
        final TextView status = (TextView) findViewById(R.id.statusOrder);
        final Button run = (Button) findViewById(R.id.startRun);
        final Button deliver = (Button) findViewById(R.id.delivered_btn);
        final Button approve = (Button) findViewById(R.id.acknowledge_btn);
        final Button shopped = (Button) findViewById(R.id.shopped_btn);
        //change Status
        if (item.chapterStatus.equals("1")) {
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("not started yet");

            approve.setBackground(getResources().getDrawable(R.drawable.green_button));
            run.setBackground(getResources().getDrawable(R.drawable.gray_button));
            deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
            shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));

            approve.setEnabled(true);
            run.setEnabled(false);
            deliver.setEnabled(false);
            shopped.setEnabled(false);
        }
        else if (item.chapterStatus.equals("2")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("approved");

            run.setBackground(getResources().getDrawable(R.drawable.green_button));
            deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
            approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
            shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));

            approve.setEnabled(false);
            run.setEnabled(true);
            deliver.setEnabled(false);
            shopped.setEnabled(false);
        }
        else if (item.chapterStatus.equals("3")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_online));
            status.setText("started");

            run.setBackground(getResources().getDrawable(R.drawable.gray_button));
            deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
            approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
            shopped.setBackground(getResources().getDrawable(R.drawable.green_button));

            approve.setEnabled(false);
            run.setEnabled(false);
            deliver.setEnabled(false);
            shopped.setEnabled(true);
        }
        else if (item.chapterStatus.equals("4")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("shopped");

            run.setBackground(getResources().getDrawable(R.drawable.gray_button));
            deliver.setBackground(getResources().getDrawable(R.drawable.green_button));
            approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
            shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));

            approve.setEnabled(false);
            run.setEnabled(false);
            deliver.setEnabled(true);
            shopped.setEnabled(false);
        }
        else if (item.chapterStatus.equals("9")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("delivered");

            run.setBackground(getResources().getDrawable(R.drawable.gray_button));
            deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
            approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
            shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));

            approve.setEnabled(false);
            run.setEnabled(false);
            deliver.setEnabled(false);
            shopped.setEnabled(false);
        }





    }
    public static void setItem(OrderListChapter i){
        item = i;
    }

    private void processStart(JSONObject res){
        try {
            String stt = (String) res.get("status");
            if (stt.equals("true")){
                //get element
                Button run = (Button) findViewById(R.id.startRun);
                Button deliver = (Button) findViewById(R.id.delivered_btn);
                Button approve = (Button) findViewById(R.id.acknowledge_btn);
                Button shopped = (Button) findViewById(R.id.shopped_btn);
                View circle = (View) findViewById(R.id.circle);
                TextView status = (TextView) findViewById(R.id.statusOrder);
                //change color
                approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
                approve.setEnabled(false);

                run.setBackground(getResources().getDrawable(R.drawable.gray_button));
                run.setEnabled(false);

                deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
                deliver.setEnabled(false);

                shopped.setBackground(getResources().getDrawable(R.drawable.green_button));
                shopped.setEnabled(true);

                circle.setBackground(getResources().getDrawable(R.drawable.circle_online));

                status.setText("started");
                item.chapterStatus = STT_RUN;
                Toast.makeText(getApplicationContext(), "Start tracking", Toast.LENGTH_LONG).show();
                //Start GPS
                OrderListActivity.orderTracking = item.chapterId;
                MainActivity.handler.postDelayed(updateData, 10000);

                startTime.setText(getCurrentTime());
                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SetTrackingActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("started", getCurrentTime());
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void processDelivered(JSONObject res){
        try {
            String stt = (String) res.get("status");
            if (stt.equals("true")){
               //get element
                Button run = (Button) findViewById(R.id.startRun);
                Button deliver = (Button) findViewById(R.id.delivered_btn);
                Button approve = (Button) findViewById(R.id.acknowledge_btn);
                Button shopped =(Button) findViewById(R.id.shopped_btn);
                View circle = (View) findViewById(R.id.circle);
                TextView status = (TextView) findViewById(R.id.statusOrder);
                //change color
                approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
                approve.setEnabled(false);

                run.setBackground(getResources().getDrawable(R.drawable.gray_button));
                run.setEnabled(false);

                deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
                deliver.setEnabled(false);

                shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));
                shopped.setEnabled(false);

                circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
                status.setText("delivered");
                item.chapterStatus = STT_DELIVER;
                Toast.makeText(getApplicationContext(), "Delivered", Toast.LENGTH_LONG).show();

                //Stop GPS
                MainActivity.handler.removeCallbacksAndMessages(null);
                deliverTime.setText(getCurrentTime());


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void processApprove(JSONObject res){
        try {
            String stt = (String) res.get("status");
            if (stt.equals("true")){
                //get element
                Button run = (Button) findViewById(R.id.startRun);
                Button deliver = (Button) findViewById(R.id.delivered_btn);
                Button approve = (Button) findViewById(R.id.acknowledge_btn);
                Button shopped = (Button) findViewById(R.id.shopped_btn);
                approveTime.setText(getCurrentTime());

                View circle = (View) findViewById(R.id.circle);
                TextView status = (TextView) findViewById(R.id.statusOrder);
                //change color
                approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
                approve.setEnabled(false);

                run.setBackground(getResources().getDrawable(R.drawable.green_button));
                run.setEnabled(true);

                deliver.setBackground(getResources().getDrawable(R.drawable.gray_button));
                deliver.setEnabled(false);

                shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));
                shopped.setEnabled(false);

                circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
                status.setText("approved");
                item.chapterStatus = STT_ACKNOWLEDGE;
                Toast.makeText(getApplicationContext(), "Approved", Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void processShopped(JSONObject res){
        try {
            String stt = (String) res.get("status");
            if (stt.equals("true")){
                //get element
                Button run = (Button) findViewById(R.id.startRun);
                Button deliver = (Button) findViewById(R.id.delivered_btn);
                Button approve = (Button) findViewById(R.id.acknowledge_btn);
                Button shopped =(Button) findViewById(R.id.shopped_btn);
                View circle = (View) findViewById(R.id.circle);
                TextView status = (TextView) findViewById(R.id.statusOrder);
                //change color
                approve.setBackground(getResources().getDrawable(R.drawable.gray_button));
                approve.setEnabled(false);

                run.setBackground(getResources().getDrawable(R.drawable.gray_button));
                run.setEnabled(false);

                deliver.setBackground(getResources().getDrawable(R.drawable.green_button));
                deliver.setEnabled(true);

                shopped.setBackground(getResources().getDrawable(R.drawable.gray_button));
                shopped.setEnabled(false);

                circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
                status.setText("Shopped");
                item.chapterStatus=STT_SHOPPED;
                Toast.makeText(getApplicationContext(), "Shopped", Toast.LENGTH_LONG).show();
               // MainActivity.h
                stopTime.setText(getCurrentTime());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void stopGPS() {
        MainActivity.handler.removeCallbacksAndMessages(null);
    }

    private void processTime(JSONObject res){
            try {
                String stt = (String) res.get("status");
                if (stt.equals("true")){
                    //get element
                    JSONObject timer = (JSONObject)res.get("result");
                    if(!timer.getString("ts_start").equals("null")){startTime.setText(timer.getString("ts_start"));}
                    if(!timer.getString("ts_stop").equals("null")){deliverTime.setText(timer.getString("ts_stop"));}
                    if(!timer.getString("ts_shop1").equals("null")){stopTime.setText(timer.getString("ts_shop1"));}
                    if(!timer.getString("ts_ack").equals("null")){approveTime.setText(timer.getString("ts_ack"));}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    private void alertError(){

    }
    private void changeApprove() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                processApprove(o);
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                alertError();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        data.put("key",MainActivity.key);
        data.put("order_id", item.chapterId.replaceAll("#", ""));
        data.put("status",STT_ACKNOWLEDGE);

        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    }
    private void changeDelivered() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                processDelivered(o);
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                alertError();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        data.put("key",MainActivity.key);
        data.put("order_id", item.chapterId.replaceAll("#", ""));
        data.put("status",STT_DELIVER);

        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    }
    private void changeStart() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                processStart(o);
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                alertError();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        data.put("key",MainActivity.key);
        data.put("order_id", item.chapterId.replaceAll("#", ""));
        data.put("status",STT_RUN);
        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    }
    private void changeShopped(){
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                processShopped(o);
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                alertError();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        data.put("key",MainActivity.key);
        data.put("order_id", item.chapterId.replaceAll("#", ""));
       data.put("status", STT_SHOPPED);
      //  data.put("status", "3");
        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_tracking, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void requestTracking(){
        //call the service here
        // create class object
        gps = new GPSTracker(this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject o) {
                    // processData(o);
                    Toast.makeText(getApplicationContext(), "aaa"+ o.toString(), Toast.LENGTH_LONG).show();
                }
            };
            Response.ErrorListener error = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    // alertError();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            };
            Map<String,String> data = new HashMap<String,String>();
            data.put("key",MainActivity.key);
            data.put("order_id",OrderListActivity.orderTracking.replace("#",""));
            Location location = gps.getLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            data.put("gps_lat", String.valueOf(latitude));
            data.put("gps_lon", String.valueOf(longitude));
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            OrderListActivity.hasTracking.put(OrderListActivity.orderTracking, true);
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url2, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        }else{

            gps.showSettingsAlert();
        }

    }
    public Runnable updateData = new Runnable(){
        public void run(){
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            requestTracking();
            ////// set the interval time here
            MainActivity.handler.postDelayed(updateData, 10000);
        }
    };
    public String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        int minutes = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);

        return Integer.toString(day)+"/"+Integer.toString(month)+" "+Integer.toString(hour)+":"+Integer.toString(minutes);
    }
    private void getInitTimer(){
        //call the service here
        // create class object

            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject o) {
                    processTime(o);

                }
            };
            Response.ErrorListener error = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    // alertError();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            };
            String url3 = url+"?"+"key="+MainActivity.key+"&order_id="+item.chapterId.replaceAll("#","");
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url3, Request.Method.GET,JSONObject.class, MainActivity.header,null,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);

    }
}
