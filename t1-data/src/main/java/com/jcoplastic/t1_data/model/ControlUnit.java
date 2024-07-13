package com.jcoplastic.t1_data.model;

import android.bluetooth.BluetoothDevice;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.regex.Pattern;

@Entity
public class ControlUnit {
    @PrimaryKey
    private long id;
    private int deviceId;
    private String address;
    private String eui; // Numero seriale univoco dell'hardware
    private String name; // Nome Bluetooth assegnato al dispositivo
    private String role;
    private int roleId;
    private int rssi;
    public long tpmsUpdatedTimestamp;
    public int tpmsSensorsLost;
}
