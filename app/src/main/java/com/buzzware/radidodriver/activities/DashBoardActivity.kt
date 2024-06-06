package com.buzzware.radidodriver.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.classes.FragmentListener
import com.buzzware.radidodriver.databinding.ActivityDashBoardBinding
import com.buzzware.radidodriver.fragments.*

class DashBoardActivity : AppCompatActivity(), FragmentListener {

    lateinit var binding : ActivityDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setView()
        setListener()

    }

    private fun setView() {
        setHomeTab()
    }

    private fun setListener() {

        binding.homeTab.setOnClickListener {
            setHomeTab()
        }

        binding.profileTab.setOnClickListener {
            setProfileTab()
        }

        binding.appBar.backIV.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setHomeTab() {
        binding.homeTab.setImageResource(R.drawable.icon_home_selected)
        binding.profileTab.setImageResource(R.drawable.icon_profile_unselected)
        binding.appBar.root.visibility = View.VISIBLE
        binding.appBar.backIV.visibility = View.INVISIBLE
        binding.appBar.titleTV.text = "Home"
        loadFragment(HomeFragment())
    }

    private fun setProfileTab() {
        binding.homeTab.setImageResource(R.drawable.icon_home_unselected)
        binding.profileTab.setImageResource(R.drawable.icon_profile_selected)
        binding.appBar.root.visibility = View.GONE
        loadFragment(ProfileFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.container.id, fragment)
        transaction.commit()
    }

    override fun loadFragment(value: String) {

        when (value) {
            "mapFragment" -> {
                binding.appBar.titleTV.text = "Location"
                binding.appBar.root.visibility = View.VISIBLE
                binding.appBar.backIV.visibility = View.VISIBLE
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(binding.container.id, MapFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            "profileAccount" -> {
                binding.appBar.titleTV.text = "Account"
                binding.appBar.root.visibility = View.VISIBLE
                binding.appBar.backIV.visibility = View.VISIBLE
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(binding.container.id, AccountFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            "profileNotification" -> {
                binding.appBar.titleTV.text = "Notifications"
                binding.appBar.root.visibility = View.VISIBLE
                binding.appBar.backIV.visibility = View.VISIBLE
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(binding.container.id, NotificationFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            "profileLogout" -> {
                finish()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkInstance()
    }

    private fun checkInstance(){
        when (supportFragmentManager.findFragmentById(binding.container.id)){

            is HomeFragment ->{
                setHomeTab()
            }

            is MapFragment ->{
                binding.appBar.root.visibility = View.VISIBLE
                binding.appBar.backIV.visibility = View.VISIBLE
            }

            is ProfileFragment ->{
                setProfileTab()
            }

            is AccountFragment ->{
                binding.appBar.root.visibility = View.VISIBLE
                binding.appBar.backIV.visibility = View.VISIBLE
            }

            is NotificationFragment ->{
                binding.appBar.root.visibility = View.VISIBLE
                binding.appBar.backIV.visibility = View.VISIBLE
            }

        }
    }
}