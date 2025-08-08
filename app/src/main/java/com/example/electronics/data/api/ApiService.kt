package com.example.electronics.data.api
import com.example.electronics.data.model.Product
import com.example.electronics.data.model.ProductResponse
import com.example.electronics.data.model.SingleProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<SingleProductResponse>

    @GET("products")
    suspend fun getAllProducts(): Response<ProductResponse>
}