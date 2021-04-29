package com.mvvmkotlinbinding.screens.phone_authentication_screen

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity
import com.mvvmwithdatabinding.R
import com.mvvmwithdatabinding.databinding.PhoneAuthenticationScreenBinding
import java.util.concurrent.TimeUnit

class PhoneAuthenticationScreen : BaseActivity() {
    private var context: Context? = null
    private var binding: PhoneAuthenticationScreenBinding? = null
    private var rootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PhoneAuthenticationScreenBinding.inflate(
            layoutInflater
        )
        rootView = binding!!.root
        setContentView(rootView)
        initViews()
    }

    private fun initViews() {
        context = this
    }

    fun onClick(v: View) {
        if (v.id == R.id.authenticate_btn) {
            if (binding!!.authenticateBtn.text.toString()
                    .equals(getString(R.string.send_code), ignoreCase = true)
            ) {
                if (!TextUtils.isEmpty(binding!!.phoneNumberEt.text.toString())) {
                    if (binding!!.phoneNumberEt.text.length >= 10) {
                        binding!!.passCodeEt.visibility = View.VISIBLE
                        binding!!.authenticateBtn.text = getString(R.string.verify)
                        binding!!.passCodeEt.requestFocus()
                    } else {
                        showSnackBar(rootView, "Please enter a valid phone number")
                    }
                } else {
                    showSnackBar(rootView, "Please enter a valid phone number")
                }
            } else if (binding!!.authenticateBtn.text.toString()
                    .equals(getString(R.string.verify), ignoreCase = true)
            ) {
            }
        }
    }

    private fun phoneAuthenticationMethod(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks
        ) // OnVerificationStateChangedCallbacks
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: $phoneAuthCredential")
                //            signInWithPhoneAuthCredential(credential);
            }

            override fun onVerificationFailed(e: FirebaseException) {}
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
            }
        }

    companion object {
        private const val TAG = "PhoneAuth"
    }
}