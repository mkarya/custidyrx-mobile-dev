package com.payne.reader.bean.receive;

import com.payne.reader.bean.config.ProfileId;

/**
 * @author naz
 * Date 2020/7/22
 */
public class RfLinkProfile extends Success {

    /**
     * Rf communications link
     */
    private ProfileId linkProfile;

    public ProfileId getLinkProfile() {
        return linkProfile;
    }

    public void setLinkProfile(ProfileId linkProfile) {
        this.linkProfile = linkProfile;
    }

    @Override
    public String toString() {
        return "RfLinkProfile{" +
                "linkProfile=" + linkProfile +
                '}';
    }
}
