package com.mvvmkotlinbinding.screens.phone_authentication_screen;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity;
import com.mvvmwithdatabinding.R;
import com.mvvmwithdatabinding.databinding.PhoneAuthenticationScreenBinding;

import java.util.concurrent.TimeUnit;
public class PhoneAuthenticationScreen extends BaseActivity
{
    private Context context;
    private static final String TAG = "PhoneAuth";

    private PhoneAuthenticationScreenBinding binding;
    private View rootView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PhoneAuthenticationScreenBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);

        initViews();
    }

    private void initViews() {
        context = this;
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.authenticate_btn) {
            if (binding.authenticateBtn.getText().toString().equalsIgnoreCase(getString(R.string.send_code))) {
                if (!TextUtils.isEmpty(binding.phoneNumberEt.getText().toString())) {
                    if (binding.phoneNumberEt.getText().length() >= 10) {
                        binding.passCodeEt.setVisibility(View.VISIBLE);
                        binding.authenticateBtn.setText(getString(R.string.verify));
                        binding.passCodeEt.requestFocus();
                    } else {
                        showSnackBar(rootView, "Please enter a valid phone number");
                    }
                } else {
                    showSnackBar(rootView, "Please enter a valid phone number");
                }
            } else if (binding.authenticateBtn.getText().toString().equalsIgnoreCase(getString(R.string.verify))) {

            }
        }
    }

    private void phoneAuthenticationMethod(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
//            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
        }
    };
}
