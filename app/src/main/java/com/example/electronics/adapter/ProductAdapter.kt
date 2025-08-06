package com.example.electronics.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.electronics.R
import com.example.electronics.data.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onFavoriteClick: (Product) -> Unit // ✅ Callback
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductBrand: TextView = itemView.findViewById(R.id.tvProductBrand)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val imgFavorite: ImageView = itemView.findViewById(R.id.imgFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        Log.d("ProductAdapter", "Binding product at position $position: ${product.title}")
        Log.d("ProductAdapter", "Image URL: ${product.image}")

        holder.tvProductName.text = product.title
        holder.tvProductBrand.text = product.brand
        holder.tvRating.text = "${product.discount}% OFF"

        // Load image with comprehensive error handling
        val imageUrl = if (product.image.startsWith("http")) {
            product.image
        } else {
            // If it's a relative URL, try to make it absolute
            "https://fakestoreapi.in${product.image}"
        }
        
        Log.d("ProductAdapter", "Processing image URL: $imageUrl")
        
        // Try to load image with multiple fallback options
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .timeout(15000)) // 15 second timeout for cloud storage
            .into(holder.imgProduct)
            
        // Add a simple text indicator for debugging
        holder.tvProductName.text = "${product.title} (ID: ${product.id})"

        holder.imgFavorite.setImageResource(
            if (product.isFavorite) R.drawable.favlike else R.drawable.fav
        )

        holder.imgFavorite.setOnClickListener {
            product.isFavorite = !product.isFavorite
            notifyItemChanged(position)
            onFavoriteClick(product) // ✅ Notify ViewModel
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        Log.d("ProductAdapter", "Updating data: ${products.size} -> ${newProducts.size} products")
        products = newProducts
        notifyDataSetChanged()
        Log.d("ProductAdapter", "Data updated, item count: $itemCount")
    }
}
