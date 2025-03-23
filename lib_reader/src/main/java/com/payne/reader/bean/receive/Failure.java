package com.payne.reader.bean.receive;

import com.payne.reader.bean.config.ResultCode;

import java.io.Serializable;

/**
 * Entity class that failed to execute the command
 *
 * @author naz
 * Date 2020/7/14
 */
public class Failure implements Serializable {
    /**
     * command code
     */
    private byte cmd;
    /**
     * Error code
     *
     * @see ResultCode
     */
    private byte errorCode;

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public void setErrorCode(byte errorCode) {
        this.errorCode = errorCode;
    }

    public byte getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "Failure{" +
                "cmd=" + (cmd & 0xFF) +
                ", errorCode=" + (errorCode & 0xFF) +
                '}';
    }
}
