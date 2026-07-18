package com.mdi2.weatherapp.data

data class FeedbackRequest (
    val city: String,
    val rating: Int,
    val comment: String,
)