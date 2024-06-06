package com.buzzware.radidodriver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.radidodriver.databinding.ItemDesignUpcomingDeliveryLayoutBinding

class UpcomingDeliveryAdapter(val context : Context, val list: List<String>, val listener : ViewOrderClicked) :
    RecyclerView.Adapter<UpcomingDeliveryAdapter.ViewHolder>() {

    interface ViewOrderClicked{
        fun onOrderClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingDeliveryAdapter.ViewHolder {
        return ViewHolder(ItemDesignUpcomingDeliveryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UpcomingDeliveryAdapter.ViewHolder, position: Int) {

        holder.binding.viewLocationTV.setOnClickListener {
            listener.onOrderClicked()
        }

    }

    override fun getItemCount(): Int {
        return 8
    }

    inner class ViewHolder(val binding : ItemDesignUpcomingDeliveryLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}