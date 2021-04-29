package com.mvvmkotlinbinding.data.social_login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mvvmkotlinbinding.utils.CommonUtils
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL

class LoginViaSocialAccounts(
    private val mActivity: Activity?,
    socialLoginResponseHandler: SocialLoginResponseHandler
) {
    private val mContext: Context?
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val mSocialResponseHandler: SocialLoginResponseHandler
    private var callbackManager: CallbackManager? = null
    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity!!, gso)
    }

    fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        mActivity!!.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOutGoogleAccount() {
        if (mActivity != null && mGoogleSignInClient != null) {
            mGoogleSignInClient!!.signOut()
                .addOnCompleteListener(mActivity) {
                    // ...
                }
        }
    }

    fun handleSignInResult(data: Intent?) {
        try {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val acct = completedTask.getResult(ApiException::class.java)
            val acctData = getGoogleLoginData(acct)
            if (acctData != null) {
                mSocialResponseHandler.onSocialLoginSuccess(acctData, "G")
            } else {
                mSocialResponseHandler.onSocialLoginFailure()
            }
        } catch (e: ApiException) {
            mSocialResponseHandler.onSocialLoginFailure()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("G_SIGN_FAILED", "signInResult:failed code=" + e.statusCode + ", " + e.message)
        }
    }

    fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(mActivity, listOf("public_profile"))
    }

    fun signOutFBAccount() {
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut()
        }
    }

    private fun initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        // Callback registration
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val request =
                        GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                            val bFacebookData = getFacebookData(`object`)
                            if (bFacebookData != null) {
                                mSocialResponseHandler.onSocialLoginSuccess(bFacebookData, "FB")
                            } else {
                                mSocialResponseHandler.onSocialLoginFailure()
                            }
                        }
                    val parameters = Bundle()
                    parameters.putString(
                        "fields",
                        "id, first_name, last_name, email,gender, birthday"
                    ) // Parámetros que pedimos a facebook
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show()
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    if (!CommonUtils.isNetworkAvailable(mContext!!)) {
                        Toast.makeText(
                            mContext,
                            "Please check your internet connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(mContext, "ERROR : $exception", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("FB_ERROR", "" + exception.toString())
                }
            })
    }

    private fun getFacebookData(`object`: JSONObject): Bundle? {
        try {
            val bundle = Bundle()
            val id = `object`.getString("id")
            bundle.putString("FB_id", id)
            try {
                val profile_pic = URL("https://graph.facebook.com/$id/picture?width=200&height=150")
                //  Log.e("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return null
            }
            val personName = StringBuilder()
            if (`object`.has("first_name")) {
                personName.append(`object`.getString("first_name"))
            }
            if (`object`.has("last_name")) {
                personName.append(" ").append(`object`.getString("last_name"))
            }
            bundle.putString("name", personName.toString())
            val fbEmail: String
            fbEmail = if (`object`.has("email")) {
                `object`.getString("email")
            } else {
                `object`.getString("idFacebook") + "@facebook.com"
            }
            bundle.putString("email", fbEmail)
            return bundle
        } catch (e: JSONException) {
            Log.d("ERROR", "Error parsing JSON")
        }
        return null
    }

    private fun getGoogleLoginData(acct: GoogleSignInAccount?): Bundle? {
        if (acct != null) {
            val bundle = Bundle()
            val personName = acct.displayName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: String
            personPhoto = if (acct.photoUrl != null) {
                acct.photoUrl.toString()
            } else {
                ""
            }
            bundle.putString("profile_pic", personPhoto)
            bundle.putString("name", personName)
            bundle.putString("email", personEmail)
            bundle.putString("G_id", personId)
            return bundle
        }
        return null
    }

    fun fbOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    interface SocialLoginResponseHandler {
        fun onSocialLoginSuccess(args: Bundle?, socialType: String?)
        fun onSocialLoginFailure()
    }

    companion object {
        const val RC_SIGN_IN = 101
    }

    init {
        mContext = mActivity
        mSocialResponseHandler = socialLoginResponseHandler
        initGoogleSignIn()
        initFacebookLogin()
    }
}