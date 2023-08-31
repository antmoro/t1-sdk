package it.outset.t1_core.models;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryStatus {
    public float batteryVoltage;
    public float batteryLevel;
    public String batteryPlug;

    public BatteryStatus(float batteryVoltage, float batteryLevel, String batteryPlug) {
        this.batteryVoltage = batteryVoltage;
        this.batteryLevel = batteryLevel;
        this.batteryPlug = batteryPlug;
    }

    public static BatteryStatus getStatus(Context context) {
        // Battery status
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        // Are we charging / charged?
        int batteryStatusExtra = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = batteryStatusExtra == BatteryManager.BATTERY_STATUS_CHARGING ||
                batteryStatusExtra == BatteryManager.BATTERY_STATUS_FULL;
        // How are we charging?
        int batteryExtraPlugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = batteryExtraPlugged == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = batteryExtraPlugged == BatteryManager.BATTERY_PLUGGED_AC;
        String batteryPlug = usbCharge ? "USB" : acCharge ? "AC" : null;
        // Current battery level
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryLevel = level * 100 / (float) scale;
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        float batteryVoltage = voltage * 0.001f;

        return new BatteryStatus(batteryVoltage, batteryLevel, batteryPlug);
    }
}
