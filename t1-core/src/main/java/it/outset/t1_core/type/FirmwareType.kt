package it.outset.t1_core.type;

public enum FirmwareType {
    TX2("1.0.0"),
    TX4("1.7.4");

    private final String versionName;

    FirmwareType(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public static FirmwareType fromVersionName(String versionName) {
        for (FirmwareType type : values()) {
            if (type.versionName.equals(versionName)) {
                return type;
            }
        }
        return null;
    }
}
