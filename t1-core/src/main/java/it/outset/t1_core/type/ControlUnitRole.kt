package it.outset.t1_core.type

enum class ControlUnitRole(val id: Int, val role: String) {
    CONTROLLER(1, "controller"),
    NODE(2, "node"),
    GATEWAY(3, "gateway"),
    UNKNOWN(255, "unknown");

    fun equalsRole(role: String): Boolean {
        return this.role == role
    }

    fun equalsRole(id: Int): Boolean {
        return this.id == id
    }

    companion object {
        @JvmStatic
        fun fromId(id: Int): ControlUnitRole {
            return entries.firstOrNull { it.id == id } ?: UNKNOWN
        }

        @JvmStatic
        fun fromRole(role: String): ControlUnitRole {
            return entries.firstOrNull { it.role == role } ?: UNKNOWN
        }
    }
}