package com.example.electronics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronics.data.model.Product
import com.example.electronics.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

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

                    val allProducts = mutableListOf<Product>()
                    productResponseList.forEach { productResponse ->
                        if (productResponse.products.isNotEmpty()) {
                            val validProducts = productResponse.products.filter { product ->
                                product.title.isNotBlank() &&
                                        product.image.isNotBlank() &&
                                        product.description.isNotBlank() &&
                                        product.category.isNotBlank()
                            }
                            allProducts.addAll(validProducts)
                        }
                    }

                    val productsWithFavoriteState = allProducts.map { it.copy(isFavorite = false) }
                    _productList.value = productsWithFavoriteState
                } else {
                    _error.value = "Failed to fetch products: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
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
                val response = repository.getProductById(productId)
                if (response.isSuccessful) {
                    _selectedProduct.value = response.body()
                } else {
                    _error.value = "Failed to fetch product: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
