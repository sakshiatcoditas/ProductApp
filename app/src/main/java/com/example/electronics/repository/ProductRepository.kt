package com.example.electronics.repository

import com.example.electronics.data.api.ApiService
import com.example.electronics.data.model.Product
import com.example.electronics.data.model.ProductResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllProducts() : Response<List<ProductResponse>>{
        return apiService.getAllProducts()
    }
    suspend fun getProductById(id: Int): Response<Product> {
        return apiService.getProductById(id)
    }
}

