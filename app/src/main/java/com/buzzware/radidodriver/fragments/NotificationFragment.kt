package com.buzzware.radidodriver.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.adapters.NotificationAdapter
import com.buzzware.radidodriver.databinding.FragmentMapBinding
import com.buzzware.radidodriver.databinding.FragmentNotificationBinding
import com.buzzware.rapidouser.Model.Notification
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment() {

    lateinit var binding : FragmentNotificationBinding
    private lateinit var fragmentContext: Context

    private val notificationList : ArrayList<Notification> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater)

        setListener()

        return binding.root
    }

    private fun setListener() {
        binding.progressBar.visibility = View.VISIBLE
        Firebase.firestore.collection("Notification").get().addOnCompleteListener { task->
            if(task.isSuccessful){
                binding.progressBar.visibility = View.GONE
                val notification = task.result.toObjects(Notification::class.java)
                notification.forEach {
                    if(it.userId.equals(Firebase.auth.currentUser!!.uid)){
                        notificationList.add(it)
                    }
                }
                setAdapter()
            }else{
                binding.progressBar.visibility = View.GONE
                Toast.makeText(fragmentContext, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAdapter() {
        binding.notificationRv.layoutManager = LinearLayoutManager(fragmentContext)
        binding.notificationRv.adapter = NotificationAdapter(fragmentContext,notificationList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}