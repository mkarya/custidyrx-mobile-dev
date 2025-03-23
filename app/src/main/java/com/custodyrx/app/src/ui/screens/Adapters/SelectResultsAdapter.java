package com.custodyrx.app.src.ui.screens.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.ProductItemData;
import com.custodyrx.library.label.adapter.BaseMultiItemRecyclerAdapter;
import com.custodyrx.library.label.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author naz
 * Date 2020/4/3
 */
public class SelectResultsAdapter extends BaseMultiItemRecyclerAdapter<AllProduct> {
    private static OnItemLongClickListener sOnItemLongClickListener;
    private static OnItemSelectedClickListener onItemSelectedClickListener;
    private static boolean sEnablePhase;

    private static Map<Integer, Boolean> checkedStateMap = new HashMap<>();

    private int totalInventoryQuantity;
    private int totalUnitCount;


    public SelectResultsAdapter(Context ctx) {
        super(ctx, R.layout.item_select_results_inventory_tag);
    }

    public List<AllProduct> getSelectedProductItems() {
        List<AllProduct> selectedItems = new ArrayList<>();
        totalInventoryQuantity = 0;  // Reset the total inventory quantity
        totalUnitCount = 0;  // Reset the total unit count

        for (int i = 0; i < getData().size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (checkedStateMap.getOrDefault(i, false)) {
                    AllProduct product = getData().get(i);
                    selectedItems.add(product);

                    // Calculate total inventory quantity and total unit count
                    totalInventoryQuantity += product.getInventoryQuantity();
                    totalUnitCount += product.getTotalUnitCount();
                }
            } else {
                if (checkedStateMap.containsKey(i) && checkedStateMap.get(i)) {
                    AllProduct product = getData().get(i);
                    selectedItems.add(product);

                    // Calculate total inventory quantity and total unit count
                    totalInventoryQuantity += product.getInventoryQuantity();
                    totalUnitCount += product.getTotalUnitCount();
                }
            }
        }


        return selectedItems;
    }


    public void selectAll(boolean select) {
        totalInventoryQuantity = 0;
        totalUnitCount = 0;


        for (int i = 0; i < getData().size(); i++) {
            checkedStateMap.put(i, select);
            if (select) {
                totalInventoryQuantity += getData().get(i).getInventoryQuantity();
                totalUnitCount += getData().get(i).getTotalUnitCount();
            }
        }
        notifyDataSetChanged();
    }

    private void updateTotalInventoryQuantity(AllProduct item, boolean isChecked) {

        if (isChecked) {
            totalInventoryQuantity += item.getInventoryQuantity();
            totalUnitCount += item.getTotalUnitCount();
        } else {
            totalInventoryQuantity -= item.getInventoryQuantity();
            totalUnitCount -= item.getTotalUnitCount();
        }

        notifyDataSetChanged();
    }

    public int getTotalUnitCount() {
        return totalUnitCount;
    }

    public int getTotalInventoryQuantity() {
        return totalInventoryQuantity;
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
        boolean onItemLongClick(SelectResultsAdapter adapter, View view, int position);
    }

    public void setOnItemSelectedClickListener(OnItemSelectedClickListener listener) {
        onItemSelectedClickListener = listener;
    }

    public interface OnItemSelectedClickListener {
        void onItemSelectedClick(boolean checked, int position);
    }


    private static class InnerVH extends BaseViewHolder<AllProduct> {
        private View mRootView;

        private TextView mEpc,tv_qty, tv_unit_count;

        private CheckBox cb_tag_epc;


        public InnerVH(View view) {
            super(view);
            mRootView = view.findViewById(R.id.cl_parent);
            mEpc = view.findViewById(R.id.tv_tag_epc);
            cb_tag_epc = view.findViewById(R.id.cb_tag_epc);
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


            mEpc.setText(item.getName());
            cb_tag_epc.setOnCheckedChangeListener(null);

            tv_qty.setText(item.getInventoryQuantity() + "x");
            tv_unit_count.setText("" + item.getTotalUnitCount());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                boolean isChecked = checkedStateMap.getOrDefault(adapterPosition, false);
                cb_tag_epc.setChecked(isChecked);
            } else {
                boolean isChecked = checkedStateMap.containsKey(adapterPosition) ? checkedStateMap.get(adapterPosition) : false;
                cb_tag_epc.setChecked(isChecked);
            }

            cb_tag_epc.setOnCheckedChangeListener((buttonView, checked) -> {
                checkedStateMap.put(adapterPosition, checked);


                if (onItemSelectedClickListener != null) {
                    onItemSelectedClickListener.onItemSelectedClick(checked, adapterPosition);
                }


            });

        }

        private String formatEPC(String epc) {
            if (epc == null || epc.length() < 2) return epc;
            return epc.replaceAll("(.{2})", "$1 ").trim();
        }
    }
}
