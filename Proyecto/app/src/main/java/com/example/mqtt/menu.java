package com.example.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void onrecepcionClick(View view) {
        Intent intent = new Intent(this, Recepcion.class);
        startActivity(intent);
    }
    public void onvesmClick(View view) {
        Intent intent = new Intent(this, mvestuario.class);
        startActivity(intent);
    }
    public void onveshClick(View view) {
        Intent intent = new Intent(this, vestuario_Hombres.class);
        startActivity(intent);
    }
    public void onDepositoClick(View view) {
        Intent intent = new Intent(this, deposito.class);
        startActivity(intent);
    }
    public void onnotificacionesClick(View view) {
        Intent intent = new Intent(this, activity_notificaciones.class);
        startActivity(intent);
    }
}