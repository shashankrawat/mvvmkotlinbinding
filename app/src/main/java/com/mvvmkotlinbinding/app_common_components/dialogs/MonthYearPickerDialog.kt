package com.mvvmkotlinbinding.app_common_components.dialogs

import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseDialogFragment
import com.mvvmwithdatabinding.R
import com.mvvmwithdatabinding.databinding.DialogMonthYearPickerBinding
import java.util.*

class MonthYearPickerDialog : BaseDialogFragment(), View.OnClickListener {
    private var listener: OnDateSetListener? = null
    private var binding: DialogMonthYearPickerBinding? = null
    fun setListener(listener: OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMonthYearPickerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cal = Calendar.getInstance()
        binding!!.pickerMonth.minValue = 1
        binding!!.pickerMonth.maxValue = 12
        binding!!.pickerMonth.value = cal[Calendar.MONTH] + 1
        val year = cal[Calendar.YEAR]
        binding!!.pickerYear.minValue = 1900
        binding!!.pickerYear.maxValue = year
        binding!!.pickerYear.value = year
        binding!!.okBtn.setOnClickListener(this)
        binding!!.cancelBtn.setOnClickListener(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.cancelBtn) {
            dismiss()
        }
        if (v.id == R.id.okBtn) {
            if (listener != null) {
                listener!!.onDateSet(
                    null,
                    binding!!.pickerYear.value,
                    binding!!.pickerMonth.value,
                    0
                )
            }
            dismiss()
        }
    }

    companion object {
        const val TAG = "MonthYearPickerDialog"
        @JvmStatic
        val instance: MonthYearPickerDialog
            get() = MonthYearPickerDialog()
    }
}