package com.buzzware.radidodriver.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.adapters.DeliveredAdapter
import com.buzzware.radidodriver.adapters.UpcomingDeliveryAdapter
import com.buzzware.radidodriver.classes.FragmentListener
import com.buzzware.radidodriver.databinding.FragmentHomeBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class HomeFragment : Fragment(), UpcomingDeliveryAdapter.ViewOrderClicked{

    lateinit var binding : FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        setView()
        setListener()

        return binding.root
    }

    private fun setView() {
        setUpcomingAdapter()


    }

    private fun setListener() {

        binding.newDeliveryTab.setOnClickListener {

            binding.newDeliveryTab.setBackgroundColor(resources.getColor(R.color.dark_red_color))
            binding.newDeliveryTab.setTextColor(resources.getColor(R.color.white))

            binding.alreadyDeliveredTab.setBackgroundColor(Color.TRANSPARENT)
            binding.alreadyDeliveredTab.setTextColor(resources.getColor(R.color.black))

            setUpcomingAdapter()

        }


        binding.alreadyDeliveredTab.setOnClickListener {

            binding.alreadyDeliveredTab.setBackgroundColor(resources.getColor(R.color.dark_red_color))
            binding.alreadyDeliveredTab.setTextColor(resources.getColor(R.color.white))

            binding.newDeliveryTab.setBackgroundColor(Color.TRANSPARENT)
            binding.newDeliveryTab.setTextColor(resources.getColor(R.color.black))

            setCompleteAdapter()

        }

    }



    private fun setUpcomingAdapter() {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = UpcomingDeliveryAdapter(requireActivity(), emptyList(), this)

    }

    private fun setCompleteAdapter() {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = DeliveredAdapter(requireActivity(), emptyList())

    }

    override fun onOrderClicked() {
        //open Map Fragment Here
        (activity as FragmentListener).loadFragment("mapFragment")
    }

}