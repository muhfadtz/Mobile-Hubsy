package com.example.coworkingspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hubsytesting.databinding.ItemCoworkingBinding

class CoworkingAdapter(private val list: List<CoworkingSpace>) :
    RecyclerView.Adapter<CoworkingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCoworkingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCoworkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coworking = list[position]
        holder.binding.tvName.text = coworking.name
        holder.binding.tvRating.text = coworking.rating
        holder.binding.tvAddress.text = coworking.address
        holder.binding.tvPrice.text = coworking.price

        Glide.with(holder.itemView.context)
            .load(coworking.imageRes)
            .into(holder.binding.imageCoworking)
    }

    override fun getItemCount() = list.size
}
