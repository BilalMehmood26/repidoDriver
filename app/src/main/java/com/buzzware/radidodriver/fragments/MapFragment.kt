package com.buzzware.radidodriver.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.databinding.AlertMarkDeliverLayoutBinding
import com.buzzware.radidodriver.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding : FragmentMapBinding
    private lateinit var mMap: GoogleMap


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)


        setView()
        setListener()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        return binding.root
    }

    private fun setView() {

    }

    private fun setListener() {

        binding.markDeliveredTV.setOnClickListener {
            showDialog()
        }

    }

    private fun showDialog() {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = AlertMarkDeliverLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.orderNowTV.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLng = LatLng(33.66931572285472, 72.99679372912075)

        // Set the zoom level (you can adjust this value as needed)
        val zoomLevel = 13f

        // Move the camera to the specified location without adding a marker
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

    }



}