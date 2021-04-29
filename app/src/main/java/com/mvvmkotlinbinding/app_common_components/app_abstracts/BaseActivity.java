package com.mvvmkotlinbinding.app_common_components.app_abstracts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.mvvmkotlinbinding.utils.ProgressDialogUtils;
import com.mvvmwithdatabinding.R;

public abstract class BaseActivity extends AppCompatActivity
{
    private ProgressDialog mBar;
    private ProgressDialogUtils progressUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSnackBar(View view, String msg) {
//        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();

        Snackbar sncbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
        sncbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sncbar.dismiss();
            }
        });

        sncbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                Log.e("SNACKBAR", "snack bar dismissed");
            }

            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
                Log.e("SNACKBAR", "snack bar shown");
            }
        });

        sncbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        sncbar.show();
    }

    public void showToast(String msg)
    {
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void showProgressDialog()
    {
        progressUtil = new ProgressDialogUtils();
        mBar = progressUtil.getDialog(this);
        progressUtil.showDialog(mBar);
    }

    public void dismissProgressDialog()
    {
        if(progressUtil != null) {
            progressUtil.onDismiss(mBar);
        }
        progressUtil = null;
        mBar = null;
    }

    public void setFragment(int containerId, Fragment fragment, String tag, boolean addToStack)
    {
        if(fragment == null){
            return;
        }

        try{
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.add(containerId, fragment, tag);
            if (addToStack) fragTransaction.addToBackStack(tag);
            fragTransaction.commit();
        }catch (Exception e){
            Log.e(tag, ""+e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermissions(String[] permissions) {
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

}
