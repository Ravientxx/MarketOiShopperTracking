package dsv.shopertracking;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.transform.ErrorListener;

import static org.apache.commons.lang3.StringUtils.strip;

/**
 * Created by hocnv on 5/21/15.
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Map<String, String> data;
    private final Response.Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url,int method, Class<T> clazz, Map<String, String> headers,Map<String, String> data,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        if (method == Method.POST)
            this.data = data;
        else this.data = null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String auth = "Basic "
                + Base64.encodeToString((MainActivity.userAuth + ":" + MainActivity.passAuth).getBytes(),
                Base64.NO_WRAP);
        headers.put("Authorization", auth);
        return headers;
    }
    @Override
    protected Map<String,String> getParams(){
        return data;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data);
            json = json.replaceAll("\\n","");
            try {
                final JSONObject jObj = new JSONObject(json);
                return (Response<T>) Response.success(jObj,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (JSONException e) {
                e.printStackTrace();
                return Response.error(new ParseError(e));
            }
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}

