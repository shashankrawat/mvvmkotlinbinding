package com.mvvmkotlinbinding.app_common_components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mvvmkotlinbinding.app_common_components.listeners.OnItemClickedListener
import com.mvvmwithdatabinding.R

class DropdownAdapter(
    private val dataList: List<String>?,
    private val listener: OnItemClickedListener<String?>
) : RecyclerView.Adapter<DropdownAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_dropdown_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataList!![position])
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var ddItemName: AppCompatTextView
        fun setData(item: String?) {
            ddItemName.text = item
        }

        override fun onClick(v: View) {
            listener?.onItemClicked(v, dataList!![adapterPosition], adapterPosition)
        }

        init {
            ddItemName = itemView.findViewById(R.id.dd_item_tv)
            itemView.setOnClickListener(this)
        }
    }
}