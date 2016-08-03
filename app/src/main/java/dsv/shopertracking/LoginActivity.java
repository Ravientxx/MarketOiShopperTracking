package dsv.shopertracking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;




public class LoginActivity extends Activity {
    //URL to get JSON Array
    private static String url = "http://catalogue.marketoi.com/index.php/api/App/connect";

    //JSON Node Names
    SharedPreferences pref;
    String user;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText pass = (EditText) findViewById(R.id.password);
        final TextView notifyText = (TextView) findViewById(R.id.notifyTxt);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String emailTxt =pref.getString("userName", null);
        String passTxt = pref.getString("pass",null);
        email.setText(emailTxt);
        pass.setText(passTxt);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailTxt = email.getText().toString();
                String passTxt = pass.getText().toString();
                if (MainActivity.hasAPI) {
                        checkLogin(emailTxt, passTxt);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    private void wrongLogin (){
        final EditText pass = (EditText) findViewById(R.id.password);
        final TextView notifyText = (TextView) findViewById(R.id.notifyTxt);
        pass.setText("");
        notifyText.setText("Wrong email or password, please check it");
    }
    private void checkLogin (final String email, final String pass) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                System.out.print("O respone: "+o);
                try {
                    JSONObject res =o.getJSONObject("result");
                    MainActivity.key = (String) res.get("key");
                    if (((String)o.get("status")).equals("true"))
                    {
                      SharedPreferences.Editor editor = pref.edit();
                        editor.putString("userName",email);
                        editor.putString("pass",pass);
                        editor.commit();
                        MainActivity.isUser = true;
                        MainActivity.routePath = "OrderList";
                        finish();
                        return;

                    }
                    wrongLogin();
                } catch (JSONException e){
                    e.printStackTrace();
                    wrongLogin();
                }

            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                wrongLogin();
            }

        };
        Map<String,String> data = new HashMap<String,String>();
        data.put("email",email);
        data.put("pass",pass);

        try {
            // Creating new JSON Parser
            GsonRequest<JSONObject> gsonR = new GsonRequest(url, Request.Method.POST, JSONObject.class, MainActivity.header,data,listener, error);
            AppController.getInstance().addToRequestQueue(gsonR);
        } catch (Exception e){
            e.printStackTrace();
            wrongLogin();
        }


    }
}
