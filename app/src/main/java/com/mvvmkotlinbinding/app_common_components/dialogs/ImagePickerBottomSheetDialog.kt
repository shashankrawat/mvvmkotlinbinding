package com.mvvmkotlinbinding.app_common_components.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mvvmkotlinbinding.app_common_components.listeners.PickerOptionsListener
import com.mvvmwithdatabinding.R
import com.mvvmwithdatabinding.databinding.BottomsheetImagePickerOptionBinding

class ImagePickerBottomSheetDialog : BottomSheetDialogFragment(), View.OnClickListener {
    private var listener: PickerOptionsListener? = null
    private var binding: BottomsheetImagePickerOptionBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetImagePickerOptionBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    fun setListener(listener: PickerOptionsListener?) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.closeDialogBtn.setOnClickListener(this)
        binding!!.cameraChooseBtn.setOnClickListener(this)
        binding!!.galleryChooseBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeDialogBtn -> dismiss()
            R.id.cameraChooseBtn -> {
                if (listener != null) {
                    listener!!.onCameraOptionClick()
                }
                dismiss()
            }
            R.id.galleryChooseBtn -> {
                if (listener != null) {
                    listener!!.onGalleryOptionClick()
                }
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "ImagePickerBottomSheetDialog"
        val instance: ImagePickerBottomSheetDialog
            get() = ImagePickerBottomSheetDialog()
    }
}