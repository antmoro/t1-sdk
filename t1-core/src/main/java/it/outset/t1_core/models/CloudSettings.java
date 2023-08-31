package it.outset.t1_core.models;

import com.google.gson.annotations.SerializedName;

public class CloudSettings {

    @SerializedName("plate")
    public String plate;

    @SerializedName("targetLoad")
    public int targetLoad;

    public String hostUrl;

    @SerializedName("hostOn")
    public boolean hostEnabled;

    @SerializedName("eqOn")
    public boolean equipmentEnabled;

    @SerializedName("diOn")
    public boolean digitalInputEnabled;

    @SerializedName("setupOn")
    public boolean setupEnabled;
}
