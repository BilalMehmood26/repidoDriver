package com.buzzware.radidodriver.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.databinding.ActivitySignUpBinding
import com.buzzware.radidodriver.databinding.LayoutOtpDialogBinding
import com.buzzware.radidodriver.fragments.OTPDialogFragment
import com.buzzware.rapidouser.Model.User
import com.buzzware.rapidouser.utils.UserSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var otpDialogFragment : OTPDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setView()
        setListener()
    }

    private fun setView() {
        binding.appBar.backIV.visibility = View.INVISIBLE
        binding.appBar.titleTV.text = "Sign Up"

        binding.apply {
            var isPasswordVisible = false

            passwordToggle.setOnClickListener {
                isPasswordVisible = !isPasswordVisible

                if (isPasswordVisible) {
                    passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    passwordToggle.setImageResource(R.drawable.icon_eye_show)
                } else {
                    passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
                    passwordToggle.setImageResource(R.drawable.icon_eye_hide)
                }
                passwordET.setSelection(passwordET.text.length)
            }
        }

    }

    private fun setListener() {

        binding.loginTV.setOnClickListener {
            finish()
        }


        binding.createAccountTV.setOnClickListener {
            validateAndSignIn()
        }

    }

    private fun validateAndSignIn() {
        val firstName = binding.firstNameET.text.toString().trim()
        val lastName = binding.lastNameET.text.toString().trim()
        val email = binding.emailET.text.toString().trim()
        val phoneNumber = binding.phoneEt.text.toString().trim()
        val carModel = binding.vehicleModelET.text.toString().trim()
        val carName = binding.vehicleNameET.text.toString().trim()
        val countryCode = binding.ccpTv.selectedCountryCodeWithPlus
        val fullPhoneNumber = "$countryCode$phoneNumber"

        val password = binding.passwordET.text.toString().trim()

        when {
            firstName.isEmpty() -> showToast("First name is required")
            lastName.isEmpty() -> showToast("Last name is required")
            email.isEmpty() -> showToast("Email is required")
            phoneNumber.isEmpty() -> showToast("Phone number is required")
            password.isEmpty() -> showToast("Password is required")
            carModel.isEmpty() -> showToast("Vehicle model is required")
            carName.isEmpty() -> showToast("Vehicle name is required")
            else -> {
                otpDialogFragment = OTPDialogFragment(fullPhoneNumber){
                    signUp(carModel, carName, firstName, lastName, email, fullPhoneNumber, password)
                }
                otpDialogFragment.show(supportFragmentManager,"")
            }
        }
    }

    private fun otpDialog(
        carModel: String,
        carName: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String
    ) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = LayoutOtpDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.cancelTV.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.continueTV.setOnClickListener {
            dialog.dismiss()
            signUp(carModel, carName, firstName, lastName, email, phoneNumber, password)
        }

        dialog.show()
    }

    private fun signUp(
        carModel: String,
        carName: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                userDetails(
                    carModel,
                    carName,
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    password,
                    auth.currentUser!!.uid,
                    it
                )
            }.addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                userDetails(
                    carModel,
                    carName,
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    password,
                    auth.currentUser!!.uid,
                    ""
                )
            }
        }.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                this@SignUpActivity,
                it.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun userDetails(
        carModel: String,
        carName: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        id: String,
        token: String
    ) {
        val userData = hashMapOf(
            "car_model" to carModel,
            "car_name" to carName,
            "email" to email,
            "firstName" to firstName,
            "id" to id,
            "lastName" to lastName,
            "password" to password,
            "phoneNumber" to phoneNumber,
            "token" to token,
            "userRole" to "driver"
        )

        val user = User(
            email = email,
            carModel = carModel,
            carName = carName,
            firstName = firstName,
            id = id,
            lastName = lastName,
            password = password,
            phoneNumber = phoneNumber,
            token = token,
            userRole = "driver"
        )
        Firebase.firestore.collection("Users").document(id).set(userData).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                UserSession.user = user
                val intent = Intent(this@SignUpActivity, DashBoardActivity::class.java)
                startActivity(intent)
                overridePendingTransition(
                    androidx.appcompat.R.anim.abc_fade_in,
                    androidx.appcompat.R.anim.abc_fade_out
                )
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@SignUpActivity,
                    it.exception!!.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}