package com.example.passocontador

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    private var passos = 0
    private var ultimoValor = 0f
    private val limite = 10f
    private val meta = 5000

    private lateinit var txtPassos: TextView
    private lateinit var txtMeta: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtPassos = findViewById(R.id.txtPassos)
        txtMeta = findViewById(R.id.txtMeta)
        progressBar = findViewById(R.id.progressBar)
        btnReset = findViewById(R.id.btnReset)

        progressBar.max = meta

        btnReset.setOnClickListener {
            passos = 0
            atualizarUI()
        }

        atualizarUI()
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
                atualizarUI()
            }
        }
    }

    private fun atualizarUI() {
        txtPassos.text = "Passos: $passos"
        txtMeta.text = "Meta: $meta"
        progressBar.progress = passos
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}