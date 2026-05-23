package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnnotationsTest {

    // ── @JsonIgnore ───────────────────────────────────────────────────────────

    class PersonIgnore(
        val name: String,
        @JsonIgnore val password: String
    )

    @Test
    fun `@JsonIgnore field is absent from JSON output`() {
        val p = PersonIgnore("Alice", "secret")
        val json = ProJson().toJson(p) as JsonObject
        assertEquals(JsonPrimitive("Alice"), json.getProperty("name"))
        assertNull(json.getProperty("password"))
    }

    @Test
    fun `@JsonIgnore does not affect other fields`() {
        val p = PersonIgnore("Bob", "hunter2")
        val json = ProJson().toJson(p) as JsonObject
        assertEquals(JsonPrimitive("PersonIgnore"), json.getProperty("\$type"))
        assertEquals(JsonPrimitive("Bob"), json.getProperty("name"))
    }

    // ── @JsonProperty ─────────────────────────────────────────────────────────

    class TaskRenamed(
        @JsonProperty("desc") val description: String,
        @JsonIgnore val deadline: Any?,
        @JsonProperty("deps") val dependencies: List<String>
    )

    @Test
    fun `@JsonProperty renames the key in JSON output`() {
        val t = TaskRenamed("T1", null, emptyList())
        val json = ProJson().toJson(t) as JsonObject
        assertEquals(JsonPrimitive("T1"), json.getProperty("desc"))
        assertEquals("[]", (json.getProperty("deps") as JsonArray).toJsonString())
        assertNull(json.getProperty("description"))
        assertNull(json.getProperty("dependencies"))
    }

    @Test
    fun `@JsonProperty combined with @JsonIgnore`() {
        val t = TaskRenamed("T1", "ignore-me", emptyList())
        val json = ProJson().toJson(t) as JsonObject
        assertNull(json.getProperty("deadline"))
        assertNull(json.getProperty("description"))
        assertEquals(JsonPrimitive("T1"), json.getProperty("desc"))
    }

    // ── @JsonString ───────────────────────────────────────────────────────────

    class DateAsText : JsonStringSerializer {
        override fun serialize(obj: Any): String {
            val d = obj as DateValue
            return "%02d/%02d/%d".format(d.day, d.month, d.year)
        }
    }

    @JsonString(DateAsText::class)
    data class DateValue(val day: Int, val month: Int, val year: Int)

    @Test
    fun `@JsonString class is serialised as a plain string`() {
        val d = DateValue(30, 2, 2026)
        val json = ProJson().toJson(d)
        assertEquals(JsonPrimitive("30/02/2026"), json)
    }

    @Test
    fun `@JsonString works inside a list`() {
        val d1 = DateValue(30, 2, 2026)
        val d2 = DateValue(31, 4, 2026)
        val json = ProJson().toJson(listOf(d1, d2)) as JsonArray
        assertEquals(JsonPrimitive("30/02/2026"), json.get(0))
        assertEquals(JsonPrimitive("31/04/2026"), json.get(1))
        assertEquals("[\"30/02/2026\", \"31/04/2026\"]", json.toJsonString())
    }

    @Test
    fun `@JsonString works as a nested field value`() {
        class Event(val name: String, val date: DateValue)
        val e = Event("conference", DateValue(15, 6, 2026))
        val json = ProJson().toJson(e) as JsonObject
        assertEquals(JsonPrimitive("15/06/2026"), json.getProperty("date"))
    }
}
