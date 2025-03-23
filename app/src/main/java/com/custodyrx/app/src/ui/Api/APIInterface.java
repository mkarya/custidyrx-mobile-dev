package com.custodyrx.app.src.ui.Api;

import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.SubmitInventoryRequest;
import com.custodyrx.app.src.ui.screens.Activities.Comments.ResponseModel.SubmitInventoryResponse;
import com.custodyrx.app.src.ui.screens.Activities.Login.LogoutResponseModel.LogoutResponse;
import com.custodyrx.app.src.ui.screens.Activities.Login.ResponseModel.LoginResponse;
import com.custodyrx.app.src.ui.screens.Activities.SelectLocation.models.GetAllLocationResponseModel.GetAllLocationResponse;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.GetAllProductItemResponse;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel.GetAllProductResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @FormUrlEncoded
    @POST(APIConstant.LOGIN)
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @GET(APIConstant.LOGOUT)
    Call<LogoutResponse> logout(@Header("Authorization") String authToken);


    @GET(APIConstant.GET_ALL_PRODUCTS)
    Call<GetAllProductResponse> getProductsByCompanyGuid(
            @Header("Authorization") String authToken,
            @Query("companyGuid") String companyGuid
    );

    @GET(APIConstant.GET_ALL_PRODUCT_ITEMS)
    Call<GetAllProductItemResponse> getItemsByCompanyAndProductGuid(
            @Header("Authorization") String authToken,
            @Query("companyGuid") String companyGuid,
            @Query("productGuid") String productGuid
    );

    @GET(APIConstant.GET_LOCATION_ALL)
    Call<GetAllLocationResponse> getAllLocation(
            @Header("Authorization") String authToken,
            @Query("companyGuid") String companyGuid,
            @Query("sortOrder") String sortOrder,
            @Query("page") int page,
            @Query("rows") int rows,
            @Query("field") String field
    );

    @POST(APIConstant.SUBMIT_INVENTORY)
    Call<SubmitInventoryResponse> submitInventory(
            @Header("Authorization") String authToken,
            @Query("companyGuid") String companyGuid,
            @Body SubmitInventoryRequest request
    );

}
