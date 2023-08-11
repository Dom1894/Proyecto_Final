package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class activity_notificaciones extends AppCompatActivity {
    Button regresarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        regresarButton = findViewById(R.id.regresar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://luisappgim.000webhostapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<MyData>> call = apiService.getMyDataList();
        call.enqueue(new Callback<List<MyData>>() {
            @Override
            public void onResponse(Call<List<MyData>> call, Response<List<MyData>> response) {
                if (response.isSuccessful()) {
                    List<MyData> dataList = response.body();
                    setupRecyclerView(dataList); // Configurar el RecyclerView con los datos obtenidos
                } else {
                    int statusCode = response.code();

                    // Aquí puedes tomar medidas basadas en el código de estado
                    if (statusCode == 404) {
                        // La página no se encontró
                        Log.e("API Error", "Página no encontrada");
                        // Muestra un mensaje de error al usuario
                        Toast.makeText(activity_notificaciones.this, "Página no encontrada", Toast.LENGTH_SHORT).show();
                    } else if (statusCode == 500) {
                        // Error interno del servidor
                        Log.e("API Error", "Error interno del servidor");
                        // Muestra un mensaje de error al usuario
                        Toast.makeText(activity_notificaciones.this, "Error interno del servidor", Toast.LENGTH_SHORT).show();
                    } else {
                        // Otros códigos de error
                        Log.e("API Error", "Error en la respuesta: " + response.message());
                        // Muestra un mensaje de error al usuario
                        Toast.makeText(activity_notificaciones.this, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyData>> call, Throwable t) {
                Log.e("API Error", "Error en la solicitud: " + t.getMessage());

                Toast.makeText(activity_notificaciones.this, "Error en la solicitud a la API", Toast.LENGTH_SHORT).show();
            }
        });
        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_notificaciones.this, menu.class);
                startActivity(intent);

            }
        });
    }

    private void setupRecyclerView(List<MyData> dataList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }
}