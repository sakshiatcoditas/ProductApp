package com.example.electronics
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electronics.data.model.Product
import com.example.electronics.databinding.FragmentHomeBinding
import com.example.electronics.ui.adapter.ProductAdapter
import com.example.electronics.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private var allProducts: List<Product> = emptyList()
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


        
        // Use view binding instead of findViewById
        binding.recyclerProducts.layoutManager = GridLayoutManager(requireContext(), 2)

// Initialize adapter with empty list and favorite click callback
        productAdapter = ProductAdapter(emptyList()) { product ->
            viewModel.toggleFavorite(product)
        }
        binding.recyclerProducts.adapter = productAdapter
        
        // Add scroll listener to track product visibility
        binding.recyclerProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                
                Log.d("HomeFragment", "Scroll: Visible=$visibleItemCount, Total=$totalItemCount, First=$firstVisibleItemPosition")
            }
        })

        // Observe product list from ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productList.collectLatest { products ->
                Log.d("HomeFragment", "Received ${products.size} products from ViewModel")
                
                // Log first few products for debugging
                products.take(3).forEachIndexed { index, product ->
                    Log.d("HomeFragment", "Product $index: ${product.title} - ${product.category}")
                }
                
                productAdapter.updateData(products)
                
                if (products.isNotEmpty()) {
                    Log.d("HomeFragment", "First product: ${products.first().title}")
                    // Show a toast to confirm data is received
                    Toast.makeText(requireContext(), "Loaded ${products.size} products!", Toast.LENGTH_SHORT).show()
                    
                    // Log adapter item count
                    Log.d("HomeFragment", "Adapter item count: ${productAdapter.itemCount}")
                    
                    // Update the title to show product count
                    binding.tvTitle.text = "Electronics (${products.size} products)"
                } else {
                    Log.w("HomeFragment", "No products received from API")
                    Toast.makeText(requireContext(), "No products found - Check logs", Toast.LENGTH_LONG).show()
                    binding.tvTitle.text = "Electronics (0 products)"
                }
            }
        }

        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                Log.d("HomeFragment", "Loading state: $isLoading")
                // Show/hide progress bar
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                
                // Show loading toast
                if (isLoading) {
                    Toast.makeText(requireContext(), "Loading products...", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe error state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Log.e("HomeFragment", "Error: $it")
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }

        // Fetch products when fragment is created
        Log.d("HomeFragment", "Fetching products...")
        Toast.makeText(requireContext(), "Starting to fetch products...", Toast.LENGTH_SHORT).show()
        viewModel.fetchProducts()

        // Test API connectivity
        testApiConnectivity()
    }
    
    private fun testApiConnectivity() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("HomeFragment", "Testing API connectivity...")
                Toast.makeText(requireContext(), "Testing API connectivity...", Toast.LENGTH_SHORT).show()
                
                // Test a sample image URL from the API
                val testImageUrl = "https://storage.googleapis.com/fir-auth-1c3bc.appspot.com/1692947383286-714WUJlhbLS._SL1500_.jpg"
                Log.d("HomeFragment", "Testing Google Cloud Storage image URL: $testImageUrl")
                
                // Test the API endpoint
                val apiUrl = "https://fakestoreapi.in/api/products"
                Log.d("HomeFragment", "API URL: $apiUrl")
                
                Toast.makeText(requireContext(), "API test completed - Check logs", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                Log.e("HomeFragment", "API connectivity test failed: ${e.message}")
                Toast.makeText(requireContext(), "API test failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
