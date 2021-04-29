package com.mvvmkotlinbinding.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.RecyclerView;

import com.mvvmkotlinbinding.app_common_components.adapters.CustomDropdownAdapter;
import com.mvvmkotlinbinding.app_common_components.listeners.DDItemListener;
import com.mvvmwithdatabinding.R;

import java.util.List;

public class CustomDDMenu<T> extends PopupWindow
{
    private final Context context;
    private final List<T> ddData;
    private final DDItemListener<T> listener;
    private final int ddType;

    public CustomDDMenu(Context context, List<T> ddData, DDItemListener<T> listener, int ddType){
        this.context = context;
        this.ddData = ddData;
        this.listener = listener;
        this.ddType = ddType;
        setUpView();
    }

    private void setUpView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_content_view, null);

        RecyclerView ddRV = view.findViewById(R.id.dropdown_rv);
        ddRV.setHasFixedSize(true);
        CustomDropdownAdapter<T> adapter = new CustomDropdownAdapter<T>(ddData, listener, ddType);

        ddRV.setAdapter(adapter);

        setContentView(view);

    }
}
