package com.mvvmkotlinbinding.app_common_components.binding_classes

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.mvvmkotlinbinding.app_common_components.listeners.TextChangeListener

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("onViewFocusChange")
        fun onViewFocusChange(view: AppCompatEditText, listener: OnFocusChangeListener) {
            view.onFocusChangeListener = OnFocusChangeListener { v: View?, hasFocus: Boolean ->
                if (!hasFocus) {
                    listener.onFocusChange(v, hasFocus)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("onTextChange")
        fun onTextChange(view: AppCompatEditText, listener: TextChangeListener) {
            view.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    listener.onTextChange(s.toString())
                }
            })
        }

        @JvmStatic
        @BindingAdapter("onTextChange")
        fun onTextChange(view: AppCompatTextView, listener: TextChangeListener) {
            view.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    listener.onTextChange(s.toString())
                }
            })
        }

        @JvmStatic
        @BindingAdapter("onCheckChange")
        fun onCheckChanged(rbView: RadioButton, listener: CompoundButton.OnCheckedChangeListener) {
            rbView.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener.onCheckedChanged(buttonView, isChecked)
                }
            }
        }
    }
}