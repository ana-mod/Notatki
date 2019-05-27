package com.example.notes

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface QodAPI {
    @GET("/qod.json")
    fun findJoke() : Call<QuoteOfTheDay>
}