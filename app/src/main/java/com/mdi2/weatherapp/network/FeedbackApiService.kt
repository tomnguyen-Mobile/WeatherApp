package com.mdi2.weatherapp.network

import com.mdi2.weatherapp.data.FeedbackRequest
import com.mdi2.weatherapp.data.FeedbackResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApiService {

    @POST("feedback")
    suspend fun submitFeedback(
        @Body request: FeedbackRequest
    ): Response<FeedbackResponse>
}