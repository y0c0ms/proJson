package projson

// JsonObject represents a JSON object: { "key": value, ... }
// LinkedHashMap preserves insertion order — a regular HashMap would not.
class JsonObject(
    private val properties: LinkedHashMap<String, JsonElement> = LinkedHashMap()
) : JsonElement {

    // setProperty adds or overwrites a key.
    // Accepts null, String, Number, Boolean, or an existing JsonElement.
    // For data classes or complex objects, convert with ProJson().toJson() first.
    fun setProperty(key: String, value: Any?) {
        properties[key] = wrap(value)
    }

    // getProperty returns null if the key does not exist.
    fun getProperty(key: String): JsonElement? = properties[key]

    // deleteProperty removes a key. No-op if the key does not exist.
    fun deleteProperty(key: String) { properties.remove(key) }

    // properties returns a read-only snapshot of all entries.
    fun properties(): Map<String, JsonElement> = properties.toMap()

    // visit calls action for every key-value pair in insertion order.
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
