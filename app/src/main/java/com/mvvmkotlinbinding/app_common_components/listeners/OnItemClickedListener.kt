package com.mvvmkotlinbinding.app_common_components.listeners;


import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public interface OnItemClickedListener<T> {

    /**
     * @param v    The {@code View} that was clicked
     * @param data The data object that is associated with the {@code View} that was clicked
     */
    void onItemClicked(@NonNull View v, @Nullable T data, int position);

    /**
     * @param v    The {@code View} that was long clicked
     * @param data The data object that is associated with the {@code View} that was clicked
     * @return {@code true} if the long click was consumed, {@code false} if not.
     */
    boolean onItemLongClicked(@NonNull View v, @Nullable T data, int position);
}
