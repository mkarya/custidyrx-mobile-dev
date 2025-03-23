package com.custodyrx.app.src.ui.screens.Activities.Login.ResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("isError")
    @Expose
    private boolean isError;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("statuscode")
    @Expose
    private int statusCode;

    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("err")
    @Expose
    private String err;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
