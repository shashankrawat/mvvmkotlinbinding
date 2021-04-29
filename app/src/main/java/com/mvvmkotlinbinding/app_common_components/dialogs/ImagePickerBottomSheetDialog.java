package com.mvvmkotlinbinding.app_common_components.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mvvmkotlinbinding.app_common_components.listeners.PickerOptionsListener;
import com.mvvmwithdatabinding.R;
import com.mvvmwithdatabinding.databinding.BottomsheetImagePickerOptionBinding;

public class ImagePickerBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG = "ImagePickerBottomSheetDialog";

    private PickerOptionsListener listener;
    private BottomsheetImagePickerOptionBinding binding;

    public static ImagePickerBottomSheetDialog getInstance() {
        return new ImagePickerBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomsheetImagePickerOptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void setListener(PickerOptionsListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.closeDialogBtn.setOnClickListener(this);
        binding.cameraChooseBtn.setOnClickListener(this);
        binding.galleryChooseBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.closeDialogBtn:
                    dismiss();
                break;

            case R.id.cameraChooseBtn:
                    if(listener != null){
                        listener.onCameraOptionClick();
                    }
                    dismiss();
                break;

            case R.id.galleryChooseBtn:
                if(listener != null){
                    listener.onGalleryOptionClick();
                }
                dismiss();
                break;
        }
    }
}
