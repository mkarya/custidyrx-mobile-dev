package com.custodyrx.app.src.ui.screens.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.custodyrx.app.BaseActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.Pages.PerformInventoryActivity;

public class InventoryTypeActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_type);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.ll_full_inventory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryTypeActivity.this, PerformInventoryActivity.class);
                intent.putExtra("LOCATION", "Stock Room");
                startActivity(intent);

            }
        });

        findViewById(R.id.ll_location_inventory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InventoryTypeActivity.this, SelectLocationActivity.class));
            }
        });


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_inventory_type;
    }
}