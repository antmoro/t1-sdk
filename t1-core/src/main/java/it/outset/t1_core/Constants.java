package it.outset.t1_core;

import java.util.regex.Pattern;

public class Constants {

    public interface BLUETOOTH {
        String MAC_ADDRESS_EMPTY = "00:00:00:00:00:00";
        String LAIRD_BASE_EUI = "00:16:A4"; // Ezurio Ltd
        String BLUEGIGA_BASE_EUI = "00:07:80"; // Bluegiga Technologies OY
        String BLUEGIGA_BASE_2_EUI = "88:6B:0F"; // Bluegiga Technologies OY
        String NAME_PREFIX_PATTERN = "^O\\.COSEN_([0-9A-Fa-f]{4})?_";
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
