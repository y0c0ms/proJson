package projson

// JsonReference represents a JSON reference to another object via its UUID.
// Serializes as: {"$ref": "<uuid>"}
class JsonReference(val id: String) : JsonElement {

    override fun toJsonString(indent: Int): String = "{\"\$ref\": \"$id\"}"

    override fun toString(): String = toJsonString()
}
