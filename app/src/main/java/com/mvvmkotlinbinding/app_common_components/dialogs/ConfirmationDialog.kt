package com.mvvmkotlinbinding.app_common_components.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseDialogFragment
import com.mvvmkotlinbinding.app_common_components.listeners.ConfirmationDialogListener
import com.mvvmwithdatabinding.R
import com.mvvmwithdatabinding.databinding.DialogConfirmationViewBinding

class ConfirmationDialog : BaseDialogFragment(), View.OnClickListener {
    private var titleText: String? = null
    private var msg: String? = null
    private var listener: ConfirmationDialogListener? = null
    private var binding: DialogConfirmationViewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            titleText = args.getString("TITLE")
            msg = args.getString("MESSAGE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogConfirmationViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    fun setListener(listener: ConfirmationDialogListener?) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!TextUtils.isEmpty(titleText)) {
            binding!!.titleText.text = titleText
        }
        if (!TextUtils.isEmpty(msg)) {
            binding!!.titleDesc.text = msg
        }
        binding!!.yes.setOnClickListener(this)
        binding!!.no.setOnClickListener(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.yes -> {
                if (listener != null) {
                    listener!!.onOkClicked()
                }
                dismiss()
            }
            R.id.no -> dismiss()
        }
    }

    companion object {
        const val TAG = "ConfirmationDialog"
        fun getInstance(args: Bundle?): ConfirmationDialog {
            val dialog = ConfirmationDialog()
            dialog.arguments = args
            return dialog
        }
    }
}