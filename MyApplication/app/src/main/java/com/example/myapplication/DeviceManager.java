package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceManager {

    public static void postRegistrarDispositivoEnServidor(String token, Context context){
    // Instantiate the RequestQueue.
    RequestQueue queue = Volley.newRequestQueue(context);
    String url = "https://luisappgim.000webhostapp.com/appgim/registrarDispositivo.php";

    // Request a string response from the provided URL.
    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject respObj = new JSONObject(response);

                        String code = respObj.getString("code");
                        String message = respObj.getString("message");
                        Integer id = respObj.getInt("id");

                        if ("OK".equals(code)){
                            context.getSharedPreferences("SP_FILE",0).edit()
                                    .putString("DEVICEID", token).commit();
                            if (id!=0){
                                context.getSharedPreferences("SP_FILE",0)
                                        .edit().putInt("ID",id).commit();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //textView.setText("That didn't work!");
            Toast.makeText(context, "Error registrando token en servidor:"
                    + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            params.put("DEVICEID", token);
            if (context.getSharedPreferences("SP_FILE",0)
                    .getInt("ID",0) != 0){
                Integer val = context.getSharedPreferences("SP_FILE",0)
                        .getInt("ID",0);
                params.put("ID", val.toString());
            }
            return params;
        };

    };

    // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}