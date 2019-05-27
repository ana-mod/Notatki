package com.example.notes


data class Quote(
    val author: String,
    val category: String,
    val date: String,
    val id: Any,
    val length: String,
    val quote: String,
    val tags: List<String>,
    val title: String
)