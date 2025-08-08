package com.example.electronics
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electronics.data.model.Product
import com.example.electronics.databinding.FragmentHomeBinding
import com.example.electronics.ui.adapter.ProductAdapter
import com.example.electronics.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by activityViewModels()
    private var selectedCategory: String = "All"
    private var searchText: String = ""
    private var fullProductList: List<Product> = emptyList()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategoryButtons()
        setupSearchBar()
        observeData()
        viewModel.fetchProducts()
    }

    private fun setupRecyclerView() {
        binding.recyclerProducts.layoutManager = GridLayoutManager(requireContext(), 2)

        productAdapter = ProductAdapter(
            emptyList(),
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product)
            },
            onProductClick = { product ->
                findNavController().navigate(R.id.productDetailFragment, bundleOf("productId" to product.id))
            }
        )
        binding.recyclerProducts.adapter = productAdapter
    }

    private fun setupCategoryButtons() {
        val categoryButtons = listOf(
            binding.btnAll to "All",
            binding.btnCombos to "Gaming",
            binding.btnSliders to "Audio",
            binding.btnPhone to "Mobile",
            binding.btnTV to "TV"
        )

        fun updateButtonBackgrounds(selected: String) {
            categoryButtons.forEach { (button, category) ->
                if (selected == category) {
                    button.setBackgroundResource(R.drawable.bg_button_selected)
                } else {
                    button.setBackgroundResource(R.drawable.bg_button_unselected)
                }
            }
        }

        categoryButtons.forEach { (button, category) ->
            button.setOnClickListener {
                selectedCategory = category
                updateButtonBackgrounds(selectedCategory)
                filterAndDisplayProducts()
            }
        }
        updateButtonBackgrounds(selectedCategory)
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                filterAndDisplayProducts()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productList.collectLatest { products ->
                fullProductList = products
                filterAndDisplayProducts()
                binding.tvTitle.text = "Electronics (${products.size} products)"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    viewModel.clearError()
                }
            }
        }
    }

    private fun filterAndDisplayProducts() {
        val filtered = fullProductList.filter { product ->
            val matchesCategory =
                (selectedCategory == "All") || (product.category.equals(selectedCategory, ignoreCase = true))
            val matchesSearch =
                searchText.isBlank() ||
                product.title.contains(searchText, ignoreCase = true) ||
                (product.brand?.contains(searchText, ignoreCase = true) ?: false)
            matchesCategory && matchesSearch
        }
        productAdapter.updateData(filtered)
        binding.tvTitle.text = "Electronics (${filtered.size} products)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
