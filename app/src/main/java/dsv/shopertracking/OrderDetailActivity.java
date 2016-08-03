package dsv.shopertracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hoang on 7/24/16.
 */
public class OrderDetailActivity extends Activity {

    static public OrderDetailChapter currentItem;

    TextView totalAmount;
    Button bill;
    ListView orderDetail;
    Context appContext;
    ArrayList<Boolean> boughtItem = new ArrayList<Boolean>();
    String url = "http://catalogue.marketoi.com/index.php/api/Front/order?order_id=";
    String urlMoID = "http://catalogue.marketoi.com/index.php/api/Front/order?mo_id=";


    static String jsonString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        totalAmount = (TextView) findViewById(R.id.total_amount);
        bill = (Button) findViewById(R.id.bill);
        orderDetail = (ListView) findViewById(R.id.order_detail);


    }

    @Override
    protected void onStart() {
        super.onStart();
        new loadDataTask().execute(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!CheckPointActivity.status.equals("9")){
            boughtItem.clear();
            for (int i = 0 ; i< orderDetail.getAdapter().getCount() ; i++){
                OrderDetailChapter chapter = (OrderDetailChapter) orderDetail.getItemAtPosition(i);
                boughtItem.add(chapter.bought);
            }
            String filename = CheckPointActivity.orderID;
            FileOutputStream outputStream = null;
            String gsonBoughtItem = new Gson().toJson(boughtItem);
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.flush();
                outputStream.write(gsonBoughtItem.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getData(String url){
        String result = "";

        url = url + CheckPointActivity.orderID;
        urlMoID = urlMoID + CheckPointActivity.moID;
        try{
            HttpURLConnection con = (HttpURLConnection) new URL(urlMoID).openConnection();
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
    public class loadDataTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            return getData(strings[0]);
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            jsonString = data;
            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                orderDetail.setAdapter(new OrderDetailAdapter(jsonObject,OrderDetailActivity.this));
                if (!CheckPointActivity.status.equals("9")){
                    orderDetail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                            OrderDetailChapter chapter = (OrderDetailChapter) orderDetail.getItemAtPosition(i);
                            chapter.bought = true;
                            BaseAdapter adapter = (BaseAdapter) orderDetail.getAdapter();
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                }
                orderDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        currentItem = (OrderDetailChapter) orderDetail.getItemAtPosition(i);
                        Intent intent = new Intent(OrderDetailActivity.this,OrderItemDetailActivity.class);
                        startActivity(intent);
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }
            calculateTotal();
            readFile();
        }
    }

    private void calculateTotal(){
        long total = 0;
        for (int i = 0 ; i<orderDetail.getAdapter().getCount() ; i++){
            OrderDetailChapter chapter = (OrderDetailChapter) orderDetail.getItemAtPosition(i);
            total += Long.parseLong(chapter.unitPrice) * Long.parseLong(chapter.qty);
        }
        totalAmount.setText(String.valueOf(total));
    }



    private void readFile(){
        String filename = CheckPointActivity.orderID;
        FileInputStream inputStream;
        String gsonBoughtItem = "";

        try {
            inputStream = openFileInput(filename);
            int c;
            while ((c = inputStream.read())!=-1){
                gsonBoughtItem += (char) c;
            }
            inputStream.close();
            boughtItem = new Gson().fromJson(gsonBoughtItem,boughtItem.getClass());

            for (int i = 0 ; i<boughtItem.size() ; i++){
                OrderDetailChapter chapter = (OrderDetailChapter) orderDetail.getItemAtPosition(i);
                chapter.bought = boughtItem.get(i);
                BaseAdapter adapter = (BaseAdapter) orderDetail.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
