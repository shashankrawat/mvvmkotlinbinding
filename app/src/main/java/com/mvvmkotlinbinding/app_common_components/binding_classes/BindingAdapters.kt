package com.mvvmkotlinbinding.app_common_components.binding_classes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.mvvmkotlinbinding.app_common_components.listeners.TextChangeListener;

public class BindingAdapters {

    @BindingAdapter("onViewFocusChange")
    public static void onViewFocusChange(AppCompatEditText view, View.OnFocusChangeListener listener){
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                listener.onFocusChange(v, hasFocus);
            }
        });
    }

    @BindingAdapter("onTextChange")
    public static void onTextChange(AppCompatEditText view, TextChangeListener listener){
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listener.onTextChange(s.toString());
            }
        });
    }

    @BindingAdapter("onTextChange")
    public static void onTextChange(AppCompatTextView view, TextChangeListener listener){
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listener.onTextChange(s.toString());
            }
        });
    }

    @BindingAdapter("onCheckChange")
    public static void onCheckChanged(RadioButton rbView, CompoundButton.OnCheckedChangeListener listener){
        rbView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    listener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }
}
