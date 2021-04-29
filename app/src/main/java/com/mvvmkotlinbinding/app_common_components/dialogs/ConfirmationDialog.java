package com.mvvmkotlinbinding.app_common_components.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseDialogFragment;
import com.mvvmkotlinbinding.app_common_components.listeners.ConfirmationDialogListener;
import com.mvvmwithdatabinding.R;
import com.mvvmwithdatabinding.databinding.DialogConfirmationViewBinding;

import org.jetbrains.annotations.NotNull;

public class ConfirmationDialog extends BaseDialogFragment implements View.OnClickListener {

    public static final String TAG = "ConfirmationDialog";

    private String titleText, msg;
    private ConfirmationDialogListener listener;
    private DialogConfirmationViewBinding binding;

    public static ConfirmationDialog getInstance(Bundle args) {
        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null){
            titleText = args.getString("TITLE");
            msg = args.getString("MESSAGE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogConfirmationViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void setListener(ConfirmationDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!TextUtils.isEmpty(titleText)) {
            binding.titleText.setText(titleText);
        }
        if(!TextUtils.isEmpty(msg)) {
            binding.titleDesc.setText(msg);
        }

        binding.yes.setOnClickListener(this);
        binding.no.setOnClickListener(this);

    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:
                if (listener != null) {
                    listener.onOkClicked();
                }
                dismiss();
                break;

            case R.id.no:
                dismiss();
                break;
        }
    }

}
