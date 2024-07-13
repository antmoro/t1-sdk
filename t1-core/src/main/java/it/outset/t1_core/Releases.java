package it.outset.t1_core;

import android.text.TextUtils;

/**
 * Created by antonio on 13/10/16.
 */

public class Releases {
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version.replace('|', '.');
    }

    public int compare(String version) {
        return VersionUtility.compareVersions(this.version, version);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(version);
    }
}
