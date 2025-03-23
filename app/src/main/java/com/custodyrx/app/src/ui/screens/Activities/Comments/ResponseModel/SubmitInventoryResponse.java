package com.custodyrx.app.src.ui.screens.Activities.Comments.ResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmitInventoryResponse {
    @SerializedName("isError")
    @Expose
    private boolean isError;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("statusCpde")
    @Expose
    private int statusCode;

    @SerializedName("data")
    @Expose
    private Object data;

    @SerializedName("err")
    @Expose
    private String err;

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
