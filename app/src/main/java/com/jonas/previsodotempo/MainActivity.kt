package com.jonas.previsodotempo

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.jonas.previsodotempo.constantes.Const
import com.jonas.previsodotempo.databinding.ActivityMainBinding
import com.jonas.previsodotempo.model.Main
import com.jonas.previsodotempo.service.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.containerPrincipal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgClima.setBackgroundResource(R.drawable.mist)

        binding.btBuscar.setOnClickListener {
            val cidade = binding.editBuscarCidade.text.toString()

            binding.progressbar.visibility = View.VISIBLE

            val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/").build().create(Api::class.java)

            retrofit.weatherMap(cidade, Const.API_KEY).enqueue(object : Callback<Main> {
                override fun onResponse(call: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful) {
                        respostaServidor(response)
                    }else{
                        Toast.makeText(applicationContext, "Cidade inválida!", Toast.LENGTH_SHORT)
                            .show()
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Main>, t: Throwable) {
                    Toast.makeText(applicationContext, "Erro fatal de servidor!", Toast.LENGTH_SHORT)
                        .show()
                    binding.progressbar.visibility = View.GONE
                }

            })
        }



        binding.trocarTema.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){//Tema Escuro - Dark mode
                binding.containerPrincipal.setBackgroundResource(R.drawable.fondo_preto_degrade)
                binding.conteinerInfo.setBackgroundResource(R.drawable.fondo_preto_degrade1)
                binding.txtInformaEs.setTextColor(Color.WHITE)
                binding.txtInformaEs1.setTextColor(Color.WHITE)
                binding.txtInformaEs2.setTextColor(Color.WHITE)
                binding.btBuscar.setBackgroundColor(R.drawable.container_info_tema_escuro)

            }else{// Tema Claro - Light mode
                binding.containerPrincipal.setBackgroundResource(R.drawable.fondo_degradado)
                binding.conteinerInfo.setBackgroundResource(R.drawable.fondo_degradado1)
                binding.txtInformaEs.setTextColor(Color.WHITE)
                binding.txtInformaEs1.setTextColor(Color.WHITE)
                binding.txtInformaEs2.setTextColor(Color.WHITE)

            }



        }
    }

    override fun onResume() {
        super.onResume()

    }

    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>) {
        val main = response.body()!!.main
        val temp = main.get("temp").toString()
        val tempMin = main.get("temp_min").toString()
        val tempMax = main.get("temp_max").toString()
        val humidity = main.get("humidity").toString()

        val sys = response.body()!!.sys
        val country = sys.get("country").asString
        var pais = ""

        val weather = response.body()!!.weather
        val main_weather = weather[0].main
        val description = weather[0].description

        val name = response.body()!!.name

        //Converter Kelvin em Graus Celsius
        val tempCelsius = (temp.toDouble() - 273.15)
        val tempMinCelsius = (tempMin.toDouble() - 273.15)
        val tempMaxCelsius = (tempMax.toDouble() - 273.15)
        val decimalFormat = DecimalFormat("00")


        if (country.equals("BR")){
            pais = "Brasil"
        }else if (country.equals("US")){
            pais = "Estados Unidos"
        }else if (country.equals("MX")){
            pais = "México"
        }else if (country.equals("AR")){
            pais = "Argentina"
        }


        if (main_weather == "Clouds" && description == "few clouds") {
            binding.imgClima.setBackgroundResource(R.drawable.fewcloud)
        }else if (main_weather == "Clouds" && description == "scattered clouds") {
            binding.imgClima.setBackgroundResource(R.drawable.cloud)
        }else if (main_weather == "Clouds" && description == "broken clouds") {
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        } else if (main_weather == "Clouds" && description == "overcast clouds") {
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        } else if (main_weather == "Clear" && description == "clear sky") {
            binding.imgClima.setBackgroundResource(R.drawable.clear_sun)
        } else if (main_weather == "Snow") {
            binding.imgClima.setBackgroundResource(R.drawable.snow)
        } else if (main_weather == "Thunderstorm") {
            binding.imgClima.setBackgroundResource(R.drawable.broken_rain)
        } else if (main_weather == "Drizzle") {
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather == "Rain") {
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather == "Mist") {
            binding.imgClima.setBackgroundResource(R.drawable.mist)
        }


        val descricaoClima = when(description){
            "clear sky" -> {
                "Céu limpo"
            }
            "few clouds" -> {
                "Poucas nuvens"
            }
            "scattered clouds" -> {
                "Nuvens dispersas"
            }
            "broken clouds" -> {
                "Nuvens quebradas"
            }
            "shower rain" -> {
                "Chuva"
            }
            "rain" -> {
                "Chuva"
            }
            "thunderstorm" -> {
                "Trovoada"
            }
            "snow" -> {
                "Neve"
            }
            else -> {
                "Névoa"
            }
        }


        binding.txtTemperatura.text = "${decimalFormat.format(tempCelsius)} °C"
        binding.txtPaisCidade.text = "$pais - $name"

        binding.txtInformaEs1.text = "Clima \n $descricaoClima \n\n Umidade \n $humidity"
        binding.txtInformaEs2.text = "Temp.Min \n ${decimalFormat.format(tempMinCelsius)} \n\n Temp.Max \n ${decimalFormat.format(tempMaxCelsius)}"

        binding.progressbar.visibility = View.GONE
    }
}