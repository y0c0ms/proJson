package projson

import kotlin.reflect.KClass

/**
 * Marks a field to be excluded from the JSON output entirely.
 *
 * Example:
 * ```
 * class User(val name: String, @JsonIgnore val password: String)
 * ```
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonIgnore

/**
 * Overrides the JSON key used for a field.
 * Without this annotation the field's declared name is used as the key.
 *
 * @param name The key to emit in the JSON output.
 *
 * Example:
 * ```
 * class Task(@JsonProperty("desc") val description: String)
 * ```
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonProperty(val name: String)

/**
 * Serialises an entire class as a plain JSON string using a custom plugin.
 * The plugin must implement [JsonStringSerializer] and have a no-arg constructor.
 *
 * Applied at the class level, not on individual fields.
 *
 * @param serializer The class responsible for converting instances to a String.
 *
 * Example:
 * ```
 * @JsonString(DateAsText::class)
 * data class Date(val day: Int, val month: Int, val year: Int)
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonString(val serializer: KClass<out JsonStringSerializer>)

/**
 * Plugin interface for [JsonString].
 * Implement this and reference your class in the annotation.
 * Your implementation must have a no-arg constructor.
 */
interface JsonStringSerializer {
    fun serialize(obj: Any): String
}
