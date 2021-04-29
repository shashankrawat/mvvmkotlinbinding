package com.mvvmkotlinbinding.app_common_components.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseDialogFragment;
import com.mvvmkotlinbinding.app_common_components.listeners.AuthenticationListener;
import com.mvvmwithdatabinding.R;
import com.mvvmwithdatabinding.databinding.AuthDialogBinding;

public class AuthenticationDialog extends BaseDialogFragment {

    public static final String TAG = "AuthenticationDialog";
    private Context context;

    private String redirect_url = "";
    private String request_url = "";
    private AuthenticationListener listener;
    private AuthDialogBinding binding;



    public static AuthenticationDialog getInstance() {
        return new AuthenticationDialog();
    }

    public void setListener(AuthenticationListener listener) {
        this.listener = listener;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AuthDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();

        initItems();
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl(request_url);
        binding.webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.bar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(redirect_url)) {
                    AuthenticationDialog.this.dismiss();
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.bar.setVisibility(View.GONE);
                if (url.contains("access_token=")) {
                    Uri uri = Uri.parse(url);
                    String access_token = uri.getEncodedFragment();
                    access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                    listener.onTokenReceived(access_token);
                }
            }
        });
    }

    private void initItems() {
        this.redirect_url = context.getResources().getString(R.string.redirect_url);
        this.request_url = context.getResources().getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content";
    }

}
