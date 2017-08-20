package me.akash.testwiki;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aakas on 8/20/2017.
 */
public class CustomObjectRequest extends JsonObjectRequest
{
    public CustomObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener)
    {
        super(method, url, jsonRequest, listener, errorListener);
    }


    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("Api-User-Agent", "aakashswain001@gmail.com/2.0");
        return headers;
    }

}