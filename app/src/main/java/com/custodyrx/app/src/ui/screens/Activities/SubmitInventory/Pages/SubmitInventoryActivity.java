package com.custodyrx.app.src.ui.screens.Activities.SubmitInventory.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.custodyrx.app.BaseActivity;
import com.custodyrx.app.MainActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.database.DatabaseHandler;
import com.custodyrx.app.src.ui.Api.APIClient;
import com.custodyrx.app.src.ui.Api.APIConstant;
import com.custodyrx.app.src.ui.Api.APIInterface;
import com.custodyrx.app.src.ui.Constants.AppUtils;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.SubmitDialog;
import com.custodyrx.app.src.ui.screens.Activities.Comments.Pages.CommentsActivity;
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.SubmitInventoryRequest;
import com.custodyrx.app.src.ui.screens.Activities.Comments.ResponseModel.SubmitInventoryResponse;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitInventoryActivity extends BaseActivity {

    private String TAG = "SubmitInventoryActivity";

    DatabaseHandler database;
    StorageHelper storageHelper;

    APIInterface apiInterface;
    private TextView tv_datetime, tv_total_quantity, tv_inventory_status, tv_inventoryType, tv_location;

    private MaterialButton backToHome, retry;

    private LinearLayout ll_retry;

    private boolean status;

    private ImageView iv_inventory_status;


    private SubmitInventoryRequest submitInventoryRequest;

    SubmitDialog detailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_inventory);

        initView();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_submit_inventory;
    }

    private void initView() {
        tv_datetime = findViewById(R.id.tv_datetime);
        tv_total_quantity = findViewById(R.id.tv_total_quantity);
        tv_inventory_status = findViewById(R.id.tv_inventory_status);
        iv_inventory_status = findViewById(R.id.iv_inventory_status);
        backToHome = findViewById(R.id.backToHome);
        retry = findViewById(R.id.bt_retry);
        ll_retry = findViewById(R.id.ll_retry);
        tv_inventoryType = findViewById(R.id.tv_inventoryType);
        tv_location = findViewById(R.id.tv_location);

        database = new DatabaseHandler(SubmitInventoryActivity.this);
        storageHelper = new StorageHelper(SubmitInventoryActivity.this);
        detailDialog = new SubmitDialog(SubmitInventoryActivity.this);

        tv_datetime.setText(getSubmitInventoryTime());
        tv_total_quantity.setText(getIntent().getStringExtra("TOTAL_QUANTITY"));
        status = getIntent().getBooleanExtra("STATUS", false);
        String submitInventoryRequestJson = getIntent().getStringExtra("BODY");
        Gson gson = new Gson();
        submitInventoryRequest = gson.fromJson(submitInventoryRequestJson, SubmitInventoryRequest.class);

        if (getIntent().getStringExtra("LOCATION_NAME") != null) {
            if (!getIntent().getStringExtra("LOCATION_NAME").isEmpty()) {
                tv_location.setText(getIntent().getStringExtra("LOCATION_NAME"));
            } else {
                tv_location.setText("---");
            }
        } else {
            tv_location.setText("---");
        }

        if (getIntent().getStringExtra("LOCATION_GUID") != null) {
            if (!getIntent().getStringExtra("LOCATION_GUID").isEmpty()) {
                tv_inventoryType.setText("Location Inventory");
            } else {
                tv_inventoryType.setText("Full Inventory");
            }
        } else {
            tv_inventoryType.setText("Full Inventory");
        }


        refreshUI();
        clickListeners();
    }

    private void refreshUI() {
        if (status) {
            tv_inventory_status.setText(getString(R.string.inventory_nsubmitted));
            iv_inventory_status.setImageResource(R.drawable.circle_check);
            ll_retry.setVisibility(View.GONE);
            showSnackBar("Inventory Submitted");
        } else {
            tv_inventory_status.setText(getString(R.string.inventory_nsubmitted));
            iv_inventory_status.setImageResource(R.drawable.circle_exclamation);
            ll_retry.setVisibility(View.VISIBLE);
        }
    }

    private void clickListeners() {

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSubmitInventoryApi(submitInventoryRequest);
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubmitInventoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void callSubmitInventoryApi(SubmitInventoryRequest submitInventoryRequest) {
        if (AppUtils.isConnectedToInternet(SubmitInventoryActivity.this)) {
            detailDialog.show();
            Log.e(TAG, "" + new Gson().toJson(submitInventoryRequest));
            apiInterface = APIClient.getMocRetrofitClient().create(APIInterface.class);
            Call<SubmitInventoryResponse> call = apiInterface.submitInventory("Bearer " + storageHelper.getToken(), storageHelper.getCompanyGuid(), submitInventoryRequest);
            call.enqueue(new Callback<SubmitInventoryResponse>() {
                @Override
                public void onResponse(Call<SubmitInventoryResponse> call, Response<SubmitInventoryResponse> response) {
                    detailDialog.dismiss();
                    Log.e(TAG, "SubmitInventoryResponse : " + new Gson().toJson(response));
                    SubmitInventoryResponse submitInventoryResponse;
                    if (response.isSuccessful()) {
                        submitInventoryResponse = response.body();
                        if (submitInventoryResponse.getStatusCode() == 200) {
                            status = true;
                            refreshUI();
                        } else {
                            status = false;
                            refreshUI();
                        }
                    } else {
                        status = false;
                        refreshUI();
                    }
                }

                @Override
                public void onFailure(Call<SubmitInventoryResponse> call, Throwable t) {
                    detailDialog.dismiss();
                    Log.e(TAG, " " + t.getMessage());
                }
            });

        } else {
            AppUtils.showSnackBar(SubmitInventoryActivity.this, "" + getString(R.string.no_internet));
        }
    }
}