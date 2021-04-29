package com.mvvmkotlinbinding.screens.login_screen.view_model;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.mvvmkotlinbinding.data.data_beans.LoginBean;
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseForm;
import com.mvvmkotlinbinding.utils.AppConstants;

public class SignInForm extends BaseForm {
    public LoginBean loginFields = new LoginBean();
    private final MutableLiveData<LoginBean> signInData = new MutableLiveData<>();
    private final MutableLiveData<JsonObject> fbSignInData = new MutableLiveData<>();
    private final MutableLiveData<JsonObject> instaSignInData = new MutableLiveData<>();

    public Boolean isEmailIdValid(String email) {
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            setErrorData("", 0);
            return true;
        }else {
            setErrorData("Please enter a valid Email.", AppConstants.ErrorEmail);
            return false;
        }
    }

    public Boolean isPasswordValid(String pwd) {
        if(!TextUtils.isEmpty(pwd) && pwd.length() >= 6){
            setErrorData("", 0);
            return true;
        }else {
            setErrorData("Password must be of atleast 6 letter", AppConstants.ErrorPassword);
            return false;
        }
    }


    public void setFBSignInData(JsonObject fbData){
        fbSignInData.setValue(fbData);
    }
    public MutableLiveData<JsonObject> getFBSignData() {
        return fbSignInData;
    }

    public void setInstaSignInData(JsonObject fbData){
        instaSignInData.setValue(fbData);
    }
    public MutableLiveData<JsonObject> getInstaSignData() {
        return instaSignInData;
    }

    public void onSignInClick() {
        if(isEmailIdValid(loginFields.getEmail()) && isPasswordValid(loginFields.getPassword())) {
            signInData.setValue(loginFields);
        }
    }

    public MutableLiveData<LoginBean> getLoginData() {
        return signInData;
    }

}
