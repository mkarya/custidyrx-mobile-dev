package com.payne.reader.bean.receive;

import com.payne.reader.bean.config.ProfileId;

public class E710LinkProfile extends Success {

    private ProfileId linkProfile;
    private byte drMode;

    public ProfileId getLinkProfile() {
        return linkProfile;
    }

    public void setLinkProfile(ProfileId linkProfile) {
        this.linkProfile = linkProfile;
    }

    public byte getDrMode() {
        return drMode;
    }

    public void setDrMode(byte drMode) {
        this.drMode = drMode;
    }

    @Override
    public String toString() {
        return "RfLinkProfile{" +
                "linkProfile=" + linkProfile +
                "drMode=" + (drMode & 0xFF) +
                '}';
    }
}
