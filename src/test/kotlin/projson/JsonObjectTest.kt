package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class JsonObjectTest {

    @Test
    fun `empty object serializes to empty braces`() {
        val obj = JsonObject()
        println("empty object -> ${obj.toJsonString()}")
        assertEquals("{}", obj.toJsonString())
    }

    @Test
    fun `setProperty with string value`() {
        val obj = JsonObject()
        obj.setProperty("name", "Alice")
        println("string property -> ${obj.toJsonString()}")
        assertEquals("{\"name\": \"Alice\"}", obj.toJsonString())
    }

    @Test
    fun `setProperty with int value`() {
        val obj = JsonObject()
        obj.setProperty("age", 30)
        println("int property -> ${obj.toJsonString()}")
        assertEquals("{\"age\": 30}", obj.toJsonString())
    }

    @Test
    fun `setProperty with null value`() {
        val obj = JsonObject()
        obj.setProperty("x", null)
        println("null property -> ${obj.toJsonString()}")
        assertEquals("{\"x\": null}", obj.toJsonString())
    }

    @Test
    // Guarantee that if setProperty is called 2 times the value is replaced
    fun `setProperty overwrites existing key`() {
        val obj = JsonObject()
        obj.setProperty("year", 2026)
        obj.setProperty("year", 2027)
        println("overwrite -> ${obj.toJsonString()}")
        assertEquals("{\"year\": 2027}", obj.toJsonString())
    }

    @Test
    fun `getProperty returns value`() {
        val obj = JsonObject()
        obj.setProperty("k", "v")
        println("get k -> ${obj.getProperty("k")}")
        assertEquals(JsonPrimitive("v"), obj.getProperty("k"))
    }

    @Test
    fun `getProperty returns null for missing key`() {
        val obj = JsonObject()
        println("get missing -> ${obj.getProperty("missing")}")
        assertNull(obj.getProperty("missing"))
    }

    @Test
    fun `deleteProperty removes key`() {
        val obj = JsonObject()
        obj.setProperty("a", 1)
        obj.deleteProperty("a")
        println("after delete -> ${obj.toJsonString()}")
        assertNull(obj.getProperty("a"))
        assertEquals("{}", obj.toJsonString())
    }

    @Test
    fun `setProperty accepts existing JsonElement`() {
        val obj = JsonObject()
        obj.setProperty("flag", JsonPrimitive(true))
        println("existing element -> ${obj.toJsonString()}")
        assertEquals("{\"flag\": true}", obj.toJsonString())
    }

    @Test
    fun `setProperty with complex type throws`() {
        val obj = JsonObject()
        val ex = assertFailsWith<IllegalArgumentException> {
            obj.setProperty("bad", listOf(1, 2, 3))
        }
        println("complex type threw -> ${ex.message}")
    }

    @Test
    fun `multiple properties preserve insertion order`() {
        val obj = JsonObject()
        obj.setProperty("a", 1)
        obj.setProperty("b", 2)
        obj.setProperty("c", 3)
        println("insertion order -> ${obj.toJsonString()}")
        assertEquals("{\"a\": 1, \"b\": 2, \"c\": 3}", obj.toJsonString())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val obj = JsonObject()
        obj.setProperty("x", 1)
        println("toString -> $obj")
        assertEquals("{\"x\": 1}", obj.toString())
    }

    @Test
    fun `nested object serializes correctly`() {
        val inner = JsonObject()
        inner.setProperty("day", 31)
        val outer = JsonObject()
        outer.setProperty("date", inner)
        println("nested -> ${outer.toJsonString()}")
        assertEquals("{\"date\": {\"day\": 31}}", outer.toJsonString())
    }

    @Test
    fun `visit traverses all key-value pairs in order`() {
        val obj = JsonObject()
        obj.setProperty("x", 1)
        obj.setProperty("y", 2)
        val visited = mutableListOf<String>()
        obj.visit { key, _ -> visited.add(key) }
        println("visited keys -> $visited")
        assertEquals(listOf("x", "y"), visited)
    }
}