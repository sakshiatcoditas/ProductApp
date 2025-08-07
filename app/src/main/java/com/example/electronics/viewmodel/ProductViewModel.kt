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

                
                val response = repository.getAllProducts()
                

                
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    val products = productResponse?.products ?: emptyList()

                        // Test if the image URL is valid

                    _productList.value = products

                } else {
                    _error.value = "Failed to fetch products: ${response.code()}"

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


