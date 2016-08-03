package dsv.shopertracking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class CheckPointActivity extends Activity {

    private static String orderURL = "http://shpr.marketoi.com/orders/";
    private static String rateURL = "http://shpr.marketoi.com/orders/";
    private static String customerInfoUrl = "http://catalogue.marketoi.com/index.php/api/Front/order_user?mo_id=";
    private static OrderListChapter item = null;
    static public String orderID;
    static public String moID;
    static public String status;
    static public ArrayList<String> cus_Info;
    TextView customerName;
    TextView customerPhone;
    TextView customerAddress;
    TextView customerNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_point);
        if (item == null) {
            finish();
            return;
        }
        final TextView idOrder = (TextView) findViewById(R.id.idOrder);
        final Button checkPoints = (Button) findViewById(R.id.checkPoints);
        final Button viewOrder = (Button) findViewById(R.id.viewOrder);
        final Button rate = (Button) findViewById(R.id.rate);
        final Button back = (Button) findViewById(R.id.backBtn_2);
        customerName = (TextView) findViewById(R.id.textName);
        customerPhone = (TextView) findViewById(R.id.textPhone);
        customerAddress = (TextView) findViewById(R.id.textAddress);
        customerNote = (TextView) findViewById(R.id.textNote);


        idOrder.setText(item.chapterId);
        checkPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTracking();
            }
        });
        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderURL = orderURL+item.chapterId.replaceAll("#","")+"/details/"+MainActivity.key;
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fixURL(orderURL)));
                //OrderDetailActivity.class

                Intent browserIntent = new Intent(CheckPointActivity.this,OrderDetailActivity.class);
                startActivity(browserIntent);

            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateURL = rateURL+item.chapterId.replaceAll("#","")+"/rate/"+MainActivity.key;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fixURL(rateURL)));
                startActivity(browserIntent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.routePath ="OrderList";
                finish();
            }
        });
        changeStatusField();new loadCustomerInfoTask().execute();
    }




    private void changeStatusField(){
        final View circle = (View) findViewById(R.id.circle);
        final TextView status = (TextView) findViewById(R.id.statusOrder);
        //change Status
        if (item.chapterStatus.equals("1")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("not started yet");
        }
        else if (item.chapterStatus.equals("2")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("approved");
        }
        else if (item.chapterStatus.equals("3")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_online));
            status.setText("started");
        }
        else if (item.chapterStatus.equals("4")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("shopped");
        }
        else if (item.chapterStatus.equals("9")){
            circle.setBackground(getResources().getDrawable(R.drawable.circle_offine));
            status.setText("delivered");
        }
    }
    public static void setItem(OrderListChapter i)
    {
        item =i;
    }
    private String fixURL(String url){
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            return  "http://" + url;
        return url;
    }
    public void gotoTracking(){
        SetTrackingActivity.setItem(item);
        MainActivity.routePath = "SetTracking";
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_point, menu);
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
    private String getData(){
        String result = "";
        orderID = item.chapterId.replaceAll("#","");
        moID = item.chapterMoId;
        status = item.chapterStatus;
        String cus_InfoUrl = customerInfoUrl + CheckPointActivity.moID;
        try{
            HttpURLConnection con = (HttpURLConnection) new URL(cus_InfoUrl).openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer(1024);
            String t = "";
            while ((t = bufferedReader.readLine()) != null){
                stringBuffer.append(t).append("\n");
            }
            bufferedReader.close();
            return  stringBuffer.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public class loadCustomerInfoTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            return getData();
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            String jsonString = data;
            String name=null;
            String phone=null;
            String address=null;
            String note=null;
            JSONObject customerInfo = null;
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            try {
                jsonObject = new JSONObject(jsonString);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                jsonArray = jsonObject.getJSONArray("result");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{

                customerInfo = jsonArray.getJSONObject(0);
                name = customerInfo.getString("cus_last_name");
                phone = customerInfo.getString("cus_phone");
                address = customerInfo.getString("cus_address");
                note = customerInfo.getString("cus_access");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            customerName.setText("Name: " + name);
            customerPhone.setText("Phone:" + phone);
            customerAddress.setText("Address: " + address);
            customerNote.setText("Note:" + note);
        }
    }


}
