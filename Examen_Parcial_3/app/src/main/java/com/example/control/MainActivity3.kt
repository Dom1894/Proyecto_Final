package com.example.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import java.util.*
import com.example.control.Constants
import java.io.File

class MainActivity3 : AppCompatActivity(), SensorEventListener {
    val requestCode = Constants.REQUEST_ENABLE_BT
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var outputStream: OutputStream
    private lateinit var mensajeTextView: TextView
    private lateinit var circleImageView: ImageView
    private var handler: Handler = Handler()
    private val colorChangeDelay: Long = 500 // Retraso de 500 milisegundos
    private val DIRECCION_ENFRENTE = 1
    private val DIRECCION_ATRAS = 2
    private val DIRECCION_IZQUIERDA = 3
    private val DIRECCION_DERECHA = 4
    lateinit var mBtAdapter: BluetoothAdapter
    var mAddressDevices: ArrayAdapter<String>? = null
    var mNameDevices: ArrayAdapter<String>? = null
    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var m_bluetoothSocket: BluetoothSocket? = null

        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //////////////
        val seleccionar_musica = findViewById<Button>(R.id.seleccionar_musica)
        seleccionar_musica.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            someActivityResultLauncher.launch(intent)
        }
        /////////////
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mAddressDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        val idBtnOnBT = findViewById<Button>(R.id.idBtnOnBT)
        val idBtnOffBT = findViewById<Button>(R.id.idBtnOffBT)
        val idBtnConect = findViewById<Button>(R.id.idBtnConect)
        val idBtnDispBT = findViewById<Button>(R.id.idBtnDispBT)
        val idSpinDisp = findViewById<Spinner>(R.id.idSpinDisp)
        circleImageView = findViewById(R.id.circleImageView)
        mensajeTextView = findViewById(R.id.mensajeTextView)
        idBtnOnBT.setOnClickListener {
            if (mBtAdapter.isEnabled) {
                //Si ya está activado
                Toast.makeText(this, "El Bluetooth ya esta activado", Toast.LENGTH_LONG).show()
            } else {
                //Encender Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("MainActivity", "ActivityCompat#requestPermissions")
                }
                someActivityResultLauncher.launch(enableBtIntent)
            }
        }
        idBtnOffBT.setOnClickListener {
            if (!mBtAdapter.isEnabled) {
                //Si ya está desactivado
                Toast.makeText(this, "El Bluetooth ya Esta desactivado", Toast.LENGTH_LONG).show()
            } else {
                //Encender Bluetooth
                mBtAdapter.disable()
                Toast.makeText(this, "Bluetooth Desactivado", Toast.LENGTH_LONG).show()
            }
        }
        idBtnDispBT.setOnClickListener {
            if (mBtAdapter.isEnabled) {

                val pairedDevices: Set<BluetoothDevice>? = mBtAdapter?.bondedDevices
                mAddressDevices!!.clear()
                mNameDevices!!.clear()

                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mAddressDevices!!.add(deviceHardwareAddress)
                    //........... EN ESTE PUNTO GUARDO LOS NOMBRE A MOSTRARSE EN EL COMBO BOX
                    mNameDevices!!.add(deviceName)
                }
                idSpinDisp.setAdapter(mNameDevices)
            } else {
                val noDevices = "Ningun dispositivo pudo ser emparejado"
                mAddressDevices!!.add(noDevices)
                mNameDevices!!.add(noDevices)
                Toast.makeText(this, "Vincule su bluetooth", Toast.LENGTH_LONG).show()
            }
        }
        idBtnConect.setOnClickListener {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {

                    val IntValSpin = idSpinDisp.selectedItemPosition
                    m_address = mAddressDevices!!.getItem(IntValSpin).toString()
                    Toast.makeText(this,m_address,Toast.LENGTH_LONG).show()
                    // Cancel discovery because it otherwise slows down the connection.
                    mBtAdapter?.cancelDiscovery()
                    val device: BluetoothDevice = mBtAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }
                Toast.makeText(this,"CONEXION EXITOSA",Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "CONEXION EXITOSA")
            } catch (e: IOException) {
                //connectSuccess = false
                e.printStackTrace()
                Toast.makeText(this,"ERROR DE CONEXION",Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "ERROR DE CONEXION")
            }
        }
        val someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == REQUEST_ENABLE_BT) {
                Log.i("MainActivity", "ACTIVIDAD REGISTRADA")
            } else if (result.resultCode == RESULT_OK && result.data != null) {
                val uri = result.data!!.data
                if (uri != null) {
                    val path = uri.path  // Obtener la ruta de la canción seleccionada
                    // Aquí deberías implementar la lógica para enviar el archivo de audio por Bluetooth y reproducir en la bocina.
                    // Puedes usar la variable 'path' para obtener la ruta del archivo de audio y luego enviarlo por Bluetooth.
                    val file = File(path)
                    val filename = file.name
                    val outputStream = m_bluetoothSocket!!.outputStream
                    val inputStream: InputStream = contentResolver.openInputStream(uri)!!
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                    inputStream.close()
                    Toast.makeText(this, "Canción enviada: $filename", Toast.LENGTH_LONG).show()
                }
            }
        }

        mBtAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth no disponible en este dipositivo", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Bluetooth disponible en este dispositivo", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x: Float = event!!.values[0]
        val y: Float = event!!.values[1]
        val z: Float = event!!.values[2]
        val maskRotation = when {
            x > 1.0 -> 0f
            x < -1.0 -> 180f
            y > 1.0 -> 90f
            y < -1.0 -> -90f
            else -> 0f
        }
        circleImageView.rotation = maskRotation
        // Determinar la dirección según los valores del acelerómetro
        var direccion = DIRECCION_ENFRENTE
        if (x > 1.0) {
            mensajeTextView.text = "Izquierda"
            sendCommand("3")
        } else if (x < -1.0) {
            mensajeTextView.text = "Derecha"
            sendCommand("4")
        } else if (y > 1.0) {
            mensajeTextView.text = "Atrás"
            sendCommand("2")
        } else if (y < -1.0) {
            mensajeTextView.text = "Enfrente"
            sendCommand("1")
        } else {
            circleImageView.clearColorFilter()
            mensajeTextView.text = "Sin Movimiento"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor?.type == Sensor.TYPE_ACCELEROMETER) {
        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                val outputStream = m_bluetoothSocket!!.outputStream
                outputStream.write(input.toByteArray())
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
