package com.buzzware.radidodriver.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.classes.FragmentListener
import com.buzzware.radidodriver.databinding.FragmentMapBinding
import com.buzzware.radidodriver.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        setView()
        setListener()

        return binding.root
    }

    private fun setView() {

    }

    private fun setListener() {
        binding.accountLayout.setOnClickListener {
            (activity as FragmentListener).loadFragment("profileAccount")
        }

        binding.notificationLayout.setOnClickListener {
            (activity as FragmentListener).loadFragment("profileNotification")
        }

        binding.logoutLayout.setOnClickListener {
            (activity as FragmentListener).loadFragment("profileLogout")
        }
    }

}