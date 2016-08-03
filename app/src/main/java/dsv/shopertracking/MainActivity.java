package dsv.shopertracking;

import dsv.shopertracking.AppController;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class  MainActivity extends Activity {

    public static boolean isUser = false;
    public static boolean hasAPI = true;
    public static String routePath = "/";
    public static String key = "";

    public static String userAuth = "admin";
    public static String passAuth = "1234";
    public static String contentHeader ="application/json";
    public static Map<String,String> header = new HashMap<>();

    private String packagePath = "dsv.shopertracking";


    public static Activity mainInstance;

    private String[] routeKey = new String [20];
    private Class[] routeClass = new Class [20];
    public static Handler handler = new Handler();

    Class<?> cls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainInstance = this;
        super.onCreate(savedInstanceState);
        header.put("Content", "application/json;charset=utf-8");

        routeKey[0]="Login";
        routeKey[1]="OrderList";
        routeKey[2]="CheckPoint";
        routeKey[3]="SetTracking";
    }
    @Override
    protected void onStart() {
        super.onStart();
        Router();
    }
    public void Router(){
        if ((routePath.equals("/")) || (!isUser))
            routePath = "Login";

        gotoScreen(this.getApplicationContext());
    }
    public  void gotoScreen(Context context) {
        String screenGo = packagePath+"."+routePath+"Activity";
        try {
            cls = Class.forName(screenGo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent myIntent = new Intent(context, cls);
            startActivity(myIntent);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
