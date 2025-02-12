package com.buzzware.radidodriver.adapters

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.radidodriver.databinding.ItemDesignUpcomingDeliveryLayoutBinding
import com.buzzware.rapidouser.Model.Order
import com.buzzware.rapidouser.Model.User
import com.buzzware.rapidouser.utils.UserSession
import com.buzzware.rapidouser.utils.convertDateTimestamp
import com.buzzware.rapidouser.utils.convertTimeTimestamp
import com.buzzware.rapidouser.utils.openPhoneDialer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DeliveredAdapter(val context : Context,  val list: ArrayList<Order>) :
    RecyclerView.Adapter<DeliveredAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveredAdapter.ViewHolder {
        return ViewHolder(ItemDesignUpcomingDeliveryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DeliveredAdapter.ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.apply {

            if (UserSession.user.image.isNotEmpty()) {
                Glide.with(context).load(UserSession.user.image).into(driverProfileIV)
            }
            driverNameTv.text = "${UserSession.user?.firstName} ${UserSession.user?.lastName}"

            statusTV.text = item.status
            nameTv.text = item.patientName
            pharmacyTv.text = item.pharmacyName
            dateTimeTv.text = "${convertDateTimestamp(item.date)} ${convertTimeTimestamp(item.time)}"
            instructionTv.text = item.instructionfordrivers
            addressTv.text = item.address
            phoneTv.text = item.patientNumber
            emailTv.text = item.patientEmail
            phoneTv.paintFlags = phoneTv.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG

            if (item.patientID.isNotEmpty()) {
                Firebase.firestore.collection("Users").document(item.patientID).get()
                    .addOnSuccessListener { response ->
                        if (response.exists()) {
                            val user = response.toObject(User::class.java)
                            if(user!!.image.isNotEmpty()){
                                Glide.with(context).load(user.image).into(userProfileIV)
                            }
                        }
                    }
            }

            phoneTv.setOnClickListener {
                openPhoneDialer(item.patientNumber, context)
            }
        }
        holder.binding.viewLocationTV.visibility = View.GONE


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding : ItemDesignUpcomingDeliveryLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}