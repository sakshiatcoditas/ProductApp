package com.example.electronics.data.model

data class ProductResponse(
    val status: String,
    val message: String,
    val products: List<Product>
) 