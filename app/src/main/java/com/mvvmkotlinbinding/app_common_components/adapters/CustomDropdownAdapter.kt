package com.mvvmkotlinbinding.app_common_components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mvvmkotlinbinding.app_common_components.listeners.DDItemListener
import com.mvvmwithdatabinding.R

class CustomDropdownAdapter<T>(
    private val dataList: List<T>?,
    private val listener: DDItemListener<T>?,
    private val ddType: Int) : RecyclerView.Adapter<CustomDropdownAdapter<T>.ViewHolder>()
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
        fun setData(item: T) {
            val ddText = ""
            /*if(ddType == AppConstants.DD_DISTRICT_TYPE){
                DistrictsBean distItem = (DistrictsBean) item;
                ddText = distItem.getDistName();
            }*/ddItemName.text = ddText
        }

        override fun onClick(v: View) {
            listener?.onItemClicked(dataList!![adapterPosition], adapterPosition, ddType)
        }

        init {
            ddItemName = itemView.findViewById(R.id.dd_item_tv)
            itemView.setOnClickListener(this)
        }
    }
}