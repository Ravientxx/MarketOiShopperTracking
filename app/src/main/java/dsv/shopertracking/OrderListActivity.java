package dsv.shopertracking;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class OrderListActivity extends Activity {
    //JSON Node Names
    private static final String TAG_ORDER = "order";
    private static final String TAG_RESPONSE = "result";
    private static final String TAG_BRANCH = "branch";
    private static final String TAG_NAME= "name";
    private static final String TAG_CONTAINER = "container";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_UNIT = "unit";
    private static final String TAG_DESC = "short_desc";
    private static final String TAG_BAR = "bar_code";
    private static final String TAG_QTY = "qty";
    private static final String TAG_PRICE = "unit_price";
    private static final String TAG_CURRENCY = "currency";
    public static Map<String,Boolean> hasTracking = new HashMap<String,Boolean>();
    public  JSONArray order_list = null;
    public  JSONArray order_list_old = null;
    public static OrderListChapter item;
    Switch availableSwitch;
    Boolean status = null;
    //URL to get JSON Array
    GPSTracker gps;
    Button reload;
    public static  String orderTracking;
    private String url = "http://catalogue.marketoi.com/index.php/api/App/order";
    private String url2 = "http://catalogue.marketoi.com/index.php/api/App/available";
    private String url3 = "http://catalogue.marketoi.com/index.php/api/App/track";
    private String url4 = "http://catalogue.marketoi.com/index.php/api/App/wallet";
    private String url5 = "http://catalogue.marketoi.com/index.php/api/App/avatime";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);

        try {
            order_list=null;
            order_list_old=null;
            getOrderList();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        availableSwitch = (Switch) findViewById(R.id.switch1);
        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    status = true;
                    setAvailable(status);
                } else {
                    status = false;
                    setAvailable(status);
                }
            }
        });
        getAvailable();
        reload = (Button)findViewById(R.id.reload_btn);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    order_list=null;
                    getOrderList();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                availableSwitch = (Switch) findViewById(R.id.switch1);
                availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // do something, the isChecked will be
                        // true if the switch is in the On position
                        if (isChecked) {
                            status = true;
                            setAvailable(status);
                        } else {
                            status = false;
                            setAvailable(status);
                        }
                    }
                });
                getAvailable();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_list, menu);
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
    private void alertError(){

    }
    private  void processDataList(JSONObject json){
        try {
            // Getting JSON Array
           order_list = json.getJSONArray(TAG_RESPONSE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        showOrderList();
    }
    private  void processDataList2(JSONObject json){
        try {
            // Getting JSON Array
            order_list_old = json.getJSONArray(TAG_RESPONSE);
            showOrderList_old();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void getOrderListUrl () {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                checkStart(o);
                processDataList(o);
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                alertError();
            }

        };
        order_list=null;
        String url_2 = url+"?key="+MainActivity.key+"&filter=status<9";

        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url_2, Request.Method.GET,JSONObject.class, MainActivity.header,null,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    }

    private void getOrderListUrl2 () {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                processDataList2(o);
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                alertError();
            }

        };
        order_list_old=null;
        String url_2 = url+"?key="+MainActivity.key+"&filter=status=9";

        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url_2, Request.Method.GET,JSONObject.class, MainActivity.header,null,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    }

    private void getAvailable () {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                try {
                    String stt = (String) res.get("status");
                    if (stt.equals("true")) {
                        //get element
                        JSONObject available = (JSONObject) res.get("result");
                        if (available.getString("available").equals("t")) {
                            availableSwitch.setChecked(true);
                        } else {
                            availableSwitch.setChecked(false);
                        }

                    }
                }
                    catch(JSONException e){
                        e.printStackTrace();
                        }

                }
            };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(getApplicationContext(), "Change fail!", Toast.LENGTH_LONG).show();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        String avaibleUrl = url2+"?key="+MainActivity.key;
        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(avaibleUrl, Request.Method.GET,JSONObject.class, MainActivity.header,null,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    };

    private void setAvailable (Boolean status) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                // processDataList(o);
                Toast.makeText(getApplicationContext(), "Change available!"+o.toString(), Toast.LENGTH_LONG).show();
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                volleyError.printStackTrace();
//                alertError();
                Toast.makeText(getApplicationContext(), "Change fail!", Toast.LENGTH_LONG).show();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        data.put("key",MainActivity.key);
        String statusParam = Boolean.toString(status);
        data.put("available",statusParam);
        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url2, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            alertError();
        }
    };
    public void getOrderList() throws JSONException {

        if (MainActivity.hasAPI) {
            getOrderListUrl();

            getOrderListUrl2();
        }
    }
    public void showOrderList()
    {
    if(order_list!=null){
        final OrderListAdapter mOder = new OrderListAdapter(order_list,OrderListActivity.this);
        final ListView orderListView = (ListView)findViewById(R.id.listView);
        orderListView.setAdapter(mOder);
        orderListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrderListChapter item = (OrderListChapter) orderListView.getItemAtPosition(position);
                showCheckPoints(item, OrderListActivity.this);
            }
        });

    }
    }
    public void showOrderList_old()
    {
        if(order_list_old!=null){
            final OrderListAdapter mOder = new OrderListAdapter(order_list_old,OrderListActivity.this);
            final ListView orderListView = (ListView)findViewById(R.id.listView_new);
            orderListView.setAdapter(mOder);
            orderListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    OrderListChapter item = (OrderListChapter) orderListView.getItemAtPosition(position);
                    showCheckPoints(item, OrderListActivity.this);
                }
            });

        }
    }
    public void showCheckPoints(OrderListChapter cartInfo,Context context)
    {
        CheckPointActivity.setItem(cartInfo);
        MainActivity.routePath ="CheckPoint";
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  handler.removeCallbacksAndMessages(null);
    }
    public Runnable updateData = new Runnable(){
        public void run(){
            requestTracking();
            MainActivity.handler.postDelayed(updateData, 10000);
        }
    };
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
                    Toast.makeText(getApplicationContext(),o+ "success", Toast.LENGTH_LONG).show();
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
            data.put("order_id", orderTracking);
            Location location = gps.getLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            data.put("gps_lat", String.valueOf(latitude));
            data.put("gps_lon", String.valueOf(longitude));
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            OrderListActivity.hasTracking.put(orderTracking, true);
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url3, Request.Method.POST,JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }
    public void checkStart(JSONObject res){
        try {
            String stt = (String) res.get("status");
            if (stt.equals("true")){
                //get element
                if(res.get("result") instanceof String){}
                else{
                    JSONObject timer = ((JSONArray) res.get("result")).getJSONObject(0);
                    orderTracking=timer.getString("id");
                    if(timer.getString("status").equals("3")||timer.getString("status").equals("4")){
                        if(!MainActivity.handler.hasMessages(0))
                            MainActivity.handler.postDelayed(updateData,10000);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
