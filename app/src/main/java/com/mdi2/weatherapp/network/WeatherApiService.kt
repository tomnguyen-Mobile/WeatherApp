package com.mdi2.weatherapp.network

import com.mdi2.weatherapp.data.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query // annotation for URL query parameters

interface WeatherApiService {
    @GET(value="data/2.5/weather")
    suspend fun getWeather( // wait for response instead of freezing
        @Query(value="q") city:String,
        @Query(value="appid") api:String,
        @Query(value="units") units:String,
    ): Response<WeatherResponse>
}