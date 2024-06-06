package com.buzzware.radidodriver.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.databinding.FragmentMapBinding
import com.buzzware.radidodriver.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    lateinit var binding : FragmentNotificationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater)



        return binding.root
    }

}