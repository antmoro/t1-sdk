package it.outset.t1_core;

import android.text.TextUtils;

/**
 * Created by antonio on 13/10/16.
 */

public class Releases {
    private String _version;

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version.replace('|', '.');
    }

    public int compare(String version) {
        return VersionUtility.compareVersions(this._version, version);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(_version);
    }
}
