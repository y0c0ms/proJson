package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonPrimitiveTest {

    @Test
    fun `null serializes to JSON null`() {
        val p = JsonPrimitive(null)
        println("null primitive -> ${p.toJsonString()}")
        assertEquals("null", p.toJsonString())
    }

    @Test
    fun `string serializes with quotes`() {
        val p = JsonPrimitive("hello")
        println("string primitive -> ${p.toJsonString()}")
        assertEquals("\"hello\"", p.toJsonString())
    }

    @Test
    fun `string escapes double quotes`() {
        val p = JsonPrimitive("say \"hi\"")
        println("string with quotes -> ${p.toJsonString()}")
        assertEquals("\"say \\\"hi\\\"\"", p.toJsonString())
    }

    @Test
    fun `string escapes backslash`() {
        val p = JsonPrimitive("a\\b")
        println("string with backslash -> ${p.toJsonString()}")
        assertEquals("\"a\\\\b\"", p.toJsonString())
    }

    @Test
    fun `integer serializes without quotes`() {
        val p = JsonPrimitive(42)
        println("int primitive -> ${p.toJsonString()}")
        assertEquals("42", p.toJsonString())
    }

    @Test
    fun `double serializes without quotes`() {
        val p = JsonPrimitive(3.14)
        println("double primitive -> ${p.toJsonString()}")
        assertEquals("3.14", p.toJsonString())
    }

    @Test
    fun `boolean true serializes correctly`() {
        val p = JsonPrimitive(true)
        println("boolean true -> ${p.toJsonString()}")
        assertEquals("true", p.toJsonString())
    }

    @Test
    fun `boolean false serializes correctly`() {
        val p = JsonPrimitive(false)
        println("boolean false -> ${p.toJsonString()}")
        assertEquals("false", p.toJsonString())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val p = JsonPrimitive("hi")
        println("toString -> $p")
        assertEquals("\"hi\"", p.toString())
    }

    @Test
    fun `invalid value type throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            JsonPrimitive(listOf(1, 2))
        }
        println("invalid type threw -> ${ex.message}")
    }
}