package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class login extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView mensajes;
    private String tipo;
    private String contraseñat;
    private String usuario2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginButton = (Button) findViewById(R.id.loginButton);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mensajes = (TextView) findViewById(R.id.mensajes);
        registar_dispositivo();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarusuario();
            }
        });
    }
        public void validarusuario(){
            String usuario2=usernameEditText.getText().toString();
            if (!usuario2.isEmpty()) {
                if (verificarConexionInternet()) {
                    String url = "https://luisappgim.000webhostapp.com/appgim/examen_buscar_user.php?id=" + usuario2;
                    Buscar(url);
                } else {
                    Toast.makeText(login.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(login.this, "Escribe el usuario", Toast.LENGTH_SHORT).show();
            }
        }
        private boolean verificarConexionInternet() {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        }
        public void registar_dispositivo(){
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(ContentValues.TAG,
                                        "Fetching FCM registration token failed",
                                        task.getException());
                                return;
                            }

                            String token = task.getResult();
                            String tokenGuardado = getSharedPreferences("SP_FILE",0)
                                    .getString("DEVICEID",null);
                            if (token != null ){
                                if (tokenGuardado == null || !token.equals(tokenGuardado)){
                                    //registramos el token en el servidor
                                    DeviceManager.postRegistrarDispositivoEnServidor( token, login.this);
                                }
                            }
                            //ver notificacion
                            //Toast.makeText(login.this, token,Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        public void Buscar(String url) {
            String contraseña1= String.valueOf(passwordEditText.getText());
            JsonArrayRequest stringaray=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject = null;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            usuario2 = jsonObject.getString("id");
                            contraseñat = jsonObject.getString("contrase");
                            if(contraseña1.equals(contraseñat)){
                                mensajes.setText("");
                                Intent inten =new Intent(login.this, menu.class);
                                inten.putExtra("tipo", tipo);
                                inten.putExtra("usuario", usuario2);
                                startActivity(inten);
                            }else{
                                mensajes.setText("contraseña equivocada");
                                Toast.makeText(getApplicationContext(), "contraseña equivocada", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "json"+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mensajes.setText("usuario no encontrado en la base de datos");
                    Toast.makeText(getApplicationContext(), "usuario no encontrado en la base de datos", Toast.LENGTH_LONG).show();
                    System.out.println(error.getMessage());
                }
            });
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringaray);
        }
    }