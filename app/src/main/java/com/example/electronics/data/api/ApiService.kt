package com.example.electronics.data.api
import com.example.electronics.data.model.Product
import com.example.electronics.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("product")
    suspend fun getProductById(@Query("id")id:Int): Response<Product>

    @GET("products")
    suspend fun getAllProducts(): Response<ProductResponse>
}