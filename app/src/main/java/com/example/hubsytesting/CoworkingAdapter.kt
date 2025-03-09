package com.example.coworkingspace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hubsytesting.R


class CoworkingAdapter(private val coworkingList: List<CoworkingSpace>) :
    RecyclerView.Adapter<CoworkingAdapter.CoworkingViewHolder>() {

    class CoworkingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCoworkingName)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoworkingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coworking, parent, false)
        return CoworkingViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoworkingViewHolder, position: Int) {
        val coworking = coworkingList[position]
        holder.tvName.text = coworking.name
        holder.tvRating.text = "⭐ ${coworking.rating} (${coworking.reviews})"
        holder.tvLocation.text = "📍 ${coworking.location}"
        holder.tvPrice.text = "Rp ${coworking.price}/day"
    }

    override fun getItemCount(): Int = coworkingList.size
}
