package com.buzzware.radidodriver.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.databinding.FragmentAccountBinding
import com.buzzware.radidodriver.databinding.FragmentMapBinding

class AccountFragment : Fragment() {

    lateinit var binding : FragmentAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)


        binding.saveTV.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

}