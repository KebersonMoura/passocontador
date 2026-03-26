package com.example.passocontador

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    private var passos = 0
    private var ultimoValor = 0f
    private val limite = 10f

    private var meta = 0

    private lateinit var txtPassos: TextView
    private lateinit var txtStatus: TextView
    private lateinit var inputMeta: EditText
    private lateinit var btnSalvarMeta: Button

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtPassos = findViewById(R.id.txtPassos)
        txtStatus = findViewById(R.id.txtStatus)
        inputMeta = findViewById(R.id.inputMeta)
        btnSalvarMeta = findViewById(R.id.btnSalvarMeta)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnSalvarMeta.setOnClickListener {
            val valor = inputMeta.text.toString()
            if (valor.isNotEmpty()) {
                meta = valor.toInt()
                Toast.makeText(this, "Meta definida: $meta passos", Toast.LENGTH_SHORT).show()
            }
        }

        solicitarPermissaoGPS()
    }

    private fun solicitarPermissaoGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            obterLocalizacao()
        }
    }

    private fun obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            val location: Location? =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            location?.let {
                Toast.makeText(this, "Localização ativa!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val diferenca = magnitude - ultimoValor
            ultimoValor = magnitude

            if (diferenca > limite) {
                passos++
                txtPassos.text = "Passos: $passos"

                if (meta > 0 && passos >= meta) {
                    txtStatus.text = "🎉 Parabéns você chegou lá!"
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}