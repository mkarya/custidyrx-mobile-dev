package com.payne.reader.bean.send;

import com.payne.reader.base.Consumer;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.OperationTag;

import java.security.InvalidParameterException;
import java.util.Objects;

public class MultiAntReadTagConfig {

    private boolean ReadConfigFlag;
    private byte[] singleReadConfig;
    private byte[] customReadConfig;
    private Consumer<OperationTag> onSuccess;
    private Consumer<Failure> onError;

    private MultiAntReadTagConfig(Builder builder){
        this.singleReadConfig = builder.singleReadConfig;
        this.customReadConfig = builder.customReadConfig;
        this.onSuccess = builder.onSuccess;
        this.onError = builder.onError;
    }

    public byte[] getSingleReadConfig() {
        return singleReadConfig;
    }

    public byte[] getCustomReadConfig() {
        return customReadConfig;
    }

    public Consumer<OperationTag> getOnSuccess() {
        return onSuccess;
    }

    public Consumer<Failure> getOnError() {
        return onError;
    }

    public boolean isReadConfigFlag() {
        return ReadConfigFlag;
    }

    public static class Builder{
        private boolean ReadConfigFlag;
        private byte[] singleReadConfig;
        private byte[] customReadConfig;
        private Consumer<OperationTag> onSuccess;
        private Consumer<Failure> onError;

        public Builder(){
            this.ReadConfigFlag = false;
            this.singleReadConfig = null;
            this.customReadConfig = null;
            this.onSuccess = null;
            this.onError = null;
        }


        public Builder setMultiAntRead(ReadConfig config){
            Objects.requireNonNull(config);
            try{
                this.ReadConfigFlag = false;
                this.singleReadConfig = config.getReadInfo();
            } catch (Exception e){
                e.printStackTrace();
            }
            return this;
        }

        public Builder setMultiAntRead(CustomSessionReadConfig config){
            Objects.requireNonNull(config);
            try{
                this.ReadConfigFlag = true;
                this.customReadConfig = config.getReadInfo();
            } catch (Exception e){
                e.printStackTrace();
            }
            return this;
        }

        public Builder setOnSuccess(Consumer<OperationTag> onSuccess){
            this.onSuccess = onSuccess;
            return this;
        }

        public Builder setOnFailure(Consumer<Failure> onFailure){
            this.onError = onFailure;
            return this;
        }

        public MultiAntReadTagConfig build(){
            if (onError == null) {
                throw new InvalidParameterException("Unimplemented ReadTag failure callback");
            }
            if(ReadConfigFlag){
                setMultiAntRead(new ReadConfig.Builder().build());
            }else{
                setMultiAntRead(new CustomSessionReadConfig.Builder().build());
            }
            return new MultiAntReadTagConfig(this);
        }

    }

}
