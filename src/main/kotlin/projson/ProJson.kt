package projson

import java.util.IdentityHashMap
import java.util.UUID

// One instance = one serialisation session. The registry tracks every
// regular-class object seen so far, keyed by reference (not equals).
class ProJson {

    private val registry = IdentityHashMap<Any, String>()

    fun toJson(obj: Any?): JsonElement {
        if (obj == null) return JsonPrimitive(null)
        if (obj is String || obj is Number || obj is Boolean) return JsonPrimitive(obj)
        if (obj is Map<*, *>) return mapToJsonObject(obj)
        if (obj is Collection<*>) return collectionToJsonArray(obj)
        if (obj is Array<*>) return collectionToJsonArray(obj.toList())
        val jsonStringAnn = obj.javaClass.getAnnotation(JsonString::class.java)
        if (jsonStringAnn != null) {
            val serializer = jsonStringAnn.serializer.java.getDeclaredConstructor().newInstance()
            return JsonPrimitive(serializer.serialize(obj))
        }
        return objectToJsonObject(obj)
    }

    private fun mapToJsonObject(map: Map<*, *>): JsonObject {
        val jsonObj = JsonObject()
        for ((k, v) in map) jsonObj.setProperty(k.toString(), toJson(v))
        return jsonObj
    }

    private fun collectionToJsonArray(collection: Collection<*>): JsonArray {
        val arr = JsonArray()
        for (item in collection) arr.add(toJson(item))
        return arr
    }

    // Data classes always have a compiler-generated copy() method; regular classes never do.
    private fun isDataClass(clazz: Class<*>): Boolean =
        clazz.declaredMethods.any { it.name == "copy" }

    private fun objectToJsonObject(obj: Any): JsonElement {
        val clazz = obj.javaClass
        val jsonObj = JsonObject()

        if (!isDataClass(clazz)) {
            // First encounter: register a UUID and emit $id.
            // $ref is only emitted by toRefOrObject, which is called for @Reference fields.
            if (!registry.containsKey(obj)) {
                val uuid = UUID.randomUUID().toString()
                registry[obj] = uuid
            }
            jsonObj.setProperty("\$id", JsonPrimitive(registry[obj]!!))
        }

        jsonObj.setProperty("\$type", JsonPrimitive(clazz.simpleName))

        val fields = clazz.declaredFields
            .filter { !java.lang.reflect.Modifier.isStatic(it.modifiers) && !it.isSynthetic }

        for (field in fields) {
            if (field.isAnnotationPresent(JsonIgnore::class.java)) continue
            field.isAccessible = true
            val value = field.get(obj)
            val key = field.getAnnotation(JsonProperty::class.java)?.name ?: field.name

            if (field.isAnnotationPresent(Reference::class.java)) {
                jsonObj.setProperty(key, toRefOrObject(value))
            } else {
                jsonObj.setProperty(key, toJson(value))
            }
        }

        return jsonObj
    }

    // Used for @Reference fields: emits $ref if already registered,
    // otherwise serialises fully (which registers the object).
    private fun toRefOrObject(value: Any?): JsonElement {
        if (value == null) return JsonPrimitive(null)
        if (value is Collection<*>) {
            val arr = JsonArray()
            for (item in value) arr.add(toRefOrObject(item))
            return arr
        }
        registry[value]?.let { return JsonReference(it) }
        return objectToJsonObject(value)
    }
}
