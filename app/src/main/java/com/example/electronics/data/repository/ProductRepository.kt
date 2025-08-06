package com.example.electronics.data.repository
import com.example.electronics.data.api.RetrofitInstance

import com.example.electronics.data.model.Product
import com.example.electronics.data.model.ProductResponse
import retrofit2.Response

class ProductRepository {
        suspend fun getAllProducts() : Response<ProductResponse>{
            return RetrofitInstance.api.getAllProducts()
        }
        suspend fun getProductById(id: Int) : Response<Product>{
            return RetrofitInstance.api.getProductById(id)
        }
    }

