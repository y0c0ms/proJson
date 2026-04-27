package projson

// ProJson is the main entry point for converting Kotlin values to JSON.
// Go analogy: think of it as a package-level function, but wrapped in a class
class ProJson {

    // toJson converts any Kotlin value to the corresponding JsonElement.
    // Decision tree:
    //   null           → JsonPrimitive(null)
    //   String/Number/Boolean → JsonPrimitive
    //   Map            → JsonObject (no $type)
    //   Collection/Array → JsonArray
    //   anything else  → JsonObject with $type (via reflection, added in Task 7)
    fun toJson(obj: Any?): JsonElement {
        if (obj == null) return JsonPrimitive(null)
        if (obj is String || obj is Number || obj is Boolean) return JsonPrimitive(obj)
        if (obj is Map<*, *>) return mapToJsonObject(obj)
        if (obj is Collection<*>) return collectionToJsonArray(obj)
        if (obj is Array<*>) return collectionToJsonArray(obj.toList())
        return objectToJsonObject(obj)
    }

    private fun mapToJsonObject(map: Map<*, *>): JsonObject {
        val jsonObj = JsonObject()
        for ((k, v) in map) {
            jsonObj.setProperty(k.toString(), toJson(v))
        }
        return jsonObj
    }

    private fun collectionToJsonArray(collection: Collection<*>): JsonArray {
        val arr = JsonArray()
        for (item in collection) {
            arr.add(toJson(item))
        }
        return arr
    }

    private fun objectToJsonObject(obj: Any): JsonObject {
        throw UnsupportedOperationException(
            "Reflection-based toJson not yet implemented for ${obj.javaClass.simpleName}"
        )
    }
}
