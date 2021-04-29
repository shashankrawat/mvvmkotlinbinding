package com.mvvmkotlinbinding.app_common_components.app_abstracts;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.mvvmkotlinbinding.data.data_beans.ErrorBean;


public abstract class BaseForm {
    private final ErrorBean errorFields = new ErrorBean();
    private final MutableLiveData<ErrorBean> errorData = new MutableLiveData<>();

    public void setErrorData(String msg, int errorType){
        errorFields.setErrorMsg(msg);
        errorFields.setErrorOf(errorType);
        errorData.setValue(errorFields);
    }

    public MutableLiveData<ErrorBean> getErrorData() {
        return errorData;
    }


    public int getVisibility(String textValue) {
        if(!TextUtils.isEmpty(textValue)){
            return View.VISIBLE;
        }else {
            return View.GONE;
        }
    }

    public int getVisibility(Boolean isEnabled) {
        if(isEnabled){
            return View.VISIBLE;
        }else {
            return View.GONE;
        }
    }

    public int getVisibilityInvisible(Boolean isEnabled) {
        if(isEnabled){
            return View.VISIBLE;
        }else {
            return View.INVISIBLE;
        }
    }
}
