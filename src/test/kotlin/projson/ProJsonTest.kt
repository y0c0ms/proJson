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
}
