package com.custodyrx.app.src.ui.screens.Activities.TagList.Pages;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Items;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.library.label.adapter.BaseMultiItemRecyclerAdapter;
import com.custodyrx.library.label.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagListAdapter extends BaseMultiItemRecyclerAdapter<Items> {

    public static String allProductName = "";


    public TagListAdapter(Context ctx) {
        super(ctx, R.layout.item_tag_list);
    }

    public void setAllProductName(String name) {
        this.allProductName = name;
        notifyDataSetChanged();
    }

    @Override
    protected Class<? extends BaseViewHolder<Items>> onBindViewTypeToViewHolder(int viewType) {
        return InnerVH.class;
    }


    private static class InnerVH extends BaseViewHolder<Items> {
        private View mRootView, unknownLabel, unknownRound;
        private TextView mEpc, tvProductName, tvUnitCount;


        public InnerVH(View view) {
            super(view);
            mRootView = view.findViewById(R.id.cl_parent);
            unknownLabel = view.findViewById(R.id.unknown_label);
            unknownRound = view.findViewById(R.id.unknown_round);
            mEpc = view.findViewById(R.id.tv_tag_epc);
            tvProductName = view.findViewById(R.id.tv_product_name);
            tvUnitCount = view.findViewById(R.id.tv_unit_count);

        }

        @Override
        public void onBindData(int adapterPosition) {
            Items item = getData(adapterPosition);
            if (item == null) {
                return;
            }

            Context context = mRootView.getContext();

            GradientDrawable background = (GradientDrawable) unknownRound.getBackground();
            unknownLabel.setBackgroundColor(context.getResources().getColor(R.color.tagColor));
            background.setColor(ContextCompat.getColor(context, R.color.lightGreen));

            mEpc.setText(item.getItemEpc());
            tvUnitCount.setText(String.valueOf(item.getUnitCount()));
            tvProductName.setText(allProductName);

            if ("Unknown".equals(allProductName)) {
                unknownLabel.setBackgroundColor(context.getResources().getColor(R.color.unknownTagColor));
                background.setColor(ContextCompat.getColor(context, R.color.unknownTagRoundColor));
            }

        }
    }
}

