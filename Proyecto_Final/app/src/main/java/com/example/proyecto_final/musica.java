package com.example.proyecto_final;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class musica extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int REQUEST_SELECT_MUSIC = 2;

    private Button regresar;
    private Button seleccionarMusica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musica);

        regresar = findViewById(R.id.regresar);
        seleccionarMusica = findViewById(R.id.seleccionar_musica);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el Intent para abrir la nueva actividad
                Intent intent = new Intent(musica.this, MainActivity.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

    }
}