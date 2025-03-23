package com.custodyrx.library.label.ui.home.inventory;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.ProductItemData;
import com.custodyrx.library.label.adapter.BaseMultiItemRecyclerAdapter;
import com.custodyrx.library.label.adapter.BaseViewHolder;
import com.custodyrx.library.label.bean.InventoryTagBean;


/**
 * @author naz
 * Date 2020/4/3
 */
public class PerformLocalInventoryTagAdapter extends BaseMultiItemRecyclerAdapter<AllProduct> {
    private static OnItemLongClickListener sOnItemLongClickListener;
    private static boolean sEnablePhase;

    public PerformLocalInventoryTagAdapter(Context ctx) {
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
    protected Class<? extends BaseViewHolder<AllProduct>> onBindViewTypeToViewHolder(int viewType) {
        return InnerVH.class;
    }

    public void setOnItemChildLongClickListener(OnItemLongClickListener l) {
        sOnItemLongClickListener = l;
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(PerformLocalInventoryTagAdapter adapter, View view, int position);
    }

    private static class InnerVH extends BaseViewHolder<AllProduct> {
        private View mRootView, unknown_label, unknown_round;

        private TextView mEpc, tv_qty, tv_unit_count;


        public InnerVH(View view) {
            super(view);
            mRootView = view.findViewById(R.id.cl_parent);
            unknown_label = view.findViewById(R.id.unknown_label);
            unknown_round = view.findViewById(R.id.unknown_round);
            mEpc = view.findViewById(R.id.tv_tag_epc);
            tv_qty = view.findViewById(R.id.tv_qty);
            tv_unit_count = view.findViewById(R.id.tv_unit_count);


            /*mRootView.setOnLongClickListener(v -> {
                sOnItemLongClickListener.onItemLongClick((PerformInventoryTagAdapter) mAdapter, mRootView, getAdapterPosition());
                return true;
            });*/
        }

        @Override
        public void onBindData(int adapterPosition) {
            AllProduct item = getData(adapterPosition);
            if (item == null) {
                return;
            }

            Context context = mRootView.getContext();


            GradientDrawable background = (GradientDrawable) unknown_round.getBackground();
            mEpc.setTextColor(context.getResources().getColor(R.color.black));
            unknown_label.setBackgroundColor(context.getResources().getColor(R.color.tagColor));
            background.setColor(ContextCompat.getColor(context, R.color.lightGreen));

            mEpc.setText(item.getName());
            tv_qty.setText(item.getInventoryQuantity() + "x");
            tv_unit_count.setText("" + item.getTotalUnitCount());

            if ("Unknown".equals(item.getName())) {
                mEpc.setTextColor(context.getResources().getColor(R.color.unknownTagColor));
                unknown_label.setBackgroundColor(context.getResources().getColor(R.color.unknownTagColor));
                background.setColor(ContextCompat.getColor(context, R.color.unknownTagRoundColor));
            }


        }

        private String formatEPC(String epc) {
            if (epc == null || epc.length() < 2) return epc;
            return epc.replaceAll("(.{2})", "$1 ").trim();
        }
    }
}
