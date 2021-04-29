package com.mvvmkotlinbinding.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.mvvmkotlinbinding.app_common_components.adapters.DropdownAdapter;
import com.mvvmkotlinbinding.app_common_components.listeners.OnItemClickedListener;
import com.mvvmwithdatabinding.R;

import java.util.List;


public class AppCustomDropdownMenu extends PopupWindow
{
    private final Context context;
    private final List<String> ddData;
    private final ObservableField<String> ddViewField;

    public AppCustomDropdownMenu(Context context, ObservableField<String> ddViewField, List<String> ddData){
        this.context = context;
        this.ddViewField = ddViewField;
        this.ddData = ddData;

        setUpView();
    }

    private void setUpView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_content_view, null);

        RecyclerView ddRV = view.findViewById(R.id.dropdown_rv);
        ddRV.setHasFixedSize(true);
        DropdownAdapter adapter = new DropdownAdapter(ddData, new OnItemClickedListener<String>() {
            @Override
            public void onItemClicked(@NonNull View v, @Nullable String data, int position) {
                dismiss();
                ddViewField.set(data);
            }

            @Override
            public boolean onItemLongClicked(@NonNull View v, @Nullable String data, int position) {
                return false;
            }
        });

        ddRV.setAdapter(adapter);

        setContentView(view);

    }


}
