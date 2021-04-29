package com.mvvmkotlinbinding.app_common_components.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.mvvmkotlinbinding.app_common_components.listeners.DDItemListener;
import com.mvvmwithdatabinding.R;

import java.util.List;

public class CustomDropdownAdapter<T> extends RecyclerView.Adapter<CustomDropdownAdapter<T>.ViewHolder> {

    private final List<T> dataList;
    private final DDItemListener<T> listener;
    private final int ddType;

    public CustomDropdownAdapter(List<T> dataList, DDItemListener<T> listener, int ddType){
        this.dataList = dataList;
        this.listener = listener;
        this.ddType = ddType;
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

        public void setData(T item){
            String ddText = "";
            /*if(ddType == AppConstants.DD_DISTRICT_TYPE){
                DistrictsBean distItem = (DistrictsBean) item;
                ddText = distItem.getDistName();
            }*/

            ddItemName.setText(ddText);

        }

        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onItemClicked(dataList.get(getAdapterPosition()), getAdapterPosition(), ddType);
            }
        }
    }
}
