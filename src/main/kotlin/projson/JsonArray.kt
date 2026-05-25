package projson

/**
 * A JSON array: `[elem1, elem2, ...]`. Elements keep the order in which they were added.
 */
class JsonArray(
    private val elements: MutableList<JsonElement> = mutableListOf()
) : JsonElement {

    /**
     * Appends [value]. It may be `null`, a [String], a [Number], a [Boolean], or an
     * existing [JsonElement]; other types throw [IllegalArgumentException].
     */
    fun add(value: Any?) { elements.add(wrap(value)) }

    /** Removes the element at [index] (zero-based). */
    fun remove(index: Int) { elements.removeAt(index) }

    /** Returns the element at [index] (zero-based). */
    fun get(index: Int): JsonElement = elements[index]

    /** Returns the number of elements. */
    fun size(): Int = elements.size

    /** Returns a read-only copy of the elements. */
    fun elements(): List<JsonElement> = elements.toList()

    /** Calls [action] for each element with its index, in order. */
    fun visit(action: (Int, JsonElement) -> Unit) {
        elements.forEachIndexed { i, elem -> action(i, elem) }
    }

    override fun toJsonString(indent: Int): String {
        if (elements.isEmpty()) return "[]"
        val items = elements.joinToString(", ") { it.toJsonString(indent) }
        return "[$items]"
    }

    override fun toString(): String = toJsonString()
}
