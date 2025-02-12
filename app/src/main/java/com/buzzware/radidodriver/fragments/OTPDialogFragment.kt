package com.buzzware.radidodriver.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.buzzware.radidodriver.R
import com.chaos.view.PinView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPDialogFragment(val phoneNumber: String, val onOTPResult: () -> Unit) : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var fragmentContext: Context


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = android.app.AlertDialog.Builder(requireActivity())
        val view = LayoutInflater.from(fragmentContext).inflate(R.layout.layout_otp_dialog, null)
        builder.setView(view)

        auth = FirebaseAuth.getInstance()

        val otpEditText = view.findViewById<PinView>(R.id.otpView2)
        val verifyOtpButton = view.findViewById<TextView>(R.id.continueTV)
        val cancelButton = view.findViewById<TextView>(R.id.cancelTV)

        cancelButton.setOnClickListener {
            Toast.makeText(fragmentContext, "dismiss", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        sendVerificationCode(phoneNumber)

        verifyOtpButton.setOnClickListener {
            val otpCode = otpEditText.text.toString().trim()
            if (otpCode.isNotEmpty()) {
                verifyCode(otpCode)
            } else {
                Toast.makeText(fragmentContext, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        return builder.create()
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(
                        fragmentContext,
                        "Verification failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(id, token)
                    verificationId = id
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            //signInWithCredential(credential)
            onOTPResult.invoke()
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    user?.updatePhoneNumber(credential)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Phone Number Linked!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onOTPResult.invoke()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to link phone number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            dismiss()
                        }
                } else {
                    Toast.makeText(
                        fragmentContext,
                        "Verification Failed. Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}