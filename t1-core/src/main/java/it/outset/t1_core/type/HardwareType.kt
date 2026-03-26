package it.outset.t1_core.type

enum class HardwareType(
    @JvmField val versionName: String,
    val adcMax: Int,
    val adcMaxBitmask: Byte,
    val axesMax: Int,
    val adcAxesMax: Int,
    val isNodeOnly: Boolean
) {
    T1("SFPN001", 4, 15.toByte(), 4, 2, false),
    TX4("T008.02", 4, 15.toByte(), 4, 2, false),
    T011_01("T011.01", 4, 15.toByte(), 4, 2, false),
    T011_10("T011.10", 4, 15.toByte(), 4, 2, false),
    T011_10L("T011.1xL", 4, 15.toByte(), 4, 2, false),
    K008_01("K008.0x", 4, 15.toByte(), 4, 2, false),
    K008_10("K008.1x", 4, 15.toByte(), 4, 2, false),
    K009_00("K009.00", 8, 255.toByte(), 4, 2, false),
    K009_10("K009.10", 8, 255.toByte(), 4, 2, false),
    K006_10("K006.10", 8, 255.toByte(), 4, 2, true),  // Prototype only
    K006_20("K006.20", 8, 255.toByte(), 4, 2, true);

    companion object {
        @JvmStatic
        fun fromVersionName(versionName: String?): HardwareType? {
            if (versionName == null) return null
            for (type in entries) {
                if (matchesPattern(type.versionName, versionName)) {
                    return type
                }
            }
            return null
        }

        private fun matchesPattern(pattern: String, input: String): Boolean {
            if (pattern.length != input.length) return false
            for (i in pattern.indices) {
                if (pattern[i] != 'x' && pattern[i] != input[i]) return false
            }
            return true
        }
    }
}
