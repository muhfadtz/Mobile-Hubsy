package com.example.coworkingspace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hubsytesting.R

class CoworkingAdapter(
    private val coworkingList: List<CoworkingSpace>,
    private val onItemClick: (String) -> Unit  // Firestore ID adalah String
) : RecyclerView.Adapter<CoworkingAdapter.CoworkingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoworkingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coworking, parent, false)
        return CoworkingViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoworkingViewHolder, position: Int) {
        val coworkingSpace = coworkingList[position]
        holder.bind(coworkingSpace)
        holder.itemView.setOnClickListener { onItemClick(coworkingSpace.id) }
    }

    override fun getItemCount(): Int = coworkingList.size

    class CoworkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgCoworking: ImageView = itemView.findViewById(R.id.imgCoworking)
        private val tvName: TextView = itemView.findViewById(R.id.tvCoworkingName)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)

        fun bind(coworkingSpace: CoworkingSpace) {
            tvName.text = coworkingSpace.name
            tvRating.text = "⭐ ${coworkingSpace.rating} (${coworkingSpace.reviews})"
            tvLocation.text = "📍 ${coworkingSpace.location}"
            tvPrice.text = "Rp ${coworkingSpace.price}/day"

            if (!coworkingSpace.image.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(coworkingSpace.image)
                    .placeholder(R.drawable.rounded_image)
                    .into(imgCoworking)
            } else {
                imgCoworking.setImageResource(R.drawable.rounded_image)
            }
        }
    }
}
