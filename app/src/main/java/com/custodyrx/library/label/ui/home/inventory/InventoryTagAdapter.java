package com.custodyrx.library.label.ui.home.inventory;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.custodyrx.app.R;
import com.custodyrx.library.label.adapter.BaseMultiItemRecyclerAdapter;
import com.custodyrx.library.label.adapter.BaseViewHolder;
import com.custodyrx.library.label.bean.InventoryTagBean;


/**
 * @author naz
 * Date 2020/4/3
 */
public class InventoryTagAdapter extends BaseMultiItemRecyclerAdapter<InventoryTagBean> {
    private static OnItemLongClickListener sOnItemLongClickListener;
    private static boolean sEnablePhase;

    public InventoryTagAdapter(Context ctx) {
        super(ctx, R.layout.item_inventory_tag);
    }

    public void enablePhase(boolean enable) {
        if (sEnablePhase != enable) {
            sEnablePhase = enable;
            notifyDataSetChanged();
        }
    }

    public boolean isPhase() {
        return sEnablePhase;
    }

    @Override
    protected Class<? extends BaseViewHolder<InventoryTagBean>> onBindViewTypeToViewHolder(int viewType) {
        return InnerVH.class;
    }

    public void setOnItemChildLongClickListener(OnItemLongClickListener l) {
        sOnItemLongClickListener = l;
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(InventoryTagAdapter adapter, View view, int position);
    }

    private static class InnerVH extends BaseViewHolder<InventoryTagBean> {
        private View mRootView;
        private TextView mId;
        private TextView mEpc;
        private TextView mPc;
        private TextView mTimes;
        private TextView mRssi;
        private TextView mAnti;
        private TextView mFreq;
        private TextView mPhase;

        public InnerVH(View view) {
            super(view);
            mRootView = view.findViewById(R.id.cl_parent);
            mId = view.findViewById(R.id.tv_tag_id);
            mEpc = view.findViewById(R.id.tv_tag_epc);
            mPc = view.findViewById(R.id.tv_tag_pc);
            mTimes = view.findViewById(R.id.tv_tag_times);
            mRssi = view.findViewById(R.id.tv_tag_rssi);
            mAnti = view.findViewById(R.id.tvAnti);
            mFreq = view.findViewById(R.id.tv_tag_freq);
            mPhase = view.findViewById(R.id.tv_tag_phase);

            mRootView.setOnLongClickListener(v -> {
                sOnItemLongClickListener.onItemLongClick((InventoryTagAdapter) mAdapter, mRootView, getAdapterPosition());
                return true;
            });
        }

        @Override
        public void onBindData(int adapterPosition) {
            InventoryTagBean item = getData(adapterPosition);
            if (item == null) {
                return;
            }
            mId.setText(String.valueOf(item.getPosition() + 1));
            mEpc.setText(item.getEpc());
            mPc.setText(item.getPc());
            mTimes.setText(item.getTimes());
            mRssi.setText(item.getRssi());
            mAnti.setText("" + item.getAntId());
            mFreq.setText(item.getFreq());

            if (sEnablePhase) {
                mPhase.setVisibility(View.VISIBLE);
                mPhase.setText(item.getPhase());
            } else {
                mPhase.setVisibility(View.GONE);
            }
        }
    }
}
