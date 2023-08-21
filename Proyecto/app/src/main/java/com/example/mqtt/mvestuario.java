package com.example.mqtt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class mvestuario extends AppCompatActivity {
    ToggleButton toggleButton, toggleButton2, toggleButton3;
    ImageView lampara, pausaImage, playImage;

    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    private MqttHandler mqttHandler;
    private static final String BROKER_URL = "tcp://18.118.188.12";
    private static final String CLIENT_ID = "your_client_id";
    SeekBar intensitySeekBar;
    Button regresarButton;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private boolean isMusicPlaying = false;
    private Spinner songSpinner;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvestuario);

        toggleButton = findViewById(R.id.toggle_button);
        toggleButton2 = findViewById(R.id.toggle_button2);
        toggleButton3 = findViewById(R.id.toggle_button3);
        intensitySeekBar = findViewById(R.id.intensity_seekbar);
        lampara = findViewById(R.id.lampara);
        regresarButton = findViewById(R.id.regresar);
        songSpinner = findViewById(R.id.songSpinner);
        pausaImage = findViewById(R.id.pausa);
        playImage = findViewById(R.id.play);

        final int activatedColor = getResources().getColor(R.color.toggle_color);
        final int deactivatedColor = Color.YELLOW;

        // Inicialización de Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // El dispositivo no soporta Bluetooth
            return;
        }

        Emisor emisor = new Emisor();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        toggleButton.setChecked(sharedPreferences.getBoolean("toggleButtonStatevm", false));

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","F_PO_A");
                subscribeToTopic("F_PO_A");

                String valor;
                if (isChecked) {
                    lampara.setColorFilter(null);
                    valor = "apagado";
                } else {
                    ColorFilter filter = new LightingColorFilter(Color.YELLOW, (int) (100 * 255));
                    lampara.setColorFilter(filter);
                    valor = "encendido";
                }
                String clave = "mvestuario_Lampara";
                emisor.enviarDatos(clave, valor);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("toggleButtonStatevm", isChecked);
                editor.apply();
            }


        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.songs_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        songSpinner.setAdapter(adapter);

        // Crear el reproductor de medios
        mediaPlayer = new MediaPlayer();
        intensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100.0f;
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No es necesario implementar esto
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No es necesario implementar esto
            }
        });
        songSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Detener y resetear el reproductor si ya se estaba reproduciendo
                mediaPlayer.reset();
                // Obtener el ID de la canción seleccionada
                int songId = getResources().getIdentifier(
                        songSpinner.getSelectedItem().toString(),
                        "raw", getPackageName());

                // Cargar la canción seleccionada en el reproductor
                mediaPlayer = MediaPlayer.create(mvestuario.this, songId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No es necesario implementar esto
            }
        });

        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String valor;
                if (isChecked) {
                    valor = "apagado";
                    intensitySeekBar.setVisibility(View.GONE);
                    pausaImage.setVisibility(View.INVISIBLE);
                    playImage.setVisibility(View.INVISIBLE);
                    songSpinner.setVisibility(View.INVISIBLE);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }

                } else {
                    valor = "encendido";
                    intensitySeekBar.setVisibility(View.VISIBLE);
                    pausaImage.setVisibility(View.VISIBLE);
                    playImage.setVisibility(View.VISIBLE);
                    songSpinner.setVisibility(View.VISIBLE);
                }
                String clave = "mvestuario_altavos";
                emisor.enviarDatos(clave, valor);
            }
        });
        pausaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isMusicPlaying = false;
                    stopMusicOverBluetooth();
                }
            }
        });

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    isMusicPlaying = true;
                    sendMusicOverBluetooth();
                }
            }
        });

        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String valor;
                if (isChecked) {
                    valor = "apagado";
                } else {
                    valor = "encendido";
                }
                String clave = "mvestuario_ventana";
                emisor.enviarDatos(clave, valor);
            }
        });
        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mvestuario.this, menu.class);
                startActivity(intent);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });
    }
    private void sendMusicOverBluetooth() {
        if (isMusicPlaying && bluetoothAdapter.isEnabled()) {
            // Obtener la lista de dispositivos Bluetooth emparejados
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            // Buscar el dispositivo específico al que deseas conectar (por ejemplo, por nombre)
            BluetoothDevice targetDevice = null;
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("Nombre del dispositivo")) {
                    targetDevice = device;
                    break;
                }
            }

            if (targetDevice != null) {
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();

                    // Inicializar el flujo de salida para enviar datos
                    outputStream = bluetoothSocket.getOutputStream();

                    // Obtener el ID de la canción seleccionada
                    int songId = getResources().getIdentifier(
                            songSpinner.getSelectedItem().toString(),
                            "raw", getPackageName());

                    // Abrir el recurso de la canción
                    InputStream musicStream = getResources().openRawResource(songId);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = musicStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    // Cerrar el flujo de salida y liberar recursos
                    outputStream.close();
                    musicStream.close();

                    // Cerrar el socket Bluetooth
                    bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }

                        if (bluetoothSocket != null) {
                            bluetoothSocket.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    private void stopMusicOverBluetooth() {
        if (!isMusicPlaying && bluetoothAdapter.isEnabled()) {
            if (!isMusicPlaying && bluetoothAdapter.isEnabled()) {
                try {
                    // Cerrar el flujo de salida y liberar recursos
                    if (outputStream != null) {
                        outputStream.close();
                        outputStream = null;
                    }

                    // Cerrar el socket Bluetooth
                    if (bluetoothSocket != null) {
                        bluetoothSocket.close();
                        bluetoothSocket = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
        // Cerrar BluetoothSocket y liberar recursos
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
