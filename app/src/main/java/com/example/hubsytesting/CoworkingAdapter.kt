package com.example.coworkingspace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hubsytesting.R

class CoworkingAdapter(
    private val coworkingList: List<CoworkingSpace>,
    private val onItemClick: (Int) -> Unit // Callback saat item diklik
) : RecyclerView.Adapter<CoworkingAdapter.CoworkingViewHolder>() {

    class CoworkingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvCoworkingName)
        val rating: TextView = view.findViewById(R.id.tvRating)
        val location: TextView = view.findViewById(R.id.tvLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoworkingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coworking, parent, false)
        return CoworkingViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoworkingViewHolder, position: Int) {
        val coworking = coworkingList[position]

        holder.name.text = coworking.name
        holder.rating.text = "⭐ ${coworking.rating} (${coworking.reviews})"
        holder.location.text = "📍 ${coworking.location}"

        // Handle klik item
        holder.itemView.setOnClickListener {
            onItemClick(coworking.id)
        }
    }

    override fun getItemCount(): Int = coworkingList.size
}
