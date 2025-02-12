package com.buzzware.radidodriver.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.adapters.DeliveredAdapter
import com.buzzware.radidodriver.adapters.UpcomingDeliveryAdapter
import com.buzzware.radidodriver.classes.FragmentListener
import com.buzzware.radidodriver.databinding.FragmentHomeBinding
import com.buzzware.rapidouser.Model.Order
import com.buzzware.rapidouser.utils.UserSession
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var fragmentContext: Context

    private val newOrderList: ArrayList<Order> = arrayListOf()
    private val deliveredOrderList: ArrayList<Order> = arrayListOf()
    private var adapterCheck = "active"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        setListener()
        getOrders()

        return binding.root
    }

    private fun setListener() {

        binding.newDeliveryTab.setOnClickListener {
            adapterCheck = "active"
            binding.newDeliveryTab.setBackgroundColor(resources.getColor(R.color.dark_red_color))
            binding.newDeliveryTab.setTextColor(resources.getColor(R.color.white))

            binding.alreadyDeliveredTab.setBackgroundColor(Color.TRANSPARENT)
            binding.alreadyDeliveredTab.setTextColor(resources.getColor(R.color.black))

            setUpcomingAdapter()

        }


        binding.alreadyDeliveredTab.setOnClickListener {
            adapterCheck = "completed"
            binding.alreadyDeliveredTab.setBackgroundColor(resources.getColor(R.color.dark_red_color))
            binding.alreadyDeliveredTab.setTextColor(resources.getColor(R.color.white))

            binding.newDeliveryTab.setBackgroundColor(Color.TRANSPARENT)
            binding.newDeliveryTab.setTextColor(resources.getColor(R.color.black))

            setCompleteAdapter()

        }

    }

    private fun getOrders() {
        binding.progressBar.visibility = View.VISIBLE
        Firebase.firestore.collection("OrderRequest").addSnapshotListener { value, error ->

            if (error != null) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(fragmentContext, "${error.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            newOrderList.clear()
            deliveredOrderList.clear()
            value!!.forEach {
                val order = it.toObject(Order::class.java)
                if (order.driverid.equals(UserSession.user.id)) {
                    when (order.status) {
                        "Active", "Payment Approval", "Paid" -> newOrderList.add(order)
                        "Complete" -> deliveredOrderList.add(order)
                    }
                }
            }
            binding.progressBar.visibility = View.GONE
            when (adapterCheck) {
                "active" -> setUpcomingAdapter()
                "completed" -> setCompleteAdapter()
            }
        }
    }

    private fun setUpcomingAdapter() {

        binding.recyclerView.layoutManager = LinearLayoutManager(fragmentContext)
        binding.recyclerView.adapter =
            UpcomingDeliveryAdapter(fragmentContext, newOrderList,
                listener = { lat, lng, orderID ->
                    (activity as FragmentListener).loadMapFragment(lng, lat, orderID)
                },
                approve = {
                    binding.progressBar.visibility = View.VISIBLE
                    Firebase.firestore.collection("OrderRequest").document(it)
                        .update("status", "Complete").addOnCompleteListener {
                        if (it.isSuccessful) {
                            binding.progressBar.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                })

    }

    private fun setCompleteAdapter() {

        binding.recyclerView.layoutManager = LinearLayoutManager(fragmentContext)
        binding.recyclerView.adapter = DeliveredAdapter(fragmentContext, deliveredOrderList)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}