package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2021/1/15
 */
public class CmdStatus {
    private final byte cmd;
    private final byte status;
    private final int antId;

    public CmdStatus(byte cmd, byte status, int antId) {
        this.cmd = cmd;
        this.status = status;
        this.antId = antId;
    }

    public byte getCmd() {
        return cmd;
    }

    public byte getStatus() {
        return status;
    }

    public int getAntId() {
        return antId;
    }
}
