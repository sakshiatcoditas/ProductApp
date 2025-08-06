package com.example.electronics.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronics.data.model.Product
import com.example.electronics.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> get() = _productList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _favoriteList = MutableStateFlow<List<Product>>(emptyList())
    val favoriteList: StateFlow<List<Product>> get() = _favoriteList

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("ProductViewModel", "Starting API call to fetch products...")
                Log.d("ProductViewModel", "API URL: https://fakestoreapi.in/api/products")
                
                val response = repository.getAllProducts()
                
                Log.d("ProductViewModel", "API Response received - Success: ${response.isSuccessful}, Code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    val products = productResponse?.products ?: emptyList()
                    
                    Log.d("ProductViewModel", "API Status: ${productResponse?.status}")
                    Log.d("ProductViewModel", "API Message: ${productResponse?.message}")
                    Log.d("ProductViewModel", "Raw API response contains ${products.size} products")
                    
                    // Log first few products for debugging
                    products.take(3).forEachIndexed { index, product ->
                        Log.d("ProductViewModel", "Product $index: ID=${product.id}, Title=${product.title}, Category=${product.category}, Brand=${product.brand}")
                        Log.d("ProductViewModel", "Product $index Image URL: ${product.image}")
                        
                        // Test if the image URL is valid
                        if (product.image.isNotEmpty()) {
                            Log.d("ProductViewModel", "Image URL is not empty")
                            if (product.image.startsWith("http")) {
                                Log.d("ProductViewModel", "Image URL is absolute: ${product.image}")
                            } else {
                                Log.d("ProductViewModel", "Image URL is relative: ${product.image}")
                            }
                        } else {
                            Log.w("ProductViewModel", "Product $index has empty image URL")
                        }
                    }
                    
                    _productList.value = products
                    Log.d("ProductViewModel", "Fetched ${products.size} products successfully")
                } else {
                    _error.value = "Failed to fetch products: ${response.code()}"
                    Log.e("ProductViewModel", "API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                Log.e("ProductViewModel", "Exception during API call", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
        product.isFavorite = !product.isFavorite
        // Trigger update by reassigning the list
        _productList.value = _productList.value.map {
            if (it.id == product.id) product else it
        }
        updateFavorites()
    }

    private fun updateFavorites() {
        _favoriteList.value = _productList.value.filter { it.isFavorite }
    }

    fun clearError() {
        _error.value = null
    }
}


