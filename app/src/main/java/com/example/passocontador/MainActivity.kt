package com.example.passocontador

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    private var ultimaLocalizacao: Location? = null
    private var distanciaTotal = 0f
    private var passos = 0
    private var meta = 0

    private lateinit var txtPassos: TextView
    private lateinit var txtStatus: TextView
    private lateinit var inputMeta: EditText
    private lateinit var btnSalvarMeta: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtPassos = findViewById(R.id.txtPassos)
        txtStatus = findViewById(R.id.txtStatus)
        inputMeta = findViewById(R.id.inputMeta)
        btnSalvarMeta = findViewById(R.id.btnSalvarMeta)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        btnSalvarMeta.setOnClickListener {
            val valor = inputMeta.text.toString()
            if (valor.isNotEmpty()) {
                meta = valor.toInt()
                Toast.makeText(this, "Meta definida: $meta passos", Toast.LENGTH_SHORT).show()
            }
        }

        iniciarGPS()
    }

    private fun iniciarGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000, // atualiza a cada 2 segundos
            1f,   // ou a cada 1 metro
            this
        )
    }

    override fun onLocationChanged(location: Location) {

        if (ultimaLocalizacao != null) {
            val distancia = ultimaLocalizacao!!.distanceTo(location)
            distanciaTotal += distancia

            // Converter distância para passos
            passos = (distanciaTotal / 0.75).toInt()

            txtPassos.text = "Passos: $passos"

            if (meta > 0 && passos >= meta) {
                txtStatus.text = "🎉 Parabéns você chegou lá!"
            }
        }

        ultimaLocalizacao = location
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}