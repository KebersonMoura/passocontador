package com.example.passocontador

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
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
    private lateinit var txtMeta: TextView
    private lateinit var txtRestante: TextView
    private lateinit var txtStatus: TextView
    private lateinit var inputMeta: EditText
    private lateinit var btnSalvarMeta: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtPassos = findViewById(R.id.txtPassos)
        txtMeta = findViewById(R.id.txtMeta)
        txtRestante = findViewById(R.id.txtRestante)
        txtStatus = findViewById(R.id.txtStatus)
        inputMeta = findViewById(R.id.inputMeta)
        btnSalvarMeta = findViewById(R.id.btnSalvarMeta)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        btnSalvarMeta.setOnClickListener {
            val valor = inputMeta.text.toString()
            if (valor.isNotEmpty()) {
                meta = valor.toInt()
                txtMeta.text = "Meta definida: $meta passos"
                atualizarRestante()
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
            2000,
            1f,
            this
        )
    }

    override fun onLocationChanged(location: Location) {

        if (ultimaLocalizacao != null) {
            val distancia = ultimaLocalizacao!!.distanceTo(location)
            distanciaTotal += distancia

            passos = (distanciaTotal / 0.75).toInt()

            txtPassos.text = "Passos: $passos"

            atualizarRestante()

            if (meta > 0 && passos >= meta) {
                txtStatus.text = "🎉 Parabéns você chegou lá!"
            }
        }

        ultimaLocalizacao = location
    }

    private fun atualizarRestante() {
        if (meta > 0) {
            val restante = meta - passos
            if (restante > 0) {
                txtRestante.text = "Faltam: $restante passos"
            } else {
                txtRestante.text = "Meta atingida!"
            }
        }
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}