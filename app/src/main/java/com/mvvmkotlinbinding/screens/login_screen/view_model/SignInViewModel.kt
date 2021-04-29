package com.mvvmkotlinbinding.screens.login_screen.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.gson.JsonObject;
import com.mvvmkotlinbinding.data.data_beans.ErrorBean;
import com.mvvmkotlinbinding.data.data_beans.LoginBean;
import com.mvvmkotlinbinding.data.network.Resource;
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseViewModel;
import com.mvvmkotlinbinding.screens.login_screen.model.SignInRepo;

public class SignInViewModel extends BaseViewModel
{
    private SignInForm signInForm;

    private LiveData<Resource<String>> fbSignInLD;
    private LiveData<Resource<String>> instaSignInLD;
    private LiveData<Resource<LoginBean>> loginLD;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        signInForm = new SignInForm();
        fbSignInLD = Transformations.switchMap(signInForm.getFBSignData(), new Function<JsonObject, LiveData<Resource<String>>>() {
            @Override
            public LiveData<Resource<String>> apply(JsonObject input) {
                return SignInRepo.get().FBSignIn(input);
            }
        });

        instaSignInLD = Transformations.switchMap(signInForm.getInstaSignData(), new Function<JsonObject, LiveData<Resource<String>>>() {
            @Override
            public LiveData<Resource<String>> apply(JsonObject input) {
                return SignInRepo.get().InstaSignIn(input);
            }
        });

        loginLD = Transformations.switchMap(signInForm.getLoginData(), new Function<LoginBean, LiveData<Resource<LoginBean>>>() {
            @Override
            public LiveData<Resource<LoginBean>> apply(LoginBean input) {
                return SignInRepo.get().signInUser(input);
            }
        });
    }



    public LiveData<Resource<String>> fbSignIn() {
        return fbSignInLD;
    }

    public LiveData<Resource<String>> instaSignIn() {
        return instaSignInLD;
    }

    public LiveData<Resource<LoginBean>> observeLogin() {
        return loginLD;
    }
    public SignInForm getForm(){
        return signInForm;
    }


    public LiveData<ErrorBean> observeErrorData() {
        return signInForm.getErrorData();
    }
}
