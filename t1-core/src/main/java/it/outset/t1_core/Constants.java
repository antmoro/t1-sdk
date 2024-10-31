package it.outset.t1_core;

import java.util.regex.Pattern;

public class Constants {

    public interface BLUETOOTH {
        String NAME_PREFIX_PATTERN = "^O\\.COSEN_([0-9A-Fa-f]{4})?_";
        String MAC_ADDRESS_EMPTY = "00:00:00:00:00:00";
        String[] VENDORS_MAC_ADDRESS = new String[]{
                // Ezurio Ltd
                "00:16:A4",
                // Bluegiga Technologies OY
                "00:07:80",
                "88:6B:0F",
                // Espressif Inc. https://maclookup.app/vendors/espressif-inc
                "18:FE:34",
                "AC:D0:74",
                "90:97:D5",
                "60:01:94",
                "5C:CF:7F",
                "54:5A:A6",
                "A0:20:A6",
                "24:0A:C4",
                "30:AE:A4",
                "2C:3A:E8",
                "A4:7B:9D",
                "24:B2:DE",
                "D8:A0:1D",
                "68:C6:3A",
                "EC:FA:BC",
                "DC:4F:22",
                "B4:E6:2D",
                "BC:DD:C2",
                "84:F3:EB",
                "84:0D:8E",
                "CC:50:E3",
                "3C:71:BF",
                "C4:4F:33",
                "80:7D:3A",
                "2C:F4:32",
                "A4:CF:12",
                "24:6F:28",
                "4C:11:AE",
                "24:62:AB",
                "D8:F1:5B",
                "50:02:91",
                "C8:2B:96",
                "98:F4:AB",
                "D8:BF:C0",
                "E0:98:06",
                "F4:CF:A2",
                "FC:F5:C4",
                "8C:AA:B5",
                "10:52:1C",
                "48:3F:DA",
                "7C:DF:A1",
                "F0:08:D1",
                "40:F5:20",
                "7C:9E:BD",
                "70:03:9F",
                "AC:67:B2",
                "84:CC:A8",
                "B8:F0:09",
                "0C:DC:7E",
                "24:A1:60",
                "08:3A:F2",
                "A8:03:2A",
                "A0:76:4E",
                "C4:DD:57",
                "8C:CE:4E",
                "94:B9:7E",
                "E0:E2:E6",
                "E8:DB:84",
                "3C:61:05",
                "E8:68:E7",
                "4C:75:25",
                "60:55:F9",
                "A4:E5:7C",
                "9C:9C:1F",
                "C4:5B:BE",
                "98:CD:AC",
                "BC:FF:4D",
                "94:3C:C6",
                "34:AB:95",
                "78:E3:6D",
                "34:86:5D",
                "30:83:98",
                "A8:48:FA",
                "7C:87:CE",
                "EC:94:CB",
                "84:F7:03",
                "34:B4:72",
                "AC:0B:FB",
                "C8:C9:A3",
                "44:17:93",
                "1C:9D:C2",
                "58:BF:25",
                "8C:4B:14",
                "40:91:51",
                "4C:EB:D6",
                "24:D7:EB",
                "90:38:0C",
                "E8:9F:6D",
                "48:55:19",
                "30:C6:F7",
                "68:67:25",
                "10:97:BD",
                "34:94:54",
                "78:21:84",
                "10:91:A8",
                "D4:F9:8D",
                "58:CF:79",
                "70:B8:F6",
                "E8:31:CD",
                "0C:B8:15",
                "94:E6:86",
                "24:4C:AB",
                "94:B5:55",
                "70:04:1D",
                "F4:12:FA",
                "C0:49:EF",
                "B8:D6:1A",
                "0C:8B:95",
                "C4:DE:E2",
                "68:B6:B3",
                "54:43:B2",
                "EC:62:60",
                "C8:F0:9E",
                "DC:54:75",
                "CC:DB:A7",
                "A0:B7:65",
                "B4:8A:0A",
                "C0:4E:30",
                "3C:E9:0E",
                "A8:42:E3",
                "34:85:18",
                "48:27:E2",
                "40:22:D8",
                "08:B6:1F",
                "80:64:6F",
                "E0:5A:1B",
                "48:E7:29",
                "08:3A:8D",
                "EC:DA:3B",
                "64:E8:33",
                "D4:D4:DA",
                "84:FC:E6",
                "34:98:7A",
                "A0:A3:B3",
                "40:4C:CA",
                "64:B7:08",
                "B0:A7:32",
                "B0:B2:1C",
                "48:31:B7",
                "54:32:04",
                "08:F9:E0",
                "24:DC:C3",
                "D8:BC:38",
                "FC:B4:67",
                "34:B7:DA",
                "08:D1:F9",
                "E4:65:B8",
                "D4:8A:FC",
                "E8:6B:EA",
                "C8:2E:18",
                "30:30:F9",
                "DC:DA:0C",
                "7C:73:98",
                "6C:B4:56",
                "10:06:1C",
                "74:4D:BD",
                "D8:13:2A",
                "30:C9:22",
                "3C:84:27",
                "80:65:99",
                "EC:64:C9",
                "F0:F5:BD",
                "CC:7B:5C",
                "FC:E8:C0",
                "24:58:7C",
                "CC:8D:A2",
                "14:2B:2F",
                "A0:DD:6C",
                "D0:EF:76",
                "C4:D8:D5",
                "48:CA:43",
                "18:8B:0E",
                "AC:15:18",
                "08:A6:F7",
                "2C:BC:BB",
                "EC:C9:FF",
                "9C:9E:6E",
                "E4:B0:63",
                "24:EC:4A",
                "1C:69:20",
                "88:13:BF",
                "34:5F:45",
                "F0:9E:9E",
                "E8:06:90",
                "78:EE:4C",
                "90:15:06",
                "D8:3B:DA",
                "7C:2C:67",
                "34:CD:B0",
                "C0:5D:89",
                "F0:24:F9",
                "94:54:C5",
                "98:3D:AE",
                "B0:81:84",
                "8C:BF:EA",
                "D4:8C:49",
                "A0:85:E3",
                "F8:B3:B7",
                "8C:4F:00",
                "E4:B3:23",
                "20:43:A8",
                "3C:8A:1F",
                "78:42:1C",
                "CC:BA:97",
                "DC:06:75",
                "DC:1E:D5",
                "F4:65:0B",
                "5C:01:3B",
                "28:37:2F",
                "30:ED:A0",
                "10:00:3B",
                "94:A9:90"
        };
    }

    public interface CONTROL_UNIT {
        int ADC_MAX = 4;
        byte ADC_MAX_BITMASK = 0b0000_1111;
        int AXES_MAX = 4;
        int ADC_AXES_MAX = 2;
        int COLLECTOR_DEVICE_ID = 0;
        int SENSOR_DEVICE_ID = 1;
        int COLLECTOR_ROLE_ID = 1;
        int SENSOR_ROLE_ID = 2;
        String COLLECTOR_ROLE = "collector";
        String SENSOR_ROLE = "sensor";
    }

    public interface CONTROL_UNIT_TPMS {
        int MAX_TPMS_SENSORS = 32;
        Pattern REGEX_TPMS_PATTERN = Pattern.compile("^C_TPMS:(\\d{1,3})(:R):WL:([A-Z]+)=(.+)");
        Pattern REGEX_TPMS_NMEA_PATTERN = Pattern.compile("\\$TP(MS|ST|AL),(.+)\\*([0-9A-F]{2})");

        int SIGNAL_TIMOUT_SECONDS = 360;

        int LEVEL_NONE = 0;
        int LEVEL_NORMAL = 1;
        int LEVEL_OK = 2;
        int LEVEL_ATTENTION = 3;
        int LEVEL_ERROR = 4;
        int LEVEL_AWAIT = 5;
        int LEVEL_PUNCTURED = 6;
        int LEVEL_FLAT = 7;
        int LEVEL_OVERPRESSURE = 8;
        int LEVEL_OVERTEMPERATURE = 9;
        int LEVEL_SIGNAL = 10;
        int LEVEL_BATTERY = 11;

        int PARAM_MAC = 0;
        int PARAM_COUNT = 4;
        int PARAM_SENSOR_ID = 5;
        int PARAM_POSITION = 6;
        int PARAM_PRESSURE = 7;
        int PARAM_TEMPERATURE = 8;
        int PARAM_VOLTAGE = 9;
        int PARAM_STATUS = 10;   }
}
