package projson

// ProJson is the main entry point for converting Kotlin values to JSON.
class ProJson {

    // toJson converts any Kotlin value to the corresponding JsonElement.
    // Decision tree:
    //   null           > JsonPrimitive(null)
    //   String/Number/Boolean > JsonPrimitive
    //   Map            > JsonObject (no $type)
    //   Collection/Array > JsonArray
    //   anything else  > JsonObject with $type (via reflection)
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
        val clazz = obj.javaClass
        val jsonObj = JsonObject()

        jsonObj.setProperty("\$type", JsonPrimitive(clazz.simpleName))

        val fields = clazz.declaredFields
            .filter { !java.lang.reflect.Modifier.isStatic(it.modifiers) && !it.isSynthetic }

        for (field in fields) {
            field.isAccessible = true
            val value = field.get(obj)
            jsonObj.setProperty(field.name, toJson(value))
        }

        return jsonObj
    }
}
