package com.mvvmkotlinbinding.screens.splash_screen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.os.Bundle;
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity;
import com.mvvmkotlinbinding.screens.login_screen.SignInActivity;
import com.mvvmwithdatabinding.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends BaseActivity
{
    private static final String TAG = "SplashScreen";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;

        printHashKey();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, SignInActivity.class));
                /*if(UserSessionImpl.getInstance(context).isUserLoggedIn()){
                    startActivity(new Intent(context, HomeActivity.class));
                }else {
                    startActivity(new Intent(context, SignInActivity.class));
                }*/
                finish();
            }
        }, 3000);
    }


    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
}
