package com.mdi2.weatherapp

// Assignment 1 imports

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdi2.weatherapp.data.FeedbackRequest
import com.mdi2.weatherapp.network.AppConstants
import com.mdi2.weatherapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this)
        setContent {
           MaterialTheme{
               Surface(modifier = Modifier.fillMaxSize()){
                   WeatherScreen()
               }
           }
       }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(){
    var city by remember { mutableStateOf("") }
    var dateResult by remember { mutableStateOf("Date: --") }
    var cityResult by remember { mutableStateOf("City: --") }
    var tempResult by remember { mutableStateOf("Temperature: --") }
    var descResult by remember { mutableStateOf("Description: --") }
    var windResult by remember { mutableStateOf("Wind Speed: --") }
    var humidityResult by remember { mutableStateOf("HumidityResult: --") }
    var isLoading by remember { mutableStateOf(false) }
    var currentCity by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var comment by remember { mutableStateOf("") }
    var feedbackResult by remember { mutableStateOf("") }
    var feedbackLoading by remember { mutableStateOf(false) }
    var debug_text: String=""

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Scaffold(   // my emulator phone has a camera on top this help adjust it
        topBar={TopAppBar(title={Text("Weather App")})}
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)){
            TextField(
                value=city,
                onValueChange={ city=it },
                label = { Text("Enter city name:") },
                modifier = Modifier.fillMaxWidth()
            ) // end of text field
            Button(
                enabled = !isLoading, // when the button clicks it will turn isLoading to true
                onClick = {
                    val trimmedCity:String= city.trim()
                    if(trimmedCity.isEmpty()){
                        Toast.makeText(
                            context,
                            "Please enter a city name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        isLoading = true
                        // Assignment 1 -- implement real weather fetch here
                        // Steps to complete:
                        // 1. Add these imports at the top of the file: rememberCoroutineScope,
                        //    kotlinx.coroutines.Dispatchers/launch/withContext, and the
                        //    AppConstants / RetrofitClient imports from the network package
                        // 2. Add "val scope = rememberCoroutineScope()" near the top of WeatherScreen(),
                        //    next to where "val context = LocalContext.current" already is
                        // 3. Replace the Toast line below with "scope.launch { }"
                        scope.launch{
                            // 8. Wrap steps 4-7 in try { } catch (e: Exception) { }, showing
                            try {
                                // 4. Inside launch, use "withContext(Dispatchers.IO) { }" for the network call
                                val response = withContext((Dispatchers.IO)){
                                    // 5. Call RetrofitClient.weatherApiService.getWeather(trimmedCity, AppConstants.API_KEY, AppConstants.UNITS)
                                    RetrofitClient.weatherApiService.getWeather(
                                        city = trimmedCity,
                                        api = AppConstants.API_KEY,
                                        units = AppConstants.UNITS
                                    )
                                }   // end of withContext
                                Log.d("WeatherApp", "Request URL: ${response.raw().request.url}")
                                Log.d("WeatherApp", "Request Code: ${response.code()}")
                                // 6. If response.isSuccessful, update cityResult / tempResult / descResult
                                val weatherResponse = response.body() // if this is null
                                if(response.isSuccessful && weatherResponse != null){
                                    cityResult = "City: ${weatherResponse.cityName}"
                                    tempResult = "Temperature: ${weatherResponse.main.temp}°F"
                                    descResult = "Description: ${weatherResponse.weather[0].description}"
                                    windResult = "Wind Speed: ${weatherResponse.wind.speed} m/s"
                                    humidityResult = "HumidityResult: ${weatherResponse.main.humidity} %"
                                    currentCity = trimmedCity
                                    val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                                    val date = formatter.format(Date(weatherResponse.dt * 1000))
                                    dateResult = date
                                    debug_text = response.body().toString()
                                }else{
                                    // replace with response.code() either 404, 401, or anything else
                                    if(response.code()==401){
                                        Toast.makeText(context, "Unauthorized, please check your API key.", Toast.LENGTH_SHORT).show()
                                    } else if (response.code() == 404){
                                        // 7. If NOT successful, show a Toast: "City not found. Check the name and try again."
                                        Toast.makeText(context, "City not found. Check the name and try again", Toast.LENGTH_SHORT).show()
                                    } else{
                                        Toast.makeText(context, "Network error. Check your connection.", Toast.LENGTH_SHORT).show()

                                    }
                                } // end of if-else statement
                            } catch(e: Exception) {
                                // 8.1   "Network error. Check your connection." in the catch block
                                Toast.makeText(
                                    context,
                                    "Network error. Check your connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }// end of try-catch block
                            finally{
                                isLoading = false
                            }// end of finally
                        } // end of scope
                    }//end of if-else-statement
                },
                modifier = Modifier.fillMaxWidth().padding(top=8.dp),
            ){
                Text(if(isLoading) "Loading..." else "Get Weather")
            } // end of button
            if (isLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            Text(dateResult, fontSize=20.sp, modifier=Modifier.padding(top=24.dp))
            Text(cityResult, fontSize=20.sp, modifier=Modifier.padding(top=8.dp))
            Text(tempResult, fontSize=20.sp, modifier=Modifier.padding(top=8.dp))
            Text(descResult, fontSize=20.sp, modifier=Modifier.padding(top=8.dp))
            Text(windResult, fontSize=20.sp, modifier=Modifier.padding(top=8.dp))
            Text(humidityResult, fontSize=20.sp, modifier=Modifier.padding(top=8.dp))

            OutlinedButton(
                onClick={
                    RetrofitClient.clearCache()
                    Toast.makeText(context, "Cache Cleared", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top=8.dp),
            ){ Text("Clear cache")}
            HorizontalDivider(modifier = Modifier.padding(top=24.dp, bottom= 16.dp))
            Text(
                text="How do you feel about today's weather?",
                fontSize=20.sp,
                modifier=Modifier.padding(top=8.dp)
            )
//            Text(debug_text, fontSize=20.sp, modifier=Modifier.padding(top=8.dp))
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating=it.toInt()},
                valueRange = 1f..5f,
                steps = 3,
            )


            Text("Rating: $rating/5", fontSize=20.sp)

            TextField(
                value=comment,
                onValueChange = { comment=it },
                label={ Text("Leave a comment about the weather...")},
                minLines=3,
                modifier=Modifier.fillMaxWidth().padding(top=8.dp),

            )

            Button (
                onClick = {
                    if(currentCity.isEmpty()){
                        Toast.makeText(
                            context,
                            "Please fetch weather for a city first",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else if (comment.isBlank()){
                        Toast.makeText(
                            context,
                            "Please leave a comment",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else{
                        // ASSIGNMENT 4:
                        // 1. Declare " var feedback loading by remember { mutableStateOf(false) }
                        //    near the top of WeatherScreen(), next to the other state variables
                        // 2. Set feedback Loading = true right before the feedback scope.launch{}
                        feedbackLoading = true
                        // 3. Set feedbackLoading = false inside a finally { } wrapped around the
                        //    feedback try/catch -- same shape as Get Weather's finally block
                        // 4. On the Submit Feedback Button: add enable = !feedbackLoading
                        //    and swap its text to "Submitting..." while feedback Loading is true
                       scope.launch {
                           try {
                               val request = FeedbackRequest(
                                   city = currentCity,
                                    rating=rating,
                                   comment=comment
                                   )
                               val response = withContext(Dispatchers.IO){
                                   RetrofitClient.feedbackApiService.submitFeedback(request)
                               }
                               // assignment 3
                               // 1. if response.isSuccessful : set feedbackResult to a success message,
                               if(response.isSuccessful){
                                   feedbackResult = "Feedback submitted successfully!"
                                    //   then clear the comment field and reset rating back to 3
                                   comment = ""
                                   rating = 3
                               } else {
                                // 2. If NOT successful: set feedbackResult to a failure message
                                    feedbackResult = "Failed to submit feedback. Please try again."
                               }

                           } catch(e:Exception){
                               feedbackResult = "Error submitting feedback. check your connection."
                           } finally {
                               feedbackLoading = false
                           }
                       }
                    }
                },
                enabled = !feedbackLoading,
                modifier = Modifier.fillMaxWidth().padding(top=8.dp)
            ){
                Text(if(feedbackLoading) "Submitting..." else "Submit Feedback")
            }
            if (feedbackLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
                Text(feedbackResult, fontSize=14.sp, modifier= Modifier.padding(top=8.dp))
        }// end of Column
    }// end of scaffold

}