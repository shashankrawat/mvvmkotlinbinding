package com.mvvmkotlinbinding.data.social_login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mvvmkotlinbinding.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

public class LoginViaSocialAccounts
{
    public static final int RC_SIGN_IN = 101;

    private Activity mActivity;
    private Context mContext;
    private GoogleSignInClient mGoogleSignInClient;
    private SocialLoginResponseHandler mSocialResponseHandler;
    private CallbackManager callbackManager;

    public LoginViaSocialAccounts(Activity activity, SocialLoginResponseHandler socialLoginResponseHandler){
        mActivity = activity;
        mContext = activity;
        mSocialResponseHandler = socialLoginResponseHandler;

        initGoogleSignIn();
        initFacebookLogin();
    }

    private void initGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
    }


    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void signOutGoogleAccount(){
        if(mActivity != null && mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        }
    }


    public void handleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            Bundle acctData = getGoogleLoginData(acct);
            if(acctData != null){
                mSocialResponseHandler.onSocialLoginSuccess(acctData, "G");
            }else {
                mSocialResponseHandler.onSocialLoginFailure();
            }

        } catch (ApiException e) {
            mSocialResponseHandler.onSocialLoginFailure();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("G_SIGN_FAILED", "signInResult:failed code=" + e.getStatusCode()+", "+e.getMessage());

        }
    }


    public void signInWithFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Collections.singletonList("public_profile"));
    }

    public void signOutFBAccount(){
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
    }


    private void initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Bundle bFacebookData = getFacebookData(object);

                        if(bFacebookData != null){
                            mSocialResponseHandler.onSocialLoginSuccess(bFacebookData, "FB");
                        }else {
                            mSocialResponseHandler.onSocialLoginFailure();
                        }
                    }

                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                if (!CommonUtils.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "ERROR : " + exception.toString(), Toast.LENGTH_SHORT).show();
                }
                Log.e("FB_ERROR",""+exception.toString());
            }
        });

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();

            String id = object.getString("id");
            bundle.putString("FB_id", id);

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                //  Log.e("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            StringBuilder personName = new StringBuilder();
            if (object.has("first_name")){
                personName.append(object.getString("first_name"));
            }
            if (object.has("last_name")) {
                personName.append(" ").append(object.getString("last_name"));
            }
            bundle.putString("name", personName.toString());

            String fbEmail;
            if (object.has("email")) {
                fbEmail = object.getString("email");
            }else {
                fbEmail = object.getString("idFacebook") + "@facebook.com";
            }
            bundle.putString("email",fbEmail);

            return bundle;
        } catch (JSONException e) {
            Log.d("ERROR", "Error parsing JSON");
        }
        return null;
    }

    private Bundle getGoogleLoginData(GoogleSignInAccount acct)
    {
        if(acct != null){
            Bundle bundle = new Bundle();

            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();

            String personPhoto;
            if (acct.getPhotoUrl() != null) {
                personPhoto = acct.getPhotoUrl().toString();
            } else {
                personPhoto = "";
            }
            bundle.putString("profile_pic", personPhoto);
            bundle.putString("name", personName);
            bundle.putString("email", personEmail);
            bundle.putString("G_id", personId);

            return bundle;
        }

        return null;
    }


    public void fbOnActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public interface SocialLoginResponseHandler{
        void onSocialLoginSuccess(Bundle args, String socialType);
        void onSocialLoginFailure();
    }

}
