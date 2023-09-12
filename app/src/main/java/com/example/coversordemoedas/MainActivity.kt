package com.example.coversordemoedas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.coversordemoedas.api.Endpoint
import com.example.coversordemoedas.util.NetworkUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.String.format

class MainActivity : AppCompatActivity() {

    private lateinit var spDe : Spinner
    private lateinit var spPara : Spinner
    private lateinit var valueInsert : TextInputEditText
    private lateinit var btnConverte : Button
    private lateinit var campoValorConvertido : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getIds()
        getCurrencies()

        btnConverte.setOnClickListener{convertMoney()}

    }

    fun getIds(){
        spDe = findViewById(R.id.SP_de)
        spPara = findViewById(R.id.SP_para)
        valueInsert = findViewById(R.id.ET_valueInsert)
        btnConverte = findViewById(R.id.Btn_converter)
        campoValorConvertido = findViewById(R.id.TV_valorconvertido)
    }

    fun convertMoney(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencyRate(spDe.selectedItem.toString(), spPara.selectedItem.toString()).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = response.body()?.entrySet()?.find { it.key == spPara.selectedItem.toString() }
                val rate : Double = data?.value.toString().toDouble()
                val conversion = valueInsert.text.toString().toDouble() * rate
                val conversionRounded = format("%.2f", conversion)
                campoValorConvertido.setText(conversionRounded.toString())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Não foi")
            }
        })
    }

    fun getCurrencies(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")

                val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                spPara.adapter = adapter
                spDe.adapter = adapter

                spPara.setSelection(posBRL)
                spDe.setSelection(posUSD)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Não foi")
            }

        })
    }
}