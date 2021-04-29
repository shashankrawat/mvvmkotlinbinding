package com.mvvmkotlinbinding.app_common_components.app_abstracts;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public abstract class BaseDialogFragment extends DialogFragment
{
    private BaseActivity activity;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (BaseActivity) getActivity();
    }

}
