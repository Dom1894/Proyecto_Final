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

    Button regresar;
    Button seleccionarMusica;

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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

        seleccionarMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar los permisos para leer el almacenamiento externo
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*"); // Limitar la selección a archivos de audio
        startActivityForResult(intent, REQUEST_SELECT_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_MUSIC && resultCode == RESULT_OK && data != null) {
            Uri selectedMusicUri = data.getData();
            // Aquí puedes hacer lo que desees con la música seleccionada, por ejemplo, reproducirla.
            Toast.makeText(getApplicationContext(), "Música seleccionada: " + selectedMusicUri.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
