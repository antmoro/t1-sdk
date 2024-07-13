package it.outset.t1_core.type

enum class WeighingMethod(val key: String) {
    GROSS("pref_weighing_method_net"),
    NET("pref_weighing_method_gross");

    fun matches(method: String): Boolean {
        return this.key == method
    }

    companion object {
        @JvmStatic
        fun fromKey(key: String): WeighingMethod {
            return entries.firstOrNull { it.key == key } ?: GROSS
        }
    }
}