package projson

// JsonArray represents a JSON array: [elem1, elem2, ...]
class JsonArray(
    private val elements: MutableList<JsonElement> = mutableListOf()
) : JsonElement {

    // add appends an element. Accepts null, String, Number, Boolean, or JsonElement.
    fun add(value: Any?) { elements.add(wrap(value)) }

    // remove deletes the element at the given index (0-based).
    fun remove(index: Int) { elements.removeAt(index) }

    // get returns the element at the given index.
    fun get(index: Int): JsonElement = elements[index]

    // size returns the number of elements.
    fun size(): Int = elements.size

    // elements returns a read-only snapshot.
    fun elements(): List<JsonElement> = elements.toList()

    // visit calls action for every element with its index.
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
