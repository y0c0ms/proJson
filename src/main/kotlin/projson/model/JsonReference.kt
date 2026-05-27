package projson.model

/**
 * A reference to another object by its UUID. Prints as `{"$ref": "<id>"}`, where [id] is
 * the `$id` that the referenced object received. [ProJson] emits this for a `@Reference`
 * field whose target has already been written.
 */
class JsonReference(val id: String) : JsonElement {

    override fun toJsonString(indent: Int): String = "{\"\$ref\": \"$id\"}"

    override fun toString(): String = toJsonString()
}
