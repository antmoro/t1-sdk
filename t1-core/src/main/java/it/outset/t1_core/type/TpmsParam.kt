package it.outset.t1_core.type

enum class TpmsParam(val value: Int) {
    MAC(0),
    COUNT(4),
    SENSOR_ID(5),
    POSITION(6),
    PRESSURE(7),
    TEMPERATURE(8),
    VOLTAGE(9),
    STATUS(10);
}
