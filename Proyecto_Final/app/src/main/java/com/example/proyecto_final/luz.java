package com.example.proyecto_final;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class luz extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;
    private ArrayList<String> mNameDevices;
    private Spinner disponiblesSpinner;
    private ArrayList<BluetoothDevice> mBluetoothDevices;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String deviceName = device.getName();
                    if (deviceName != null && !mNameDevices.contains(deviceName)) {
                        mNameDevices.add(deviceName);
                        mBluetoothDevices.add(device);
                        updateSpinner();
                    }
                }
            }
        }
    };

    private void updateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mNameDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disponiblesSpinner.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luz);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mNameDevices = new ArrayList<>();
        mBluetoothDevices = new ArrayList<>();

        disponiblesSpinner = findViewById(R.id.disponibles);
        Button conectarButton = findViewById(R.id.conectar);
        Button dispButton = findViewById(R.id.disp);

        conectarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar la funcionalidad para el botón "Conectar"
            }
        });

        dispButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBtAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth no es compatible", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mBtAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                } else {
                    scanBluetoothDevices();
                }
            }
        });
    }

    private void scanBluetoothDevices() {
        mNameDevices.clear();
        mBluetoothDevices.clear();

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            mBtAdapter.startDiscovery();
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanBluetoothDevices();
            } else {
                Toast.makeText(this, "Permiso de Bluetooth no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
