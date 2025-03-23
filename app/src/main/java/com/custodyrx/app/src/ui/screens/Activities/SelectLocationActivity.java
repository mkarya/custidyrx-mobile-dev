package com.custodyrx.app.src.ui.screens.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custodyrx.app.BaseActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.Api.APIClient;
import com.custodyrx.app.src.ui.Api.APIInterface;
import com.custodyrx.app.src.ui.Constants.AppUtils;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.screens.Activities.Login.Pages.LoginActivity;
import com.custodyrx.app.src.ui.screens.Activities.Login.ResponseModel.LoginResponse;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.Pages.PerformInventoryActivity;
import com.custodyrx.app.src.ui.screens.Activities.SelectLocation.models.GetAllLocationResponseModel.GetAllLocationResponse;
import com.custodyrx.app.src.ui.screens.Activities.SelectLocation.models.GetAllLocationResponseModel.LocationData;
import com.custodyrx.app.src.ui.screens.Activities.SelectLocation.pages.LocationAdapter;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLocationActivity extends BaseActivity {

    private String TAG = "SelectLocationActivity";
    APIInterface apiInterface;

    StorageHelper storageHelper;

    RecyclerView recyclerView;
    LocationAdapter locationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initView();
    }

    private void initView() {

        storageHelper = new StorageHelper(SelectLocationActivity.this);

        recyclerView = findViewById(R.id.rvlocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        callAllLocationApi();
    }

    private void callAllLocationApi() {
        if (AppUtils.isConnectedToInternet(SelectLocationActivity.this)) {
            showProgressDialog(SelectLocationActivity.this);
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<GetAllLocationResponse> call = apiInterface.getAllLocation("Bearer " + storageHelper.getToken(), storageHelper.getCompanyGuid(), "ASC", 1, 100, "name");
            call.enqueue(new Callback<GetAllLocationResponse>() {
                @Override
                public void onResponse(Call<GetAllLocationResponse> call, Response<GetAllLocationResponse> response) {
                    hideProgressDialog();
                    Log.e(TAG, "GetAllLocationResponse : " + new Gson().toJson(response));
                    GetAllLocationResponse getAllLocationResponse;
                    if (response.isSuccessful()) {
                        getAllLocationResponse = response.body();
                        if (getAllLocationResponse.getStatusCode() == 200) {
                            List<LocationData> locations = response.body().getData().getLocationData();
                            locationAdapter = new LocationAdapter(SelectLocationActivity.this, locations);
                            recyclerView.setAdapter(locationAdapter);
                            locationAdapter.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(LocationData location) {
                                    Intent intent = new Intent(SelectLocationActivity.this, PerformInventoryActivity.class);
                                    intent.putExtra("LOCATION_GUID", location.getGuid());
                                    intent.putExtra("LOCATION_NAME",location.getName());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            AppUtils.showSnackBar(SelectLocationActivity.this, "" + getAllLocationResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + getAllLocationResponse.getMsg());
                        }
                    } else {
                        AppUtils.showSnackBar(SelectLocationActivity.this, "" + response.message());
                        Log.e(TAG, "UnSuccessful :" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<GetAllLocationResponse> call, Throwable t) {
                    hideProgressDialog();
                    Log.e(TAG, " " + t.getMessage());
                }
            });

        } else {
            AppUtils.showSnackBar(SelectLocationActivity.this, "" + getString(R.string.no_internet));
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_select_location;
    }
}