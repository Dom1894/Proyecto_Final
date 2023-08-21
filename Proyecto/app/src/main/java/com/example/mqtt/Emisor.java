package com.example.mqtt;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Emisor {
        public void enviarDatos(String clave, String valor) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Socket socket = new Socket("dirección_ip_receptor", 33); // Reemplaza con la IP y puerto del receptor
                        OutputStream outputStream = socket.getOutputStream();

                        String mensaje = clave + "=" + valor;
                        outputStream.write(mensaje.getBytes());

                        outputStream.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }