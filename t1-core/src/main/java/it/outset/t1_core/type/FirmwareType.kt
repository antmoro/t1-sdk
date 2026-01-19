package it.outset.t1_core.type

enum class FirmwareType(@JvmField val versionName: String) {
    TX2("1.0.0"),
    TX4("1.7.4");

    companion object {
        fun fromVersionName(versionName: String?): FirmwareType? {
            for (type in entries) {
                if (type.versionName == versionName) {
                    return type
                }
            }
            return null
        }
    }
}
