package com.mvvmkotlinbinding.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.mvvmwithdatabinding.R;

public class ProgressDialogUtils
{
    public ProgressDialog getDialog(Context c) {
        ProgressDialog mBar = new ProgressDialog(c);
        if(mBar.getWindow() != null) {
            mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mBar.setCanceledOnTouchOutside(false);
        return mBar;
    }

    public void showDialog(ProgressDialog mBar) {
        mBar.show();
        mBar.setContentView(R.layout.custom_progress_bar);

    }

    public void onDismiss(ProgressDialog mBar) {
        if (mBar != null && mBar.isShowing())
            mBar.dismiss();

    }
}
