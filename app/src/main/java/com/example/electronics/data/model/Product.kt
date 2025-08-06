package com.example.electronics.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val title: String,
    val image: String,
    val price: Int,
    val description: String,
    val brand: String,
    val model: String,
    val color: String,
    val category: String,
    val discount: Int,
    val popular: Boolean? = false,
    val onSale: Boolean? = false,
    var isFavorite: Boolean = false
): Parcelable

@Parcelize
data class Rating(
    val rate: Double,
    val count: Int
): Parcelable
