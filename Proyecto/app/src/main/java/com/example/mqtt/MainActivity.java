package com.example.mqtt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Button H;
    private static final String BROKER_URL = "tcp://18.118.188.12";
    private static final String CLIENT_ID = "your_client_id";
    private MqttHandler mqttHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////
        Button H = findViewById(R.id.H);
        H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, menu.class);
                startActivity(intent);
            }
        });
        ////////////////////////////////
        Button publishButton = findViewById(R.id.publishButton);
        Button publishButton2 = findViewById(R.id.publishButton2);

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("DEMO","P_P_A");

            }
        });

        publishButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mqttHandler = new MqttHandler();
                mqttHandler.connect(BROKER_URL,CLIENT_ID);
                publishMessage("DEMO","F_PO_A");
                subscribeToTopic("F_PO_A");

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