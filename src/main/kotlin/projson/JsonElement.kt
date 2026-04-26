package projson

// JsonElement is the common interface for all JSON node types.
interface JsonElement {
    fun toJsonString(indent: Int = 0): String
    override fun toString(): String
}

// JsonPrimitive wraps a leaf JSON value: null, String, Number, or Boolean.
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
        is Boolean -> value.toString()  // Boolean checked before Number — in Kotlin, Boolean is NOT a subtype of Number
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

// wrap() converts a raw Kotlin value into a JsonElement.
// Used internally by JsonObject and JsonArray so callers don't have to type JsonPrimitive(x) manually.
// For complex objects (data classes etc.), use ProJson().toJson() instead.
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
