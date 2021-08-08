package io.github.advancerman.todd.json

@Target(AnnotationTarget.FUNCTION)
annotation class ManualJsonConstructor(val constructorName: String = "")

object JsonDefaults {
    fun setDefault(key: String, value: Any?, map: MutableMap<String, Pair<Any?, Boolean>>): Any? {
        map.getOrPut(key) { value to true }.let {
            val (previousValue, valuePresent) = it
            return if (!valuePresent) {
                map[key] = value to true
                value
            } else {
                previousValue
            }
        }
    }
}
