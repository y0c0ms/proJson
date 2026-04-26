package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class JsonObjectTest {

    @Test
    fun `empty object serializes to empty braces`() {
        val obj = JsonObject()
        assertEquals("{}", obj.toJsonString())
    }

    @Test
    fun `setProperty with string value`() {
        val obj = JsonObject()
        obj.setProperty("name", "Alice")
        assertEquals("{\"name\": \"Alice\"}", obj.toJsonString())
    }

    @Test
    fun `setProperty with int value`() {
        val obj = JsonObject()
        obj.setProperty("age", 30)
        assertEquals("{\"age\": 30}", obj.toJsonString())
    }

    @Test
    fun `setProperty with null value`() {
        val obj = JsonObject()
        obj.setProperty("x", null)
        assertEquals("{\"x\": null}", obj.toJsonString())
    }

    @Test

    // Guarantee that if setProperty is called 2 times the value is replaced
    fun `setProperty overwrites existing key`() {
        val obj = JsonObject()
        obj.setProperty("year", 2026)
        obj.setProperty("year", 2027)
        assertEquals("{\"year\": 2027}", obj.toJsonString())
    }

    @Test
    fun `getProperty returns value`() {
        val obj = JsonObject()
        obj.setProperty("k", "v")
        assertEquals(JsonPrimitive("v"), obj.getProperty("k"))
    }

    @Test
    fun `getProperty returns null for missing key`() {
        val obj = JsonObject()
        assertNull(obj.getProperty("missing"))
    }

    @Test
    fun `deleteProperty removes key`() {
        val obj = JsonObject()
        obj.setProperty("a", 1)
        obj.deleteProperty("a")
        assertNull(obj.getProperty("a"))
        assertEquals("{}", obj.toJsonString())
    }

    @Test
    fun `setProperty accepts existing JsonElement`() {
        val obj = JsonObject()
        obj.setProperty("flag", JsonPrimitive(true))
        assertEquals("{\"flag\": true}", obj.toJsonString())
    }

    @Test
    fun `setProperty with complex type throws`() {
        val obj = JsonObject()
        assertFailsWith<IllegalArgumentException> {
            obj.setProperty("bad", listOf(1, 2, 3))
        }
    }

    @Test
    fun `multiple properties preserve insertion order`() {
        val obj = JsonObject()
        obj.setProperty("a", 1)
        obj.setProperty("b", 2)
        obj.setProperty("c", 3)
        assertEquals("{\"a\": 1, \"b\": 2, \"c\": 3}", obj.toJsonString())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val obj = JsonObject()
        obj.setProperty("x", 1)
        assertEquals("{\"x\": 1}", obj.toString())
    }

    @Test
    fun `nested object serializes correctly`() {
        val inner = JsonObject()
        inner.setProperty("day", 31)
        val outer = JsonObject()
        outer.setProperty("date", inner)
        assertEquals("{\"date\": {\"day\": 31}}", outer.toJsonString())
    }

    @Test
    fun `visit traverses all key-value pairs in order`() {
        val obj = JsonObject()
        obj.setProperty("x", 1)
        obj.setProperty("y", 2)
        val visited = mutableListOf<String>()
        obj.visit { key, _ -> visited.add(key) }
        assertEquals(listOf("x", "y"), visited)
    }
}
