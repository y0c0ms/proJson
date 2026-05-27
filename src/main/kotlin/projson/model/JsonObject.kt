package projson.model

/**
 * A JSON object: `{"key": value, ...}`. Keys are kept in insertion order by the backing
 * [LinkedHashMap], so the printed order matches the order of [setProperty] calls.
 */
class JsonObject(
    private val properties: LinkedHashMap<String, JsonElement> = LinkedHashMap()
) : JsonElement {

    /**
     * Adds [key], or overwrites it if present. [value] may be `null`, a [String], a
     * [Number], a [Boolean], or an existing [JsonElement]; other types throw
     * [IllegalArgumentException]. For a data class or other object, convert it with
     * [ProJson.toJson] first.
     */
    fun setProperty(key: String, value: Any?) {
        properties[key] = wrap(value)
    }

    /** Returns the value for [key], or `null` if the key is absent. */
    fun getProperty(key: String): JsonElement? = properties[key]

    /** Removes [key]. Does nothing if the key is absent. */
    fun deleteProperty(key: String) { properties.remove(key) }

    /** Returns a read-only copy of all entries. */
    fun properties(): Map<String, JsonElement> = properties.toMap()

    /** Calls [action] for every key/value pair, in insertion order. */
    fun visit(action: (String, JsonElement) -> Unit) {
        for ((key, value) in properties) action(key, value)
    }

    override fun toJsonString(indent: Int): String {
        if (properties.isEmpty()) return "{}"
        val entries = properties.entries.joinToString(", ") { (k, v) ->
            "\"$k\": ${v.toJsonString(indent)}"
        }
        return "{$entries}"
    }

    override fun toString(): String = toJsonString()
}
