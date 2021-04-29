package com.mvvmkotlinbinding.app_common_components.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseDialogFragment
import com.mvvmkotlinbinding.app_common_components.listeners.AuthenticationListener
import com.mvvmwithdatabinding.R
import com.mvvmwithdatabinding.databinding.AuthDialogBinding

class AuthenticationDialog : BaseDialogFragment() {
    private var mContext: Context? = null
    private var redirect_url = ""
    private var request_url = ""
    private var listener: AuthenticationListener? = null
    private var binding: AuthDialogBinding? = null
    fun setListener(listener: AuthenticationListener?) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthDialogBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context
        initItems()
        binding!!.webview.settings.javaScriptEnabled = true
        binding!!.webview.loadUrl(request_url)
        binding!!.webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
                binding!!.bar.visibility = View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(redirect_url)) {
                    dismiss()
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                binding!!.bar.visibility = View.GONE
                if (url.contains("access_token=")) {
                    val uri = Uri.parse(url)
                    var access_token = uri.encodedFragment
                    access_token = access_token!!.substring(access_token.lastIndexOf("=") + 1)
                    listener!!.onTokenReceived(access_token)
                }
            }
        }
    }

    private fun initItems() {
        redirect_url = mContext!!.resources.getString(R.string.redirect_url)
        request_url = mContext!!.resources.getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                mContext!!.resources.getString(R.string.client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content"
    }

    companion object {
        const val TAG = "AuthenticationDialog"
        val instance: AuthenticationDialog
            get() = AuthenticationDialog()
    }
}