package com.example.control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity2 extends AppCompatActivity {

    ImageButton boton_luz,boton_musica,boton_ventanas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ///Abrir Otras Interfaces///
        boton_luz=(ImageButton) findViewById(R.id.boton_luz);
        boton_luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el Intent para abrir la nueva actividad
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });
        boton_musica=(ImageButton) findViewById(R.id.boton_musica);
        boton_musica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el Intent para abrir la nueva actividad
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

        boton_ventanas=(ImageButton) findViewById(R.id.boton_ventanas);
        boton_ventanas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el Intent para abrir la nueva actividad
                Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

    }
}