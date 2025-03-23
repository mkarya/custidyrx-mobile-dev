package com.custodyrx.library.label.ui.home.inventory;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.custodyrx.app.R;
import com.custodyrx.library.label.adapter.BaseMultiItemRecyclerAdapter;
import com.custodyrx.library.label.adapter.BaseViewHolder;
import com.custodyrx.library.label.bean.InventoryTagBean;
import com.google.gson.Gson;


/**
 * @author naz
 * Date 2020/4/3
 */
public class PerformInventoryTagAdapter extends BaseMultiItemRecyclerAdapter<InventoryTagBean> {
    private static OnItemLongClickListener sOnItemLongClickListener;
    private static boolean sEnablePhase;

    public PerformInventoryTagAdapter(Context ctx) {
        super(ctx, R.layout.item_perform_inventory_tag);
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
        boolean onItemLongClick(PerformInventoryTagAdapter adapter, View view, int position);
    }

    private static class InnerVH extends BaseViewHolder<InventoryTagBean> {
        private View mRootView;

        private TextView mEpc;



        public InnerVH(View view) {
            super(view);
            mRootView = view.findViewById(R.id.cl_parent);
            mEpc = view.findViewById(R.id.tv_tag_epc);


            /*mRootView.setOnLongClickListener(v -> {
                sOnItemLongClickListener.onItemLongClick((PerformInventoryTagAdapter) mAdapter, mRootView, getAdapterPosition());
                return true;
            });*/
        }

        @Override
        public void onBindData(int adapterPosition) {
            InventoryTagBean item = getData(adapterPosition);
            if (item == null) {
                return;
            }
            mEpc.setText(item.getEpc());

        }
    }
}
