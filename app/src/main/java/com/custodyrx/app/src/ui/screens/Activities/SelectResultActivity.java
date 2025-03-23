package com.custodyrx.app.src.ui.screens.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custodyrx.app.BaseActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.screens.Activities.Comments.Pages.CommentsActivity;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Adapters.SelectResultsAdapter;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.ProductItemData;
import com.custodyrx.library.label.ui.home.inventory.ItemSpacingDecoration;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SelectResultActivity extends BaseActivity {

    private String TAG = "SelectResultActivity";

    RecyclerView rvlocalInventory;

    private LinearLayoutManager mLayoutManager;

    private SelectResultsAdapter selectResultsAdapter;

    List<AllProduct> allProducts;

    MaterialButton all, none, next;

    TextView tv_qty, tv_unit_count;

    private int totalQty;
    private int totalUnitCount;




    private SelectResultsAdapter.OnItemSelectedClickListener OnItemSelectedClickListener = this::itemSelectedClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_result);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        initView();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_select_result;
    }

    private void initView() {

        all = findViewById(R.id.all);
        none = findViewById(R.id.none);
        next = findViewById(R.id.next);
        tv_qty = findViewById(R.id.tv_qty);
        tv_unit_count = findViewById(R.id.tv_unit_count);
        rvlocalInventory = findViewById(R.id.rvlocalInventory);


        String allProductJson = getIntent().getStringExtra("ALL_PRODUCT");
        Type listType = new TypeToken<List<AllProduct>>() {
        }.getType();
        allProducts = new Gson().fromJson(allProductJson, listType);


        setLocalInventory();

        clickListeners();
    }

    private void setLocalInventory() {

        mLayoutManager = new LinearLayoutManager(SelectResultActivity.this);
        selectResultsAdapter = new SelectResultsAdapter(SelectResultActivity.this);
        selectResultsAdapter.setData(allProducts);

        selectResultsAdapter.setOnItemSelectedClickListener(OnItemSelectedClickListener);

        tv_qty.setText(selectResultsAdapter.getTotalInventoryQuantity() + "x");
        tv_unit_count.setText(selectResultsAdapter.getTotalUnitCount() + "");


        // rvlocalInventory
        rvlocalInventory.setHasFixedSize(true);
        rvlocalInventory.setLayoutManager(mLayoutManager);
        rvlocalInventory.setAdapter(selectResultsAdapter);
        rvlocalInventory.addItemDecoration(new ItemSpacingDecoration(10));
        rvlocalInventory.setItemAnimator(null);
        selectResultsAdapter.selectAll(false);
        selectResultsAdapter.notifyDataSetChanged();


    }

    private void itemSelectedClick(boolean cheked, int position) {

        totalQty = 0;
        totalUnitCount = 0;
        List<AllProduct> selectedItems = selectResultsAdapter.getSelectedProductItems();
        for (AllProduct product : selectedItems) {
            totalQty += product.getInventoryQuantity();
            totalUnitCount += product.getTotalUnitCount();
        }

        tv_qty.setText(totalQty + "x");
        tv_unit_count.setText(totalUnitCount+"");
    }

    private void clickListeners() {
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResultsAdapter.selectAll(true);
                tv_qty.setText(selectResultsAdapter.getTotalInventoryQuantity() + "x");
                tv_unit_count.setText(selectResultsAdapter.getTotalUnitCount() + "");
            }
        });


        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResultsAdapter.selectAll(false);
                tv_qty.setText(selectResultsAdapter.getTotalInventoryQuantity() + "x");
                tv_unit_count.setText(selectResultsAdapter.getTotalUnitCount() + "");
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AllProduct> selectedProductItems = selectResultsAdapter.getSelectedProductItems();

                if (selectedProductItems.size() > 0){
                    Gson gson = new Gson();
                    String productItemJson = gson.toJson(selectedProductItems);
                    Intent intent = new Intent(SelectResultActivity.this, CommentsActivity.class);
                    intent.putExtra("ALL_PRODUCT", productItemJson);
                    intent.putExtra("TOTAL_QUANTITY", tv_unit_count.getText().toString());
                    intent.putExtra("LOCATION_GUID", getIntent().getStringExtra("LOCATION_GUID"));
                    intent.putExtra("LOCATION_NAME", getIntent().getStringExtra("LOCATION_NAME"));
                    startActivity(intent);
                }else {
                    showSnackBar("Please select inventory!");
                }

            }
        });
    }


}