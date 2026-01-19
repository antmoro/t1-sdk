package it.outset.t1_core.type;

public enum HardwareType {
    T1("SFPN001", 4, (byte) 0b0000_1111, 4, 2),
    TX4("T008.02", 4, (byte) 0b0000_1111, 4, 2),
    T011_01("T011.01", 4, (byte) 0b0000_1111, 4, 2),
    T011_10("T011.10", 4, (byte) 0b0000_1111, 4, 2),
    K008_01("K008.01", 4, (byte) 0b0000_1111, 4, 2),
    K008_10("K008.10", 8, (byte) 0b0000_1111, 4, 2),
    K009_00("K009.00", 8, (byte) 0b1111_1111, 4, 2),
    K009_10("K009.10", 8, (byte) 0b1111_1111, 4, 2);

    private final String versionName;
    private final int adcMax;
    private final byte adcMaxBitmask;
    private final int axesMax;
    private final int adcAxesMax;

    HardwareType(String versionName, int adcMax, byte adcMaxBitmask, int axesMax, int adcAxesMax) {
        this.versionName = versionName;
        this.adcMax = adcMax;
        this.adcMaxBitmask = adcMaxBitmask;
        this.axesMax = axesMax;
        this.adcAxesMax = adcAxesMax;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getAdcMax() {
        return adcMax;
    }

    public byte getAdcMaxBitmask() {
        return adcMaxBitmask;
    }

    public int getAxesMax() {
        return axesMax;
    }

    public int getAdcAxesMax() {
        return adcAxesMax;
    }

    public static HardwareType fromVersionName(String versionName) {
        for (HardwareType type : values()) {
            if (type.versionName.equals(versionName)) {
                return type;
            }
        }
        return null;
    }
}
