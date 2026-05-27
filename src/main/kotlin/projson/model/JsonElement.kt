package projson.model

/**
 * Common interface for every JSON node: [JsonPrimitive], [JsonObject], [JsonArray], and
 * [JsonReference].
 */
interface JsonElement {
    /**
     * Returns the JSON text for this node. [indent] is accepted for API symmetry; the
     * output is compact and does not change with it.
     */
    fun toJsonString(indent: Int = 0): String

    /** Returns the same text as [toJsonString]. */
    override fun toString(): String
}

/**
 * A leaf JSON value: `null`, a [String], a [Number], or a [Boolean]. The constructor
 * throws [IllegalArgumentException] for any other type. Strings are escaped when printed.
 */
data class JsonPrimitive(val value: Any?) : JsonElement {

    init {
        // require() throws IllegalArgumentException if the condition is false.
        require(value == null || value is String || value is Number || value is Boolean) {
            "JsonPrimitive only accepts null, String, Number, or Boolean. Got: ${value?.javaClass?.simpleName}"
        }
    }

    override fun toJsonString(indent: Int): String = when (value) {
        null      -> "null"
        is String  -> "\"${escapeString(value)}\""
        is Boolean -> value.toString()  // Boolean checked before Number; in Kotlin, Boolean is NOT a subtype of Number
        is Number  -> value.toString()
        else       -> throw IllegalStateException("unreachable")
    }

    override fun toString(): String = toJsonString()

    private fun escapeString(s: String): String = s
        .replace("\\", "\\\\")  // backslash first, otherwise we'd double-escape later replacements
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

/**
 * Wraps a raw Kotlin value as a [JsonElement]. Accepts an existing [JsonElement], `null`,
 * [String], [Number], or [Boolean], and throws [IllegalArgumentException] for anything
 * else. Used by [JsonObject.setProperty] and [JsonArray.add]. For arbitrary objects use
 * [ProJson.toJson].
 */
internal fun wrap(value: Any?): JsonElement = when (value) {
    is JsonElement -> value
    null           -> JsonPrimitive(null)
    is String      -> JsonPrimitive(value)
    is Number      -> JsonPrimitive(value)
    is Boolean     -> JsonPrimitive(value)
    else -> throw IllegalArgumentException(
        "Cannot auto-wrap ${value::class.simpleName}. Use ProJson().toJson() for complex types."
    )
}
