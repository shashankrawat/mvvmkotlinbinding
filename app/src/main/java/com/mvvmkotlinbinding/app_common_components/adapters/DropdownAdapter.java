package com.mvvmkotlinbinding.app_common_components.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.mvvmkotlinbinding.app_common_components.listeners.OnItemClickedListener;
import com.mvvmwithdatabinding.R;

import java.util.List;

public class DropdownAdapter extends RecyclerView.Adapter<DropdownAdapter.ViewHolder>
{
    private final List<String> dataList;
    private final OnItemClickedListener<String> listener;

    public DropdownAdapter(List<String> dataList, OnItemClickedListener<String> listener){
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dropdown_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatTextView ddItemName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ddItemName = itemView.findViewById(R.id.dd_item_tv);
            itemView.setOnClickListener(this);
        }

        public void setData(String item){
            ddItemName.setText(item);
        }

        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onItemClicked(v, dataList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}
