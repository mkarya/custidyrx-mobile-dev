package com.custodyrx.app.src.ui.screens.Activities.Comments.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Items;
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Products;
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.SubmitInventoryRequest;
import com.custodyrx.app.src.ui.screens.Activities.Comments.ResponseModel.SubmitInventoryResponse;
import com.custodyrx.app.src.ui.screens.Activities.Login.Pages.LoginActivity;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.Pages.PerformInventoryActivity;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Activities.SubmitInventory.Pages.SubmitInventoryActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends BaseActivity {

    private String TAG = "CommentsActivity";

    DatabaseHandler database;
    StorageHelper storageHelper;

    APIInterface apiInterface;

    private String totalQuantity;

    List<AllProduct> allProducts;

    private TextView tv_datetime, tv_total_quantity, tv_inventoryType, tv_location;

    MaterialButton finish;

    private TextInputEditText etComment;

    SubmitDialog detailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

       /* findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitDialog detailDialog = new SubmitDialog(CommentsActivity.this);
                detailDialog.show();

                // Close the dialog after 3 seconds and show a toast
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (detailDialog.isShowing()) {
                            detailDialog.dismiss();
                        }
                        showSnackBar("Inventory Submitted");

                        findViewById(R.id.ll_comment).setVisibility(View.GONE);
                        findViewById(R.id.finish).setVisibility(View.GONE);
                        findViewById(R.id.ll_finished).setVisibility(View.VISIBLE);
                    }
                }, 3000); // 3000 milliseconds = 3 seconds
            }
        });*/

        findViewById(R.id.backToHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        initView();
    }

    private void initView() {

        finish = findViewById(R.id.finish);
        tv_datetime = findViewById(R.id.tv_datetime);
        tv_datetime.setText(getSubmitInventoryTime());
        tv_total_quantity = findViewById(R.id.tv_total_quantity);
        tv_inventoryType = findViewById(R.id.tv_inventoryType);
        tv_location = findViewById(R.id.tv_location);
        etComment = findViewById(R.id.etComment);

        database = new DatabaseHandler(CommentsActivity.this);
        storageHelper = new StorageHelper(CommentsActivity.this);
        detailDialog = new SubmitDialog(CommentsActivity.this);

        String allProductJson = getIntent().getStringExtra("ALL_PRODUCT");
        Type listType = new TypeToken<List<AllProduct>>() {
        }.getType();
        allProducts = new Gson().fromJson(allProductJson, listType);
        totalQuantity = getIntent().getStringExtra("TOTAL_QUANTITY");
        tv_total_quantity.setText(totalQuantity);

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

        Log.e(TAG, "Product : " + allProductJson);
        Log.e(TAG, "Total :" + totalQuantity);

        clickListners();


    }

    private void requestDataSet() {
        List<Products> productList = new ArrayList<>();  // List to store all products

        for (AllProduct product : allProducts) {
            String GUID = product.getGuid();

            List<Items> items = database.getSelectedProductItems(GUID); // Get items for the product

            Products products = new Products(GUID, product.getName(), items);
            productList.add(products);  // Add to the list
        }

        // Create SubmitInventoryRequest with proper values
        SubmitInventoryRequest submitInventoryRequest = new SubmitInventoryRequest(storageHelper.getGuid(), tv_inventoryType.getText().toString(), getIntent().getStringExtra("LOCATION_GUID"), totalQuantity, etComment.getText().toString(), getCurrentUTCTimeString(), productList);

        Log.e(TAG, new Gson().toJson(submitInventoryRequest));

        callSubmitInventoryApi(submitInventoryRequest);


    }


    private void clickListners() {
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDataSet();

            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_comments;
    }

    private void callSubmitInventoryApi(SubmitInventoryRequest submitInventoryRequest) {
        if (AppUtils.isConnectedToInternet(CommentsActivity.this)) {
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
                            Intent i = new Intent(CommentsActivity.this, SubmitInventoryActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.putExtra("TOTAL_QUANTITY", totalQuantity);
                            i.putExtra("STATUS", true);
                            i.putExtra("LOCATION_GUID", getIntent().getStringExtra("LOCATION_GUID"));
                            i.putExtra("LOCATION_NAME", getIntent().getStringExtra("LOCATION_NAME"));
                            startActivity(i);
                        } else {
                            Gson gson = new Gson();
                            String submitInventoryRequestJson = gson.toJson(submitInventoryRequest);
                            Intent i = new Intent(CommentsActivity.this, SubmitInventoryActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.putExtra("TOTAL_QUANTITY", totalQuantity);
                            i.putExtra("STATUS", false);
                            i.putExtra("BODY", submitInventoryRequestJson);
                            i.putExtra("LOCATION_GUID", getIntent().getStringExtra("LOCATION_GUID"));
                            i.putExtra("LOCATION_NAME", getIntent().getStringExtra("LOCATION_NAME"));
                            startActivity(i);
                        }
                    } else {
                        Gson gson = new Gson();
                        String submitInventoryRequestJson = gson.toJson(submitInventoryRequest);
                        Intent i = new Intent(CommentsActivity.this, SubmitInventoryActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("TOTAL_QUANTITY", totalQuantity);
                        i.putExtra("STATUS", false);
                        i.putExtra("BODY", submitInventoryRequestJson);
                        i.putExtra("LOCATION_GUID", getIntent().getStringExtra("LOCATION_GUID"));
                        i.putExtra("LOCATION_NAME", getIntent().getStringExtra("LOCATION_NAME"));
                        startActivity(i);
                        Log.e(TAG, "UnSuccessful :" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<SubmitInventoryResponse> call, Throwable t) {
                    detailDialog.dismiss();
                    Log.e(TAG, " " + t.getMessage());
                }
            });


        } else {
            AppUtils.showSnackBar(CommentsActivity.this, "" + getString(R.string.no_internet));
        }
    }
}