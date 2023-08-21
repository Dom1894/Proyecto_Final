package com.example.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class Nuevo extends AppCompatActivity {

    ToggleButton Luz, Bocina, Ventilador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);
        /////////////////////////////////////////////
        Luz=(ToggleButton) findViewById(R.id.Luz);
        Bocina=(ToggleButton) findViewById(R.id.Bocina);
        Ventilador=(ToggleButton) findViewById(R.id.Ventilador);
        /////////////////////////////////////////////
        Luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        Bocina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Ventilador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}