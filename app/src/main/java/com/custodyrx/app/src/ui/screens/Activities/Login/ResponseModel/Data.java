package com.custodyrx.app.src.ui.screens.Activities.Login.ResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("userData")
    @Expose
    private UserData userData;

    @SerializedName("token")
    @Expose
    private String token;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
