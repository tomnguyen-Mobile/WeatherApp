package com.mdi2.weatherapp.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse (
    @SerializedName("name")
    val cityName: String,
    val main: Main, // nested object
    val weather: List<Weather>, // array of weather conditions
    val wind: Wind,
    val dt: Long // date time
)

data class Main(
    val temp: Double,   // temperature in Celsius
    val humidity: Int, // as percentage
)

data class Weather(
    val description: String, // example: clear sky, rain
)

data class Wind(
    val speed: Double   // wind speed in meters per second
)