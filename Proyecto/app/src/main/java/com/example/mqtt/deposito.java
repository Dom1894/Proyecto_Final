package com.example.mqtt;

import android.content.Intent;
import android.content.SharedPreferences;
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


public class deposito extends AppCompatActivity {
    ToggleButton toggleButton, toggleButton_1,toggleButton2,toggleButton3,toggleButton_3;
    ImageView lampara,pausaImage,playImage;

    private MediaPlayer mediaPlayer;
    private MqttHandler mqttHandler;
    private static final String BROKER_URL = "tcp://54.234.221.112";
    private static final String CLIENT_ID = "your_client_id";
    private SharedPreferences sharedPreferences;

    SeekBar intensitySeekBar;
    Button regresarButton,apagaluz,encenderluz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito);

        toggleButton = findViewById(R.id.toggle_button);
        toggleButton_1 = findViewById(R.id.toggle_button_1);
        toggleButton2 = findViewById(R.id.toggle_button2);
        toggleButton_3 = findViewById(R.id.toggle_button_3);
        toggleButton3 = findViewById(R.id.toggle_button3);
        intensitySeekBar = findViewById(R.id.intensity_seekbar);
        lampara = findViewById(R.id.lampara);
        regresarButton = findViewById(R.id.regresar);
        Spinner songSpinner = findViewById(R.id.songSpinner);
        pausaImage = findViewById(R.id.pausa);
        playImage = findViewById(R.id.play);


        apagaluz = findViewById(R.id.apagaluz);
        encenderluz= findViewById(R.id.encenderluz);

        final int activatedColor = getResources().getColor(R.color.toggle_color);
        final int deactivatedColor = Color.YELLOW;
        Emisor emisor = new Emisor();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        encenderluz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","E_Luces_Principales");
                subscribeToTopic("E_Luces_Principales ");



            }
        });

        apagaluz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","A_Luces_Principales");
                subscribeToTopic("A_Luces_Principales ");



            }
        });



        toggleButton.setChecked(sharedPreferences.getBoolean("toggle_buttonapagar", false));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","E_Luces_Principales");
                subscribeToTopic("A_Luces_Principales ");

                String valor ;
                if (isChecked) {
                    lampara.setColorFilter(null);
                    valor = "apagado";
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("toggleButtonStated", isChecked);
                editor.apply();
            }
        });




        toggleButton.setChecked(sharedPreferences.getBoolean("toggleButtonStated", false));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","E_Luces_Principales");
                subscribeToTopic("A_Luces_Principales ");

                String valor ;

                    ColorFilter filter = new LightingColorFilter(Color.YELLOW, (int) (100 * 255));
                    lampara.setColorFilter(filter);
                    valor = "encendido";

                String clave = "Deposito_Lampara";
                emisor.enviarDatos(clave, valor);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("toggleButtonStated", isChecked);
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

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","E_Luces_Principales");
                subscribeToTopic("A_Luces_Principales ");

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
                mediaPlayer = MediaPlayer.create(deposito.this, songId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No es necesario implementar esto
            }
        });

        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String valor ;
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
                String clave = "Deposito_altavos";
                emisor.enviarDatos(clave, valor);
            }
        });
        pausaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });
        toggleButton_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("GymDemo","E_Ventilador");
                subscribeToTopic("A_Ventilador");


                String valor ;
                if (isChecked) {
                    valor = "apagado";
                } else {
                    valor = "encendido";
                }
                String clave = "Deposito_ventana";
                emisor.enviarDatos(clave, valor);
            }
        });
        regresarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(deposito.this, menu.class);
                startActivity(intent);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
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
