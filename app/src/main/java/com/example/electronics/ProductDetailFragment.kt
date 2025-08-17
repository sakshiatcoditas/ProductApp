package com.example.electronics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.electronics.data.model.Product
import com.example.electronics.databinding.ProductDetailBinding
import com.example.electronics.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by activityViewModels()
    private var currentProduct: Product? = null
    private var quantity: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        val productId = arguments?.getInt("productId") ?: -1
        println("DEBUG: ProductDetailFragment created with productId: $productId")
        
        if (productId == -1) {
            println("DEBUG: ERROR - No productId received!")
            // Show error or navigate back
            findNavController().navigateUp()
            return
        }
        
        // Try to get product from the already loaded list first
        val productFromList = viewModel.getProductFromList(productId)
        if (productFromList != null) {

            viewModel.setSelectedProduct(productFromList)
        } else {

            viewModel.fetchProductById(productId)
        }
        
        observeProductDetails()
    }

    private fun observeProductDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedProduct.collectLatest { product ->
                println("DEBUG: ProductDetailFragment received product: ${product?.title ?: "null"}")
                if (product == null) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    displayProductDetails(product)
                }
            }
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityDisplay()
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            updateQuantityDisplay()
        }

        binding.btnOrder.setOnClickListener {

        }
    }

    private fun displayProductDetails(product: Product) {
        val imageUrl = if (product.image.startsWith("http")) {
            product.image
        } else {
            "https://fakestoreapi.in${product.image}"
        }

        Glide.with(requireContext())
            .load(imageUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .timeout(15000))
            .into(binding.imgProduct)

        binding.tvProductName.text = product.title
        binding.tvProductBrand.text = product.brand ?: "Unknown Brand"
        binding.tvProductmodel.text = product.model ?: "N/A"
        binding.tvCategory.text = product.category
        binding.tvColor.text = product.color ?: "N/A"
        binding.tvDescription.text = product.description
        binding.tvDiscount.text = "${product.discount}% OFF"
        binding.tvPrice.text = "$${product.price}"

        updateQuantityDisplay()
        currentProduct = product
    }

    private fun updateQuantityDisplay() {
        binding.tvQuantity.text = quantity.toString()
        currentProduct?.let { product ->
            val totalPrice = product.price * quantity
            binding.tvPrice.text = "$${totalPrice}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}