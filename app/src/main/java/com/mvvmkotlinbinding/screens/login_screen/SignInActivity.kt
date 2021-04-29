package com.mvvmkotlinbinding.screens.login_screen

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity
import com.mvvmkotlinbinding.app_common_components.dialogs.AuthenticationDialog
import com.mvvmkotlinbinding.app_common_components.listeners.AuthenticationListener
import com.mvvmkotlinbinding.data.data_beans.LoginBean
import com.mvvmkotlinbinding.data.network.Resource
import com.mvvmkotlinbinding.data.network.Status
import com.mvvmkotlinbinding.data.social_login.LoginViaSocialAccounts
import com.mvvmkotlinbinding.data.social_login.LoginViaSocialAccounts.SocialLoginResponseHandler
import com.mvvmkotlinbinding.screens.home_screen.HomeActivity
import com.mvvmkotlinbinding.screens.login_screen.view_model.SignInViewModel
import com.mvvmkotlinbinding.utils.AppConstants
import com.mvvmkotlinbinding.utils.CommonUtils
import com.mvvmkotlinbinding.utils.DeviceId
import com.mvvmwithdatabinding.R
import com.mvvmwithdatabinding.databinding.ActivitySignInBinding
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SignInActivity : BaseActivity(), AuthenticationListener, SocialLoginResponseHandler {
    private var context: Context? = null
    private var model: SignInViewModel? = null
    private var socialLoginApi: LoginViaSocialAccounts? = null
    private lateinit var bindView: ActivitySignInBinding
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        rootView = bindView.root
        setContentView(rootView)
        initializeItems()
        bindView.viewmodel = model
        subscribeViewModel()
    }

    private fun initializeItems() {
        context = this
        model = ViewModelProvider(this).get(SignInViewModel::class.java)
        socialLoginApi = LoginViaSocialAccounts(this, this)
        setHyperLinkText()

        // social login requirements
        socialLoginApi!!.signOutGoogleAccount()
        socialLoginApi!!.signOutFBAccount()
    }

    private fun setHyperLinkText() {
        bindView!!.termConditionTv.movementMethod = LinkMovementMethod.getInstance()
        val text =
            "By signing up, you agree to our <font color=\"#000000\"><a href='https://www.google.com'>terms of service</a></font><br/> and <font color=\"#000000\"><a href='https://www.google.com'>privacy policy</a></font>."
        val s = Html.fromHtml(text) as Spannable
        for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
            s.setSpan(object : UnderlineSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.isUnderlineText = false
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0)
        }
        bindView!!.termConditionTv.text = s
    }

    private fun subscribeViewModel() {
        model!!.fbSignIn().observe(this, Observer<Resource<String>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    startActivity(Intent(context, HomeActivity::class.java))
                    finishAffinity()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                    showSnackBar(rootView, resource.message)
                }
            }
        })
        model!!.instaSignIn().observe(this, Observer<Resource<String>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    startActivity(Intent(context, HomeActivity::class.java))
                    finishAffinity()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                    showSnackBar(rootView, resource.message)
                }
            }
        })
        model!!.observeLogin().observe(this, Observer<Resource<LoginBean>> { resource ->
            if (resource == null) {
                return@Observer
            }
            when (resource.status) {
                Status.LOADING -> showProgressDialog()
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    startActivity(Intent(context, HomeActivity::class.java))
                    finishAffinity()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                    showSnackBar(rootView, resource.message)
                }
            }
        })
        model!!.observeErrorData().observe(this, Observer { errorRes ->
            if (errorRes == null) {
                return@Observer
            }
            when (errorRes.errorOf) {
                AppConstants.ErrorEmail -> {
                    bindView!!.emailEt.error = errorRes.errorMsg
                    bindView!!.emailEt.requestFocus()
                }
                AppConstants.ErrorPassword -> {
                    bindView!!.emailEt.error = null
                    bindView!!.passwordEt.error = errorRes.errorMsg
                    bindView!!.passwordEt.requestFocus()
                }
            }
        })
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.fb_login_btn -> socialLoginApi!!.signInWithFacebook()
            R.id.insta_login_btn -> {
                val authenticationDialog = AuthenticationDialog.instance
                authenticationDialog.setListener(this)
                authenticationDialog.show(supportFragmentManager, AuthenticationDialog.TAG)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialLoginApi!!.fbOnActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginViaSocialAccounts.RC_SIGN_IN && resultCode == RESULT_OK) {
            socialLoginApi!!.handleSignInResult(data)
        }
    }

    private fun callSocialSigIn(args: Bundle?, socialType: String?) {
        if (!CommonUtils.isNetworkAvailable(context!!)) {
            hideKeyBoard()
            showSnackBar(rootView, getString(R.string.no_network))
            return
        }
        val obj = JsonObject()
        obj.addProperty("name", args?.getString("name"))
        obj.addProperty("email", args?.getString("email"))
        if (socialType.equals("FB", ignoreCase = true)) {
            obj.addProperty("facebook_id", args?.getString("FB_id"))
        } else if (socialType.equals("INSTA", ignoreCase = true)) {
            obj.addProperty("insta_id", args?.getString("FB_id"))
        }
        obj.addProperty(
            "device_id",
            DeviceId.instance?.getDeviceId(context!!, model!!.userSession)
        )
        obj.addProperty("device_type", "A")
        obj.addProperty("device_token", "kdljalsjdskljfalksdjflka")
        obj.addProperty("profile_image", args?.getString("profile_pic"))
        if (socialType.equals("FB", ignoreCase = true)) {
            model!!.form.setFBSignInData(obj)
        } else if (socialType.equals("INSTA", ignoreCase = true)) {
            model!!.form.setInstaSignInData(obj)
        }
        showProgressDialog()
    }

    override fun onSocialLoginSuccess(args: Bundle?, socialType: String?) {
        callSocialSigIn(args, socialType)
    }

    override fun onSocialLoginFailure() {
        showToast("Something went wrong, Please try again!")
    }

    inner class RequestInstagramAPI : AsyncTask<String?, String?, String?>() {

        override fun onPostExecute(response: String?) {
            if (response != null) {
                try {
                    val jsonObject = JSONObject(response)
                    val jsonData = jsonObject.getJSONObject("data")
                    if (jsonData.has("error_message") && jsonData["error_message"] == "You are not a sandbox user of this client") {
                        CookieSyncManager.createInstance(context)
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.removeAllCookie()
                    }
                    val args = Bundle()
                    if (jsonData.has("id")) {
                        args.putString("name", jsonData.getString("username"))
                        args.putString("INSTA_id", jsonData.getString("id"))
                        if (jsonData.has("email")) args.putString(
                            "email",
                            jsonData.getString("email")
                        )
                        args.putString("profile_pic", jsonData.getString("profile_picture"))
                        callSocialSigIn(args, "INSTA")
                    }
                } catch (e: JSONException) {
                    Log.e("INSTA_EXP", "" + e.toString())
                }
            } else {
                val toast = Toast.makeText(applicationContext, "Login error!", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val httpClient: HttpClient = DefaultHttpClient()
            val httpGet = HttpGet(resources.getString(R.string.get_user_info_url) + params[0])
            try {
                val response = httpClient.execute(httpGet)
                val httpEntity = response.entity
                return EntityUtils.toString(httpEntity)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    companion object {
        private const val TAG = "SignInActivity"
    }

    override fun onTokenReceived(auth_token: String?) {
        if (auth_token == null) return
        RequestInstagramAPI().execute(auth_token)
    }
}