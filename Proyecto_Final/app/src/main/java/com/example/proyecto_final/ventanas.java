package com.example.proyecto_final;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ventanas extends AppCompatActivity {

    private Button regresar;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    // Dirección MAC del dispositivo Bluetooth al que deseas conectarte.
    private static final String BLUETOOTH_DEVICE_ADDRESS = "XX:XX:XX:XX:XX:XX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventanas);

        // Inicializar el adaptador Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        regresar = findViewById(R.id.regresar);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    // Si el adaptador Bluetooth está habilitado, intenta conectar al dispositivo Bluetooth
                    connectToBluetoothDevice();
                } else {
                    // Si el Bluetooth no está habilitado, solicita habilitarlo
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
            }
        });
    }

    // Método para conectar al dispositivo Bluetooth
    private void connectToBluetoothDevice() {
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(BLUETOOTH_DEVICE_ADDRESS);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID estándar para el módulo Bluetooth SPP (Serial Port Profile)

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // En este punto, la conexión se ha establecido con éxito, ahora puedes enviar comandos para controlar las ventanas
            // Por ejemplo, enviar un comando "ABRIR" o "CERRAR" a través del socket Bluetooth.
            // Puedes usar OutputStream y InputStream para enviar y recibir datos.
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar al dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegúrate de cerrar el socket Bluetooth cuando la actividad se destruya.
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
