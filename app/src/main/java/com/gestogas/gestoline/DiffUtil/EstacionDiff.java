package com.gestogas.gestoline.DiffUtil;

import androidx.recyclerview.widget.DiffUtil;

import com.gestogas.gestoline.data.dataEstacion;

import java.util.List;

public class EstacionDiff extends DiffUtil.Callback{

    private final List<dataEstacion> oldList;
    private final List<dataEstacion> newList;

    public EstacionDiff(List<dataEstacion> oldList, List<dataEstacion> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        dataEstacion oldItem = oldList.get(oldItemPosition);
        dataEstacion newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}

