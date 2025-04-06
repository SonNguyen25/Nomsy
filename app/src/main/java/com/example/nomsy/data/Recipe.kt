package com.example.nomsy.data

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val instructions: String
)