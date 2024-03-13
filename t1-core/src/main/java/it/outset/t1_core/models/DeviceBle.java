package it.outset.t1_core.models;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

@Deprecated
public class DeviceBle {

    private int fwVersion;
    private int fwCrc;
    private int settingsCrc;
    private int whitelistCrc;

    public BluetoothDevice device;

    public int rssi;

    public DeviceBle() {
    }

    public DeviceBle(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public String getName() {
        return device.getName();
    }

    public boolean isNameEmpty() {
        return TextUtils.isEmpty(device.getName());
    }

    public String getAddress() {
        return device.getAddress();
    }

    public boolean equalsAddress(String address) {
        return device.getAddress().equalsIgnoreCase(address);
    }

    public void update(DeviceBle deviceBle) {
        this.device = deviceBle.device;
        this.rssi = deviceBle.rssi;
    }

    public int getFwVersion() {
        return fwVersion;
    }

    public void setFwVersion(int fwVersion) {
        this.fwVersion = fwVersion;
    }

    public int getFwCrc() {
        return fwCrc;
    }

    public void setFwCrc(int crc) {
        this.fwCrc = crc;
    }

    public int getSettingsCrc() {
        return settingsCrc;
    }

    public void setSettingsCrc(int crc) {
        this.settingsCrc = crc;
    }

    public int getWhitelistCrc() {
        return whitelistCrc;
    }

    public void setWhitelistCrc(int crc) {
        this.whitelistCrc = crc;
    }
}
