// FavFragment.kt
package com.example.electronics

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electronics.data.model.Product
import com.example.electronics.databinding.FragmentFavBinding
import com.example.electronics.ui.adapter.ProductAdapter
import com.example.electronics.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavFragment : Fragment() {
    private var _binding: FragmentFavBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by activityViewModels()
    private lateinit var favoritesAdapter: ProductAdapter
    private var allFavorites: List<Product> = emptyList()
    private var searchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        binding.recyclerFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        
        favoritesAdapter = ProductAdapter(
            emptyList(),
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product)
            },
            onProductClick = { product ->
                findNavController().navigate(R.id.productDetailFragment, bundleOf("productId" to product.id))
            }
        )
        
        binding.recyclerFavorites.adapter = favoritesAdapter
    }

    private fun setupSearchBar() {
        binding.etSearchFavorites.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                filterAndDisplayFavorites()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteList.collectLatest { favorites ->
                allFavorites = favorites
                filterAndDisplayFavorites()
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

    private fun filterAndDisplayFavorites() {
        val filtered = allFavorites.filter { product ->
            searchText.isBlank() ||
            product.title.contains(searchText, ignoreCase = true) ||
            (product.brand?.contains(searchText, ignoreCase = true) ?: false)
        }

        favoritesAdapter.updateData(filtered)
        
        if (filtered.isEmpty()) {
            if (allFavorites.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerFavorites.visibility = View.GONE
                binding.tvFavoritesSubtitle.text = "Your saved items"
            } else {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerFavorites.visibility = View.GONE
                binding.tvFavoritesSubtitle.text = "No results for '$searchText'"
            }
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerFavorites.visibility = View.VISIBLE
            binding.tvFavoritesSubtitle.text = "${filtered.size} favorite items"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
