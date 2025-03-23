package com.custodyrx.app.src.ui.screens.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custodyrx.app.MainActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.database.DatabaseHandler;
import com.custodyrx.app.src.ui.Api.APIClient;
import com.custodyrx.app.src.ui.Api.APIInterface;
import com.custodyrx.app.src.ui.Constants.AppUtils;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.custodyrx.app.src.ui.dialogs.SyncProgressDialog;
import com.custodyrx.app.src.ui.screens.Activities.InventoryTypeActivity;
import com.custodyrx.app.src.ui.screens.Activities.Login.Pages.LoginActivity;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.Pages.PerformInventoryActivity;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Activities.TagList.Pages.TagListActivity;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.BizLocation;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.GetAllProductItemResponse;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.ProductItemData;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel.GetAllProductResponse;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel.ProductCategory;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel.ProductData;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardFragment extends Fragment {


    private String TAG = "DashboardFragment";
    LinearLayout ll_inventory, ll_checklist, ll_transfer, ll_sync, ll_tools;

    StorageHelper storageHelper;
    DatabaseHandler database;
    APIInterface apiInterface;

    LoaderDialog loaderDialog;

    SyncProgressDialog syncProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initView(view);


        return view;
    }

    private void initView(View view) {
        ll_inventory = view.findViewById(R.id.ll_inventory);
        ll_checklist = view.findViewById(R.id.ll_checklist);
        ll_transfer = view.findViewById(R.id.ll_transfer);
        ll_sync = view.findViewById(R.id.ll_sync);
        ll_tools = view.findViewById(R.id.ll_tools);

        /*ll_checklist.setAlpha(0.5f);
        ll_checklist.setEnabled(false);
        ll_transfer.setAlpha(0.5f);
        ll_transfer.setEnabled(false);*/

        storageHelper = new StorageHelper(getActivity());
        database = new DatabaseHandler(getActivity());


        if (storageHelper.isSyncOnLogin()) {
            callGetAllProductApi();
        }

        clickListeners();


    }

    private void callGetAllProductApi() {
        if (AppUtils.isConnectedToInternet(getActivity())) {
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<GetAllProductResponse> call = apiInterface.getProductsByCompanyGuid("Bearer " + storageHelper.getToken(), storageHelper.getCompanyGuid());
            call.enqueue(new Callback<GetAllProductResponse>() {
                @Override
                public void onResponse(Call<GetAllProductResponse> call, Response<GetAllProductResponse> response) {
                    Log.e(TAG, "GetAllProductResponse : " + new Gson().toJson(response));
                    GetAllProductResponse getAllProductResponse;
                    if (response.isSuccessful()) {
                        getAllProductResponse = response.body();
                        if (getAllProductResponse.getStatusCode() == 200) {
                            database.clearProductsTable();
                            database.clearProductCategoriesTable();
                            database.clearProductItemsTable();
                            List<ProductData> productList = getAllProductResponse.getData().getProductData();
                            for (ProductData product : productList) {
                                String categoryJson = new Gson().toJson(product.getProductCategory());
                                database.insertProduct(product);
                                callGetProductItemApi(product.getGuid());
                                List<ProductCategory> productCategory = product.getProductCategory();
                                for (ProductCategory productItem : productCategory) {
                                    Log.e(TAG, productItem.getName());
                                    database.insertProductCategory(productItem, product.getGuid());
                                }
                            }


                        } else {
                            AppUtils.showSnackBar(getActivity(), "" + getAllProductResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + getAllProductResponse.getMsg());
                        }
                    } else {
                        AppUtils.showSnackBar(getActivity(), "" + response.message());
                        Log.e(TAG, "UnSuccessful :" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<GetAllProductResponse> call, Throwable t) {
                    syncProgressDialog.dismiss();
                    Log.e(TAG, " " + t.getMessage());
                }
            });

        } else {
            syncProgressDialog.dismiss();
            AppUtils.showSnackBar(getActivity(), "" + getString(R.string.no_internet));
        }
    }


    private void callGetProductItemApi(String productGuid) {
        if (AppUtils.isConnectedToInternet(getActivity())) {
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<GetAllProductItemResponse> call = apiInterface.getItemsByCompanyAndProductGuid("Bearer " + storageHelper.getToken(), storageHelper.getCompanyGuid(), productGuid);
            call.enqueue(new Callback<GetAllProductItemResponse>() {
                @Override
                public void onResponse(Call<GetAllProductItemResponse> call, Response<GetAllProductItemResponse> response) {
                    Log.e(TAG, "GetAllProductItemResponse : " + new Gson().toJson(response));
                    GetAllProductItemResponse getAllProductItemResponse;
                    if (response.isSuccessful()) {
                        getAllProductItemResponse = response.body();
                        if (getAllProductItemResponse.getStatusCode() == 200) {

                            List<ProductItemData> productItemList = getAllProductItemResponse.getData().getItemList();
                            for (ProductItemData productItemData : productItemList) {
                                database.insertProductItems(productItemData);
                            }


                            AppUtils.showSnackBar(getActivity(), "Data sync successfully!");
                            syncProgressDialog.dismiss();

                        } else {
                            AppUtils.showSnackBar(getActivity(), "" + getAllProductItemResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + getAllProductItemResponse.getMsg());
                        }
                    } else {
                        AppUtils.showSnackBar(getActivity(), "" + response.message());
                        Log.e(TAG, "UnSuccessful :" + response.message());
                    }


                }

                @Override
                public void onFailure(Call<GetAllProductItemResponse> call, Throwable t) {
                    syncProgressDialog.dismiss();
                    Log.e(TAG, " " + t.getMessage());
                }
            });
        } else {
            syncProgressDialog.dismiss();
            AppUtils.showSnackBar(getActivity(), "" + getString(R.string.no_internet));
        }
    }

    private void clickListeners() {
        ll_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loaderDialog = new LoaderDialog(requireActivity());
                loaderDialog.show();
                Intent intent = new Intent(getActivity(), InventoryTypeActivity.class);
                startActivity(intent);
                loaderDialog.dismiss();

            }
        });

        ll_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncProgressDialog = new SyncProgressDialog(requireActivity());
                syncProgressDialog.show();

                callGetAllProductApi();
            }
        });


        ll_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*List<AllProduct> products = database.getAllProducts();
                Log.e(TAG, "Products : " + new Gson().toJson(products));*/




            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.title != null) {
            MainActivity.title.setText(getString(R.string.itemsight));
        }
    }

    protected void showSnackBar(String message) {
        ViewGroup view = (ViewGroup) getView();  // Corrected from findViewById
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));

            ViewGroup viewGroup = (ViewGroup) snackbar.getView();
            viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black));

            TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

            SpannableString boldText = new SpannableString("CLOSE");
            boldText.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), 0);

            snackbar.setAction(boldText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        } else {
            Log.e("Snackbar Error", "Root view is null.");
        }
    }

}