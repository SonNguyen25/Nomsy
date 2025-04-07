package com.example.nomsy.models

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val instructions: String
)