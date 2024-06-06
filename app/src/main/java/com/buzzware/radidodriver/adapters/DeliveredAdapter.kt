package com.buzzware.radidodriver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.radidodriver.databinding.ItemDesignUpcomingDeliveryLayoutBinding

class DeliveredAdapter(val context : Context, val list: List<String>) :
    RecyclerView.Adapter<DeliveredAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveredAdapter.ViewHolder {
        return ViewHolder(ItemDesignUpcomingDeliveryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DeliveredAdapter.ViewHolder, position: Int) {

        holder.binding.statusTV.text = "Delivered"
        holder.binding.viewLocationTV.visibility = View.GONE


    }

    override fun getItemCount(): Int {
        return 8
    }

    inner class ViewHolder(val binding : ItemDesignUpcomingDeliveryLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}