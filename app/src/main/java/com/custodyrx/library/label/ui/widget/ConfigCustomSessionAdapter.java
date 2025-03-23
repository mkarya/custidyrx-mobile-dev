package com.custodyrx.library.label.ui.widget;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.custodyrx.app.databinding.ItemInventoryConfigCustomBinding;
import com.payne.reader.bean.config.AntennaCount;

import java.util.ArrayList;
import java.util.List;

/**
 * @author naz
 * Date 2020/11/24
 */
public class ConfigCustomSessionAdapter extends RecyclerView.Adapter<ConfigCustomSessionAdapter.BaseViewHolder> {
    private final List<Boolean> mData;

    public ConfigCustomSessionAdapter() {
        this.mData = new ArrayList<>();
    }

    public void refreshAntCount(AntennaCount antennaCount) {
        if (antennaCount == AntennaCount.SINGLE_CHANNEL) {
            mData.clear();
            notifyDataSetChanged();
            return;
        }
        int oldCount = mData.size();
        int newCount = antennaCount.getValue();
        if (newCount > oldCount) {
            int index = mData.size();
            for (; index < newCount; index++) {
                mData.add(index == 0);
            }
            notifyItemRangeInserted(oldCount, newCount - oldCount);
        } else if (newCount < oldCount) {
            int index = oldCount - 1;
            for (; index > newCount; index--) {
                mData.remove(index);
            }
            notifyItemRangeRemoved(newCount, oldCount - newCount);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryConfigCustomBinding binding = ItemInventoryConfigCustomBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.binding.cbAnt.setChecked(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        private final ItemInventoryConfigCustomBinding binding;

        public BaseViewHolder(@NonNull ItemInventoryConfigCustomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
