package com.mvvmkotlinbinding.app_common_components.app_abstracts;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.mvvmkotlinbinding.utils.AppCustomDropdownMenu;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseFragment extends Fragment
{
    private BaseActivity activity;
    private WeakReference<Context> mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = new WeakReference<>(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (BaseActivity) getActivity();
    }


    public void hideKeyBoard() {
        if(activity != null){
            activity.hideKeyBoard();
        }
    }

    public void showToast(String msg){
        if(activity != null){
            activity.showToast(msg);
        }
    }

    public void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        if(activity != null) {
            activity.showProgressDialog();
        }
    }

    public void dismissProgressDialog() {
        if(activity != null) {
            activity.dismissProgressDialog();
        }
    }

    public void setFragment(int containerID, Fragment fragment, String tag, boolean addToStack) {
        if (activity != null) {
            activity.setFragment(containerID, fragment, tag, addToStack);
        }
    }

    public void onBackPressed(){
        if(activity != null){
            activity.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermissions(String[] permissions) {
        if(activity != null) {
            return activity.checkPermissions(permissions);
        }else {
            return false;
        }
    }

    public void showDropDown(View anchor, ObservableField<String> ddViewField, List<String> ddData, boolean isWidthFull){
        AppCustomDropdownMenu popupWindow = new AppCustomDropdownMenu(mContext.get(), ddViewField, ddData);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        if(isWidthFull){
            popupWindow.setWidth(anchor.getWidth());
        }else {
            popupWindow.setWidth((anchor.getWidth() * 2));
        }
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor);
    }
}
