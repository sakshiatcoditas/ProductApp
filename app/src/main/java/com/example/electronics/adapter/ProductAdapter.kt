package com.example.electronics.ui.adapter

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
    private val onFavoriteClick: (Product) -> Unit,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductBrand: TextView = itemView.findViewById(R.id.tvProductBrand)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val imgFavorite: ImageView = itemView.findViewById(R.id.imgFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.tvProductName.text = product.title
        holder.tvProductBrand.text = product.brand ?: "Unknown Brand"
        holder.tvPrice.text = "$${product.price}"

        val imageUrl = if (product.image.startsWith("http")) {
            product.image
        } else {
            "https://fakestoreapi.in${product.image}"
        }
        
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .timeout(15000))
            .into(holder.imgProduct)

        holder.imgFavorite.setImageResource(
            if (product.isFavorite) R.drawable.favlike else R.drawable.fav
        )

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        holder.imgFavorite.setOnClickListener {
            holder.imgFavorite.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    holder.imgFavorite.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            
            onFavoriteClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
