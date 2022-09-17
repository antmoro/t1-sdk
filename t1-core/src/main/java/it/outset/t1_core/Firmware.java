package it.outset.t1_core;

/**
 * Created by antonio on 13/10/16.
 */

public class Firmware extends Releases {
    public static final String VERSION_TX2 = "1.0.0";
    public static final String VERSION_TX4 = "1.7.4";
    public static final int VERSION_EQUAL = 0;
    public static final int VERSION_OLDER = 1;
    public static final int VERSION_NEWER = -1;
    public static final int VERSION_INVALID = -2;


    // Holding Registers
    public static final int ADJUSTMENT_INDEX_START = 1;
    public static final int CALIBRATION_INDEX_START = 23;
    public final static int TRUCK_AXIS_COUNT = 3;
    public final static int TRAILER_AXIS_COUNT = 2;

    public Firmware() {
//        this.parse(VERSION_TX4);
    }
}
