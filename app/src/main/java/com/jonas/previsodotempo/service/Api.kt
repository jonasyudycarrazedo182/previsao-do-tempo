package com.jonas.previsodotempo.service

import com.jonas.previsodotempo.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
//51da4d42a54fc51e61275e5b53dc67d9(A chave do API)

interface Api {
    @GET("weather")

    fun weatherMap(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
    ): Call<Main>

}