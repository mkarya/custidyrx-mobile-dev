package com.payne.reader.bean.receive;

import java.io.Serializable;

/**
 * Entity class with successful command execution
 *
 * @author naz
 * Date 2020/7/14
 */
public class Success implements Serializable {
    /**
     * command code
     */
    private byte cmd;

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "Success{" +
                "cmd=" + (cmd & 0xFF) +
                '}';
    }
}
