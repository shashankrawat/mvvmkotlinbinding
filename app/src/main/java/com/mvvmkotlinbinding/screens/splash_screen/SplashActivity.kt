package com.mvvmkotlinbinding.screens.splash_screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity
import com.mvvmkotlinbinding.screens.login_screen.SignInActivity
import com.mvvmwithdatabinding.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : BaseActivity() {
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        context = this
        printHashKey()
        Handler().postDelayed({
            startActivity(Intent(context, SignInActivity::class.java))
            /*if(UserSessionImpl.getInstance(context).isUserLoggedIn()){
                        startActivity(new Intent(context, HomeActivity.class));
                    }else {
                        startActivity(new Intent(context, SignInActivity.class));
                    }*/finish()
        }, 3000)
    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }

    companion object {
        private const val TAG = "SplashScreen"
    }
}