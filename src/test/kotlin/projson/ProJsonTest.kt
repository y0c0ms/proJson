package projson

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ProJsonTest {

    private val pj = ProJson()

    // --- Null and primitives ---

    @Test
    fun `null becomes JsonPrimitive null`() {
        val result = pj.toJson(null)
        assertIs<JsonPrimitive>(result)
        assertEquals("null", result.toJsonString())
    }

    @Test
    fun `string becomes JsonPrimitive`() {
        val result = pj.toJson("hello")
        assertEquals("\"hello\"", result.toJsonString())
    }

    @Test
    fun `int becomes JsonPrimitive`() {
        val result = pj.toJson(42)
        assertEquals("42", result.toJsonString())
    }

    @Test
    fun `boolean becomes JsonPrimitive`() {
        val result = pj.toJson(false)
        assertEquals("false", result.toJsonString())
    }

    // --- Map (becomes JsonObject without dollar-type) ---

    @Test
    fun `map becomes JsonObject without type property`() {
        val result = pj.toJson(mapOf("a" to 1, "b" to "x")) as JsonObject
        // maps have no $type
        assertEquals(null, result.getProperty("\$type"))
        assertEquals(JsonPrimitive(1), result.getProperty("a"))
        assertEquals(JsonPrimitive("x"), result.getProperty("b"))
    }

    // --- Collection (becomes JsonArray) ---

    @Test
    fun `list becomes JsonArray`() {
        val result = pj.toJson(listOf("a", null, "b")) as JsonArray
        assertEquals("[\"a\", null, \"b\"]", result.toJsonString())
    }

    @Test
    fun `empty list becomes empty JsonArray`() {
        val result = pj.toJson(emptyList<Any>()) as JsonArray
        assertEquals("[]", result.toJsonString())
    }

    @Test
    fun `set becomes JsonArray`() {
        val result = pj.toJson(setOf(1)) as JsonArray
        assertEquals("[1]", result.toJsonString())
    }

    @Test
    fun `array becomes JsonArray`() {
        val result = pj.toJson(arrayOf("x", "y")) as JsonArray
        assertEquals("[\"x\", \"y\"]", result.toJsonString())
    }

    @Test
    fun `nested list becomes nested JsonArray`() {
        val result = pj.toJson(listOf(listOf(1, 2), listOf(3))) as JsonArray
        assertEquals("[[1, 2], [3]]", result.toJsonString())
    }

    // --- Reflection-based (data classes / regular classes) ---

    // Test fixtures defined inside the test file.
    data class Date(val day: Int, val month: Int, val year: Int)
    class Task(val description: String, val deadline: Date?, val dependencies: List<Task>)

    @Test
    fun `data class becomes JsonObject with type`() {
        val d = Date(31, 4, 2026)
        val result = pj.toJson(d) as JsonObject
        assertEquals(JsonPrimitive("Date"), result.getProperty("\$type"))
        assertEquals(JsonPrimitive(31), result.getProperty("day"))
        assertEquals(JsonPrimitive(4), result.getProperty("month"))
        assertEquals(JsonPrimitive(2026), result.getProperty("year"))
    }

    @Test
    fun `data class serializes to valid JSON`() {
        val d = Date(31, 4, 2026)
        val json = pj.toJson(d)
        val text = json.toJsonString()
        assertEquals(true, text.contains("\"\$type\": \"Date\""))
        assertEquals(true, text.contains("\"day\": 31"))
    }

    @Test
    fun `null property becomes JsonPrimitive null`() {
        val t = Task("T1", null, emptyList())
        val result = pj.toJson(t) as JsonObject
        assertEquals(JsonPrimitive(null), result.getProperty("deadline"))
    }

    @Test
    fun `list property becomes JsonArray`() {
        val t = Task("T1", null, emptyList())
        val result = pj.toJson(t) as JsonObject
        val deps = result.getProperty("dependencies")
        assertIs<JsonArray>(deps)
        assertEquals("[]", deps.toJsonString())
    }

    @Test
    fun `nested object property becomes nested JsonObject`() {
        val t = Task("T1", Date(30, 2, 2026), emptyList())
        val result = pj.toJson(t) as JsonObject
        val deadline = result.getProperty("deadline")
        assertIs<JsonObject>(deadline)
        assertEquals(JsonPrimitive("Date"), deadline.getProperty("\$type"))
    }

    @Test
    fun `setProperty can override reflection-generated value (spec example)`() {
        val d = Date(31, 4, 2026)
        val json = pj.toJson(d) as JsonObject
        json.setProperty("year", 2027)
        assertEquals(JsonPrimitive(2027), json.getProperty("year"))
    }

    @Test
    fun `list of objects becomes JsonArray of JsonObjects`() {
        val t1 = Task("T1", Date(30, 2, 2026), emptyList())
        val t2 = Task("T2", Date(31, 4, 2026), emptyList())
        val t3 = Task("T3", null, listOf(t1, t2))
        val all = listOf(t1, t2, t3)
        val result = pj.toJson(all) as JsonArray
        assertEquals(3, result.size())
        assertIs<JsonObject>(result.get(0))
        assertIs<JsonObject>(result.get(2))
    }
}
