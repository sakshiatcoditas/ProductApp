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
                    val productResponseList = response.body() ?: emptyList()
                    println("DEBUG: Successfully fetched ${productResponseList.size} response objects")
                    
                    // Debug: Print the first response structure
                    if (productResponseList.isNotEmpty()) {
                        val firstResponse = productResponseList[0]
                        println("DEBUG: First response - status: ${firstResponse.status}, message: ${firstResponse.message}, products count: ${firstResponse.products.size}")
                        
                        if (firstResponse.products.isNotEmpty()) {
                            val firstProduct = firstResponse.products[0]
                            println("DEBUG: First product - id: ${firstProduct.id}, title: ${firstProduct.title}, category: ${firstProduct.category}")
                        }
                    }
                    
                    // Extract products from the wrapped response
                    val allProducts = mutableListOf<Product>()
                    productResponseList.forEach { productResponse ->
                        if (productResponse.products.isNotEmpty()) {
                            // Filter out products with null required fields
                            val validProducts = productResponse.products.filter { product ->
                                product.title.isNotBlank() && 
                                product.image.isNotBlank() && 
                                product.description.isNotBlank() &&
                                product.category.isNotBlank()
                            }
                            allProducts.addAll(validProducts)
                        }
                    }
                    
                    println("DEBUG: Total products extracted: ${allProducts.size}")
                    
                    // Ensure all products start with isFavorite = false
                    val productsWithFavoriteState = allProducts.map { it.copy(isFavorite = false) }
                    _productList.value = productsWithFavoriteState
                } else {
                    val errorMsg = "Failed to fetch products: ${response.code()} - ${response.message()}"
                    println("DEBUG: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.message}"
                println("DEBUG: $errorMsg")
                e.printStackTrace()
                _error.value = errorMsg
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
        

        val updatedList = _productList.value.map { existingProduct -> 
            if (existingProduct.id == product.id) {
                existingProduct.copy(isFavorite = newFavoriteState)
            } else {
                existingProduct
            }
        }
        _productList.value = updatedList

        updateFavorites()
    }

    private fun updateFavorites() {
        val favorites = _productList.value.filter { it.isFavorite }
        _favoriteList.value = favorites
    }

    fun clearError() {
        _error.value = null
    }

    fun getProductFromList(productId: Int): Product? {
        return _productList.value.find { it.id == productId }
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
    }

    fun fetchProductById(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("DEBUG: Fetching product with ID: $productId")
                val response = repository.getProductById(productId)
                if (response.isSuccessful) {
                    val product = response.body()
                    println("DEBUG: Successfully fetched product: ${product?.title}")
                    _selectedProduct.value = product
                } else {
                    val errorMsg = "Failed to fetch product: ${response.code()} - ${response.message()}"
                    println("DEBUG: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.message}"
                println("DEBUG: $errorMsg")
                e.printStackTrace()
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }
}


