package com.mdi2.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val weatherApiService: WeatherApiService by lazy{
        Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL) //prepends this to every @GET
            .addConverterFactory(GsonConverterFactory.create()) //JSON-to-data-class conversion
            .build() // API returns JSON, Gson maps it to weatherResponse fields
            .create(WeatherApiService::class.java) // generates the HTTP implementation
    }
}