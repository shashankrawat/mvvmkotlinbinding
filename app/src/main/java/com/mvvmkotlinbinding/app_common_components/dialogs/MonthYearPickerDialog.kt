package com.mvvmkotlinbinding.app_common_components.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseDialogFragment;
import com.mvvmwithdatabinding.R;
import com.mvvmwithdatabinding.databinding.DialogMonthYearPickerBinding;

import java.util.Calendar;

public class MonthYearPickerDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = "MonthYearPickerDialog";
    private DatePickerDialog.OnDateSetListener listener;
    private DialogMonthYearPickerBinding binding;


    public static MonthYearPickerDialog getInstance() {
        return new MonthYearPickerDialog();
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogMonthYearPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar cal = Calendar.getInstance();

        binding.pickerMonth.setMinValue(1);
        binding.pickerMonth.setMaxValue(12);
        binding.pickerMonth.setValue(cal.get(Calendar.MONTH) + 1);

        int year = cal.get(Calendar.YEAR);
        binding.pickerYear.setMinValue(1900);
        binding.pickerYear.setMaxValue(year);
        binding.pickerYear.setValue(year);

        binding.okBtn.setOnClickListener(this);
        binding.cancelBtn.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if(window != null){
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        setCancelable(false);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if(window != null){
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cancelBtn){
            dismiss();
        }

        if(v.getId() == R.id.okBtn){
            if (listener != null) {
                listener.onDateSet(null, binding.pickerYear.getValue(), binding.pickerMonth.getValue(), 0);
            }
            dismiss();
        }
    }
}
