package com.custodyrx.app.src.ui.screens.Activities.TagList.Pages;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custodyrx.app.BaseActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Items;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Activities.SelectResultActivity;
import com.custodyrx.app.src.ui.screens.Adapters.SelectResultsAdapter;
import com.custodyrx.library.label.ui.home.inventory.ItemSpacingDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TagListActivity extends BaseActivity {


    private String TAG = "TagListActivity";

    RecyclerView rvProductItems;
    private LinearLayoutManager mLayoutManager;

    private TagListAdapter tagListAdapter;

    TextView tv_qty, tv_unit_count;

    private ImageView iv_tag;

    AllProduct allProducts;

    List<Items> productItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        initView();

    }

    private void initView() {

        tv_qty = findViewById(R.id.tv_quantity);
        tv_unit_count = findViewById(R.id.tv_unit_count);
        rvProductItems = findViewById(R.id.rvProductItems);
        iv_tag = findViewById(R.id.iv_tag);


        String allProductJson = getIntent().getStringExtra("ALL_PRODUCT");
        allProducts = new Gson().fromJson(allProductJson, AllProduct.class);


        if (!allProducts.getName().equalsIgnoreCase("Unknown")) {
            iv_tag.setColorFilter(ContextCompat.getColor(this, R.color.lightGreen), PorterDuff.Mode.SRC_IN);
        } else {

            iv_tag.setColorFilter(ContextCompat.getColor(this, R.color.unknownTagRoundColor), PorterDuff.Mode.SRC_IN);
        }


        setLocalInventory();


    }

    private void setLocalInventory() {

        productItems = new ArrayList<>();
        productItems.addAll(allProducts.getProductItems());
        mLayoutManager = new LinearLayoutManager(TagListActivity.this);
        tagListAdapter = new TagListAdapter(TagListActivity.this);
        tagListAdapter.setData(productItems);
        tagListAdapter.setAllProductName(allProducts.getName());

        tv_qty.setText(allProducts.getInventoryQuantity() + "");
        tv_unit_count.setText(allProducts.getTotalUnitCount() + "x");

        // rvlocalInventory
        rvProductItems.setHasFixedSize(true);
        rvProductItems.setLayoutManager(mLayoutManager);
        rvProductItems.setAdapter(tagListAdapter);
        rvProductItems.addItemDecoration(new ItemSpacingDecoration(10));
        rvProductItems.setItemAnimator(null);
        tagListAdapter.notifyDataSetChanged();


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tag_list;
    }
}