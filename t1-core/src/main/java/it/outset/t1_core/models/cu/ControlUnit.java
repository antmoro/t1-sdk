package it.outset.t1_core.models.cu;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.outset.t1_core.Constants.*;
import it.outset.t1_core.Firmware;
import it.outset.t1_core.Hardware;
import it.outset.t1_core.models.hr.HoldingRegister;
import it.outset.t1_core.type.FirmwareType;
import it.outset.t1_core.type.HardwareType;

/**
 *
 */
public class ControlUnit {
    private long id;
    private int deviceId;
    private String name; // Nome Bluetooth assegnato al dispositivo
    private String eui; // Numero seriale univoco dell'hardware
    private String role;
    private int roleId;
    private int rssi;
    private float weightCorrection;
    private Firmware firmware;
    private Hardware hardware;
    private BluetoothDevice bluetoothDevice;
    private HoldingRegister holdingRegister;

    private List<ControlUnitAxis> axesList;
    private List<ControlUnitSensor> sensorsList;
    private List<ControlUnitTpms> tpmsList;

    private Pattern prefixPattern = Pattern.compile(BLUETOOTH.NAME_PREFIX_PATTERN);

    public ControlUnit() {
        firmware = new Firmware(FirmwareType.TX4.getVersionName());
        hardware = new Hardware(HardwareType.K008_01.getVersionName());
        tpmsList = new ArrayList<>();
        axesList = new ArrayList<>();
        sensorsList = new ArrayList<>();
        holdingRegister = new HoldingRegister();
    }

    public ControlUnit(BluetoothDevice bluetoothDevice, int rssi) {
        this();
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
    }

    public long getId() {
        return id;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEui(String eui) {
        this.eui = eui;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRoleId() {
        return holdingRegister.getRole();
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
        holdingRegister.setRole(roleId);
    }

    public float getWeightCorrection() {
        return weightCorrection;
    }

    public void setWeightCorrection(float weightCorrection) {
        this.weightCorrection = weightCorrection;
    }

/*
    public String getName() {
        if (bluetoothDevice == null)
            return "Dummy Bluetooth device";

        name = bluetoothDevice.getName();

        if (name != null) {
            Matcher matcher = prefixPattern.matcher(name);
            if (matcher.find()) {
                this.name = name.substring(matcher.end());
            } else {
                this.name = name;
            }
        }

        return name;
    }

    public boolean isNameEmpty() {
        return bluetoothDevice == null || TextUtils.isEmpty(bluetoothDevice.getName());
    }
*/

    public String getAddress() {
        if (bluetoothDevice == null)
            return BLUETOOTH.MAC_ADDRESS_EMPTY;

        return bluetoothDevice.getAddress();
    }

    public boolean equalsAddress(String address) {
        return bluetoothDevice.getAddress().equalsIgnoreCase(address);
    }

    public void update(ControlUnit controlUnit) {
        this.bluetoothDevice = controlUnit.bluetoothDevice;
        this.rssi = controlUnit.rssi;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public String getEui() {
        return getAddress().replace(":", "");
    }

    public List<ControlUnitTpms> getTpms() {
        return tpmsList;
    }

    public List<ControlUnitAxis> getAxes() {
        return axesList;
    }

    public ControlUnitAxis getAxisByPosition(int position) {
        if (position < CONTROL_UNIT.AXES_MAX) {
            return axesList.get(position);
        }
        return null;
    }

    public List<ControlUnitSensor> getSensors() {
        return sensorsList;
    }

    public Firmware getFirmware() {
        return firmware;
    }

    public void setFirmware(Firmware firmware) {
        this.firmware = firmware;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public HoldingRegister getHoldingRegister() {
        return holdingRegister;
    }

    public void setHoldingRegister(HoldingRegister holdingRegister) {
        this.holdingRegister = holdingRegister;
    }


    /**
     * Configurazione Assi
     */

    public void addAxis(ControlUnitAxis axis) {
        if (axesList.size() < CONTROL_UNIT.AXES_MAX) {
            axis.setId(axesList.size());
            axesList.add(axis);
        }
    }

    /**
     * Elimina un asse dalla configurazione e aggiorna gli holding register
     * @param axis Asse da eliminare.
     * @return
     */
    public boolean removeAxis(ControlUnitAxis axis) {
        boolean success = axesList.remove(axis);
        if (success) {
            for (int i = 0; i < CONTROL_UNIT.AXES_MAX; i++) {
                if (i < axesList.size()) {
                    ControlUnitAxis item = axesList.get(i);
                    axis.setId(i);
                    holdingRegister.setAdcInstalledPerAxis(i, axis.getInstalled());
                } else {
                    holdingRegister.setAdcInstalledPerAxis(i, 0);
                }
            }

        }
        return success;
    }

    public void setAxisEmptyWeight(int position, int weight) {
        if (position < axesList.size()) {
            axesList.get(position).setWeightEmpty(weight);
            holdingRegister.setAxisWeightEmpty(position, weight);
        }
    }

    public void setAxisFullWeight(int position, int weight) {
        if (position < axesList.size()) {
            axesList.get(position).setWeightEmpty(weight);
            holdingRegister.setAxisWeightFull(position, weight);
        }
    }

    /**
     * Configurazione sensori ADC
     */

    public void setAxisAdcMin(List<Integer> adcs, int[] adcInput) {
        if (adcs.size() > 0) {
            for (int i = 0; i < adcs.size(); i++) {
                int adcPosition = adcs.get(i);
                holdingRegister.setAdcMin(adcPosition, adcInput[adcPosition]);
            }
        }
    }

    public void setAxisAdcMax(List<Integer> adcs, int[] adcInput) {
        if (adcs.size() > 0) {
            for (int i = 0; i < adcs.size(); i++) {
                int adcPosition = adcs.get(i);
                holdingRegister.setAdcMax(adcPosition, adcInput[adcPosition]);
            }
        }
    }

    public void setAdcMin(int position, int adc) {
        holdingRegister.setAdcMin(position, adc);
    }

    public void setAdcMax(int position, int adc) {
        holdingRegister.setAdcMax(position, adc);
    }

    public List<Integer> getAdcListByAxis(int position) {
        return holdingRegister.getAdcByInstalledList(holdingRegister.getAdcInstalledByAxis(position));
    }

    public int getAllAdcInstalled() {
        int installed = 0;
        for (int i = 0; i < CONTROL_UNIT.AXES_MAX; i++) {
            installed |= holdingRegister.getAdcInstalledByAxis(i);
        }
        return installed;
    }

    public static void addAdc(ControlUnitAxis axis, int adcPosition, HoldingRegister hr) {
        if (adcPosition < CONTROL_UNIT.ADC_MAX) {
            int installed = axis.getInstalled();
            int bitmask = (byte) (1 << adcPosition);
            axis.setInstalled(installed | bitmask);
            hr.setAdcInstalledPerAxis(axis.getId(), axis.getInstalled());
        }
    }

    public static void removeAdc(ControlUnitAxis axis, int adcPosition, HoldingRegister hr) {
        if (adcPosition < CONTROL_UNIT.ADC_MAX) {
            int installed = axis.getInstalled();
            int bitmask = (byte) (1 << adcPosition);
            int remove = 0xF ^ bitmask;
            axis.setInstalled(installed & remove);
            hr.setAdcInstalledPerAxis(axis.getId(), axis.getInstalled());
        }
    }

    public static boolean hasAdc(ControlUnitAxis axis, int adcPosition) {
        if (adcPosition < CONTROL_UNIT.ADC_MAX) {
            int installed = axis.getInstalled();
            int bitmask = (byte) (1 << adcPosition);
            return (installed & bitmask) > 0;
        }
        return false;
    }

    public static boolean hasAdc(int installed, int adcPosition) {
        int bitmask = (byte) (1 << adcPosition);
        return (installed & bitmask) > 0;
    }

    public static int getAdcCountPerAxis(ControlUnitAxis axis) {
        int installed = axis.getInstalled();
        int count = 0;
        for (int i = 0; i < CONTROL_UNIT.ADC_MAX; i++) {
            int bitmask = (byte) (1 << i);
            count += (installed & bitmask) > 0 ? 1 : 0;
        }
        return count;
    }

    public ControlUnitSensor getSensor(int position) {
        ControlUnitSensor cuSensor = new ControlUnitSensor();
        cuSensor.setPosition(position);
        cuSensor.setEmptyAdc(holdingRegister.getAdcMin(position));
        cuSensor.setFullAdc(holdingRegister.getAdcMax(position));
        cuSensor.setType(holdingRegister.getSensorType(position));
        return cuSensor;
    }
}
