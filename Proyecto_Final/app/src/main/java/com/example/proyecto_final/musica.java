package com.example.proyecto_final;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        seleccionarMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_SELECT_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_MUSIC && resultCode == RESULT_OK && data != null) {
            Uri selectedMusicUri = data.getData();
            if (selectedMusicUri != null) {
                playMusicViaBluetooth(selectedMusicUri);
            } else {
                Toast.makeText(this, "No se pudo obtener la música seleccionada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playMusicViaBluetooth(Uri musicUri) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth no está habilitado", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            Toast.makeText(this, "No hay dispositivos Bluetooth vinculados", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Mostrar un diálogo para que el usuario elija el dispositivo Bluetooth al que desea conectarse

        // Establecer la conexión Bluetooth y enviar los datos para reproducir la música.
        ConnectBluetoothTask connectBluetoothTask = new ConnectBluetoothTask(bluetoothAdapter, musicUri);
        connectBluetoothTask.execute();
    }

    private class ConnectBluetoothTask extends AsyncTask<Void, Void, Boolean> {
        private final BluetoothAdapter bluetoothAdapter;
        private final Uri musicUri;

        ConnectBluetoothTask(BluetoothAdapter adapter, Uri musicUri) {
            this.bluetoothAdapter = adapter;
            this.musicUri = musicUri;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Aquí deberías establecer la conexión Bluetooth con el dispositivo específico
                // al que deseas enviar la música, y luego enviar los datos de música mediante Bluetooth.
                // La implementación exacta variará según el tipo de dispositivo Bluetooth.

                BluetoothDevice bluetoothDevice = ...; // Obtener el dispositivo Bluetooth específico o buscar dispositivos cercanos
                BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                socket.connect();

                OutputStream outputStream = socket.getOutputStream();
                // ... (Aquí es donde enviarías la música a través del socket Bluetooth)

                outputStream.close(); // Cerrar el flujo de salida
                socket.close(); // Cerrar el socket
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(musica.this, "Reproduciendo música en Bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(musica.this, "Error al conectar o transmitir la música por Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
