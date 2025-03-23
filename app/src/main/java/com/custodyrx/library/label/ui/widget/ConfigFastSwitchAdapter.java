package com.custodyrx.library.label.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.custodyrx.app.R;
import com.custodyrx.app.databinding.ItemInventoryConfigSwitchBinding;
import com.custodyrx.library.label.bean.ConfigFast;
import com.payne.reader.bean.config.AntennaCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author naz
 * Date 2020/11/24
 */
public class ConfigFastSwitchAdapter extends RecyclerView.Adapter<ConfigFastSwitchAdapter.BaseViewHolder> {
    private List<ConfigFast> mData;
    private final List<String> mFourChannels;
    private final List<String> mEightChannels;
    private final List<String> mHighEightChannels;

    public ConfigFastSwitchAdapter(Context context) {
        this.mFourChannels = Arrays.asList(context.getResources().getStringArray(R.array.four_channel));
        this.mEightChannels = Arrays.asList(context.getResources().getStringArray(R.array.eight_channel));
        this.mHighEightChannels = Arrays.asList(context.getResources().getStringArray(R.array.high_eight_channel));
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
                List<String> channels;
                int selectId = index;
                if (index > AntennaCount.EIGHT_CHANNELS.getValue()) {
                    channels = mHighEightChannels;
                    selectId = index % 8;
                } else if (index > AntennaCount.FOUR_CHANNELS.getValue() || newCount > AntennaCount.FOUR_CHANNELS.getValue()) {
                    channels = mEightChannels;
                } else {
                    channels = mFourChannels;
                }
                mData.add(new ConfigFast(channels, selectId, 1));
            }
            if (oldCount > 0 && oldCount < AntennaCount.EIGHT_CHANNELS.getValue()) {
                for (int i = 0; i < oldCount; i++) {
                    mData.get(i).setChannels(mEightChannels);
                    notifyItemChanged(i);
                }
            }
            notifyItemRangeInserted(oldCount, newCount - oldCount);
        } else if (newCount < oldCount) {
            int index = oldCount - 1;
            for (; index > newCount; index--) {
                mData.remove(index);
            }
            if (newCount > 0 && newCount < AntennaCount.EIGHT_CHANNELS.getValue()) {
                for (int i = 0; i < newCount; i++) {
                    mData.get(i).setChannels(mFourChannels);
                    notifyItemChanged(i);
                }
            }
            notifyItemRangeRemoved(newCount, oldCount - newCount);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryConfigSwitchBinding binding = ItemInventoryConfigSwitchBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ConfigFast configFast = mData.get(position);
        holder.binding.spFastAnt.attachDataSource(configFast.getChannels());
        holder.binding.spFastAnt.setSelectedIndex(configFast.getSelectId());
        holder.binding.etFastAntTime.setText(String.valueOf(configFast.getRepeat()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        private final ItemInventoryConfigSwitchBinding binding;

        public BaseViewHolder(@NonNull ItemInventoryConfigSwitchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
