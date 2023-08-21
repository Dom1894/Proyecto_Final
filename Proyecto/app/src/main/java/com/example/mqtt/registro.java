package com.example.mqtt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class registro extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;
    private RequestQueue requestQueue;

    private MqttHandler mqttHandler;
    private static final String BROKER_URL = "tcp://18.118.188.12";
    private static final String CLIENT_ID = "your_client_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        requestQueue = Volley.newRequestQueue(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("DEMO","F_PO_A");
                subscribeToTopic("F_PO_A");

                String id = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String username = "usuario";
                String url = "https://luisappgim.000webhostapp.com/appgim/newusuario.php";

                // Crear el cuerpo de los datos en formato x-www-form-urlencoded
                String requestBody = "usuario=" + Uri.encode(username) +
                        "&id=" + Uri.encode(id) +
                        "&contrase=" + Uri.encode(password);

                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(registro.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                delayedInterface();
                            }

                            private void delayedInterface() {
                                Handler handler = new Handler(Looper.getMainLooper());


                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent inten =new Intent(registro.this, login.class);
                                        startActivity(inten);
                                    }
                                }, 500);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMessage = "Registration failed: " + error.getMessage();
                                Toast.makeText(registro.this, errorMessage, Toast.LENGTH_SHORT).show();
                                System.out.println(errorMessage);
                            }
                        }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return requestBody.getBytes();
                    }
                };
                requestQueue.add(request);
            }
        });
    }
    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
    }
    private void publishMessage(String topic, String message){
        Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();
        mqttHandler.publish(topic,message);
    }
    private void subscribeToTopic(String topic){
        Toast.makeText(this, "Subscribing to topic "+ topic, Toast.LENGTH_SHORT).show();
        mqttHandler.subscribe(topic);
    }
}