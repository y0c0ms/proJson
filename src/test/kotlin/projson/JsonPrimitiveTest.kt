package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonPrimitiveTest {

    @Test
    fun `null serializes to JSON null`() {
        val p =    JsonPrimitive(null)
        assertEquals("null", p.toJsonString())
    }

    @Test
    fun `string serializes with quotes`() {
        val p = JsonPrimitive("hello")
        assertEquals("\"hello\"", p.toJsonString())
    }

    @Test
    fun `string escapes double quotes`() {
        val p = JsonPrimitive("say \"hi\"")
        assertEquals("\"say \\\"hi\\\"\"", p.toJsonString())
    }

    @Test
    fun `string escapes backslash`() {
        val p = JsonPrimitive("a\\b")
        assertEquals("\"a\\\\b\"", p.toJsonString())
    }

    @Test
    fun `integer serializes without quotes`() {
        val p = JsonPrimitive(42)
        assertEquals("42", p.toJsonString())
    }

    @Test
    fun `double serializes without quotes`() {
        val p = JsonPrimitive(3.14)
        assertEquals("3.14", p.toJsonString())
    }

    @Test
    fun `boolean true serializes correctly`() {
        val p = JsonPrimitive(true)
        assertEquals("true", p.toJsonString())
    }

    @Test
    fun `boolean false serializes correctly`() {
        val p = JsonPrimitive(false)
        assertEquals("false", p.toJsonString())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val p = JsonPrimitive("hi")
        assertEquals("\"hi\"", p.toString())
    }

    @Test
    fun `invalid value type throws`() {
        assertFailsWith<IllegalArgumentException> {
            JsonPrimitive(listOf(1, 2))
        }
    }
}
