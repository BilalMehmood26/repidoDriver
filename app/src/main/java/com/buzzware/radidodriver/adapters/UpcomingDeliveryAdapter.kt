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

class UpcomingDeliveryAdapter(
    val context: Context,
    val list: ArrayList<Order>,
    val listener: (Double, Double, String) -> Unit,
    val approve: (String) -> Unit,
) :
    RecyclerView.Adapter<UpcomingDeliveryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingDeliveryAdapter.ViewHolder {
        return ViewHolder(
            ItemDesignUpcomingDeliveryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UpcomingDeliveryAdapter.ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.apply {

            when (item.status) {
                "Payment Approval" -> {
                    viewLocationTV.visibility = View.GONE
                    statusTV.text = "Wait for payment"
                }

                "Paid" -> {
                    approveTV.visibility = View.VISIBLE
                    statusTV.text = item.status
                    viewLocationTV.visibility = View.GONE
                }

                else -> {
                    viewLocationTV.visibility = View.VISIBLE
                    approveTV.visibility = View.GONE
                    statusTV.text = item.status
                }
            }

            approveTV.setOnClickListener {
                approve.invoke(item.OrderID)
            }

            viewLocationTV.setOnClickListener {
                listener.invoke(item.latitude, item.longitude, item.OrderID)
            }

            if (UserSession.user.image.isNotEmpty()) {
                Glide.with(context).load(UserSession.user.image).into(driverProfileIV)
            }
            driverNameTv.text = "${UserSession.user?.firstName} ${UserSession.user?.lastName}"
            nameTv.text = item.patientName
            pharmacyTv.text = item.pharmacyName
            dateTimeTv.text =
                "${convertDateTimestamp(item.date)} ${convertTimeTimestamp(item.time)}"
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
                            if (user!!.image.isNotEmpty()) {
                                Glide.with(context).load(user.image).into(userProfileIV)
                            }
                        }
                    }
            }

            phoneTv.setOnClickListener {
                openPhoneDialer(item.patientNumber, context)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemDesignUpcomingDeliveryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}