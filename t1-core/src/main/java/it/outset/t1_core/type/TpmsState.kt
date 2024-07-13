package it.outset.t1_core.type

enum class TpmsState(val level: Int, val state: String) {
    NONE(0, "none"),
    NORMAL(1, "normal"),
    OK(2, "ok"),
    ATTENTION(3, "attention"),
    ERROR(4, "error"),
    AWAITING(5, "awaiting"),
    PUNCTURED(6, "punctured"),
    FLAT(7, "flat"),
    OVERPRESSURE(8, "overpressure"),
    OVERTEMPERATURE(9, "overtemperature"),
    SIGNAL_LOST(10, "signal_lost"),
    BATTERY_LOW(11, "battery_low");

    fun equalsLevel(level: Int): Boolean {
        return this.level == level
    }

    fun equalsName(state: String): Boolean {
        return this.state == state
    }

    companion object {
        //TODO: move to local configuration local and cloud
        const val signalTimeoutSeconds: Int = 360

        @JvmStatic
        fun fromLevel(level: Int): TpmsState {
            return entries.firstOrNull { it.level == level } ?: NONE
        }

        @JvmStatic
        fun fromKey(state: String): TpmsState {
            return entries.firstOrNull { it.state == state } ?: NONE
        }
    }
}
