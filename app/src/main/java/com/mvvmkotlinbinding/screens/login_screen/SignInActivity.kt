package com.mvvmkotlinbinding.screens.login_screen;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.mvvmkotlinbinding.data.data_beans.ErrorBean;
import com.mvvmkotlinbinding.data.data_beans.LoginBean;
import com.mvvmkotlinbinding.data.network.Resource;
import com.mvvmkotlinbinding.data.social_login.LoginViaSocialAccounts;
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity;
import com.mvvmkotlinbinding.app_common_components.dialogs.AuthenticationDialog;
import com.mvvmkotlinbinding.app_common_components.listeners.AuthenticationListener;
import com.mvvmkotlinbinding.screens.home_screen.HomeActivity;
import com.mvvmkotlinbinding.screens.login_screen.view_model.SignInViewModel;
import com.mvvmkotlinbinding.utils.AppConstants;
import com.mvvmkotlinbinding.utils.CommonUtils;
import com.mvvmkotlinbinding.utils.DeviceId;
import com.mvvmwithdatabinding.R;
import com.mvvmwithdatabinding.databinding.ActivitySignInBinding;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.mvvmkotlinbinding.data.social_login.LoginViaSocialAccounts.RC_SIGN_IN;

public class SignInActivity extends BaseActivity implements AuthenticationListener, LoginViaSocialAccounts.SocialLoginResponseHandler {
    private static final String TAG = "SignInActivity";
    private Context context;

    private SignInViewModel model;
    private LoginViaSocialAccounts socialLoginApi;

    private ActivitySignInBinding bindView;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        rootView = bindView.getRoot();
        setContentView(rootView);

        initializeItems();

        bindView.setViewmodel(model);

        subscribeViewModel();
    }


    private void initializeItems()
    {
        context = this;
        model = new ViewModelProvider(this).get(SignInViewModel.class);
        socialLoginApi = new LoginViaSocialAccounts(this, this);

        setHyperLinkText();

        // social login requirements
        socialLoginApi.signOutGoogleAccount();
        socialLoginApi.signOutFBAccount();
    }

    private void setHyperLinkText() {
        bindView.termConditionTv.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "By signing up, you agree to our <font color=\"#000000\"><a href='https://www.google.com'>terms of service</a></font><br/> and <font color=\"#000000\"><a href='https://www.google.com'>privacy policy</a></font>.";
        Spannable s = (Spannable) Html.fromHtml(text);
        for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(@NonNull TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        bindView.termConditionTv.setText(s);
    }


    private void subscribeViewModel() {

        model.fbSignIn().observe(this, new Observer<Resource<String>>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChanged(@Nullable Resource<String> resource) {
                if (resource == null) {
                    return;
                }

                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        dismissProgressDialog();
                        startActivity(new Intent(context, HomeActivity.class));

                        finishAffinity();
                        break;
                    case ERROR:
                        dismissProgressDialog();
                        showSnackBar(rootView, resource.message);
                        break;
                }
            }
        });

        model.instaSignIn().observe(this, new Observer<Resource<String>>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChanged(@Nullable Resource<String> resource) {
                if (resource == null) {
                    return;
                }
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        dismissProgressDialog();
                        startActivity(new Intent(context, HomeActivity.class));
                        finishAffinity();
                        break;
                    case ERROR:
                        dismissProgressDialog();
                        showSnackBar(rootView, resource.message);
                        break;
                }
            }
        });

        model.observeLogin().observe(this, new Observer<Resource<LoginBean>>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChanged(@Nullable Resource<LoginBean> resource) {
                if (resource == null) {
                    return;
                }

                switch (resource.status) {
                    case LOADING:
                        showProgressDialog();
                        break;
                    case SUCCESS:
                        dismissProgressDialog();
                        startActivity(new Intent(context, HomeActivity.class));

                        finishAffinity();
                        break;
                    case ERROR:
                        dismissProgressDialog();
                        showSnackBar(rootView, resource.message);
                        break;
                }
            }
        });


        model.observeErrorData().observe(this, new Observer<ErrorBean>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChanged(@Nullable ErrorBean errorRes) {
                if (errorRes == null) {
                    return;
                }

                switch (errorRes.getErrorOf()) {
                    case AppConstants.ErrorEmail:
                        bindView.emailEt.setError(errorRes.getErrorMsg());
                        bindView.emailEt.requestFocus();
                        break;
                    case AppConstants.ErrorPassword:
                        bindView.emailEt.setError(null);
                        bindView.passwordEt.setError(errorRes.getErrorMsg());
                        bindView.passwordEt.requestFocus();
                        break;
                }
            }
        });
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fb_login_btn:
                socialLoginApi.signInWithFacebook();
                break;

            case R.id.insta_login_btn:
                AuthenticationDialog authenticationDialog = AuthenticationDialog.getInstance();
                authenticationDialog.setListener(this);
                authenticationDialog.show(getSupportFragmentManager(), AuthenticationDialog.TAG);

//                socialLoginApi.signInWithGoogle();
                break;

        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        socialLoginApi.fbOnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            socialLoginApi.handleSignInResult(data);
        }
    }


    private void callSocialSigIn(Bundle args, String socialType)
    {
    if (!CommonUtils.isNetworkAvailable(context)) {
        hideKeyBoard();
        showSnackBar(rootView, getString(R.string.no_network));
        return;
    }

    JsonObject obj = new JsonObject();
    obj.addProperty("name", args.getString("name"));
    obj.addProperty("email", args.getString("email"));
    if(socialType.equalsIgnoreCase("FB")) {
        obj.addProperty("facebook_id", args.getString("FB_id"));
    }else if(socialType.equalsIgnoreCase("INSTA")){
        obj.addProperty("insta_id", args.getString("FB_id"));
    }
    obj.addProperty("device_id", DeviceId.getInstance().getDeviceId(context, model.getUserSession()));
    obj.addProperty("device_type", "A");
    obj.addProperty("device_token", "kdljalsjdskljfalksdjflka");
    obj.addProperty("profile_image", args.getString("profile_pic"));

    if(socialType.equalsIgnoreCase("FB")) {
        model.getForm().setFBSignInData(obj);
    }else if(socialType.equalsIgnoreCase("INSTA")){
        model.getForm().setInstaSignInData(obj);
    }
    showProgressDialog();
}


    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;

        new RequestInstagramAPI().execute(auth_token);
    }

    @Override
    public void onSocialLoginSuccess(Bundle args, String socialType) {
        callSocialSigIn(args, socialType);
    }

    @Override
    public void onSocialLoginFailure() {
        showToast("Something went wrong, Please try again!");
    }


    public class RequestInstagramAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + params[0]);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if(jsonData.has("error_message") && jsonData.get("error_message").equals("You are not a sandbox user of this client")){
                        CookieSyncManager.createInstance(context);
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                    }

                    Bundle args = new Bundle();
                    if (jsonData.has("id")) {
                        args.putString("name", jsonData.getString("username"));
                        args.putString("INSTA_id", jsonData.getString("id"));
                        if(jsonData.has("email"))
                            args.putString("email", jsonData.getString("email"));
                        args.putString("profile_pic", jsonData.getString("profile_picture"));

                        callSocialSigIn(args, "INSTA");
                    }
                } catch (JSONException e) {
                    Log.e("INSTA_EXP",""+e.toString());
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"Login error!",Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }
}
