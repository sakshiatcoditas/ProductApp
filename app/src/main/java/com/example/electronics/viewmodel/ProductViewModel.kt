package com.example.electronics.viewmodel

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

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = repository.getAllProducts()

                if (response.isSuccessful) {
                    val productResponse = response.body()
                    val products = productResponse?.products ?: emptyList()

                    // Ensure all products start with isFavorite = false
                    val productsWithFavoriteState = products.map { it.copy(isFavorite = false) }
                    _productList.value = productsWithFavoriteState
                } else {
                    _error.value = "Failed to fetch products: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
        // Find the current product in the list and get its current favorite state
        val currentProduct = _productList.value.find { it.id == product.id }
        val currentFavoriteState = currentProduct?.isFavorite ?: false
        val newFavoriteState = !currentFavoriteState
        
        // Update the product in the main list
        val updatedList = _productList.value.map { existingProduct -> 
            if (existingProduct.id == product.id) {
                existingProduct.copy(isFavorite = newFavoriteState)
            } else {
                existingProduct
            }
        }
        _productList.value = updatedList
        
        // Update favorites list
        updateFavorites()
    }

    private fun updateFavorites() {
        val favorites = _productList.value.filter { it.isFavorite }
        _favoriteList.value = favorites
    }

    fun clearError() {
        _error.value = null
    }

    fun fetchProductById(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getProductById(productId)
                if (response.isSuccessful) {
                    _selectedProduct.value = response.body()?.product
                } else {
                    _error.value = "Failed to fetch product: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}


