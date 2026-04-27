package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class you can also JsonArrayTest {

    @Test
    fun `empty array serializes to empty brackets`() {
        val arr = JsonArray()
        assertEquals("[]", arr.toJsonString())
    }

    @Test
    fun `add string element`() {
        val arr = JsonArray()
        arr.add("a")
        assertEquals("[\"a\"]", arr.toJsonString())
    }

    @Test
    fun `add null element`() {
        val arr = JsonArray()
        arr.add(null)
        assertEquals("[null]", arr.toJsonString())
    }

    @Test
    fun `add multiple elements`() {
        val arr = JsonArray()
        arr.add("a")
        arr.add(null)
        arr.add("b")
        assertEquals("[\"a\", null, \"b\"]", arr.toJsonString())
    }

    @Test
    fun `spec example - add to existing list`() {
        val arr = JsonArray()
        arr.add("a")
        arr.add(null)
        arr.add("b")
        arr.add("c")
        assertEquals("[\"a\", null, \"b\", \"c\"]", arr.toJsonString())
    }

    @Test
    fun `remove by index`() {
        val arr = JsonArray()
        arr.add("a")
        arr.add("b")
        arr.add("c")
        arr.remove(1)
        assertEquals("[\"a\", \"c\"]", arr.toJsonString())
    }

    @Test
    fun `get by index`() {
        val arr = JsonArray()
        arr.add("hello")
        assertEquals(JsonPrimitive("hello"), arr.get(0))
    }

    @Test
    fun `size returns element count`() {
        val arr = JsonArray()
        arr.add(1)
        arr.add(2)
        assertEquals(2, arr.size())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val arr = JsonArray()
        arr.add(42)
        assertEquals("[42]", arr.toString())
    }

    @Test
    fun `add complex type throws`() {
        val arr = JsonArray()
        assertFailsWith<IllegalArgumentException> {
            arr.add(listOf(1, 2))
        }
    }

    @Test
    fun `nested array serializes correctly`() {
        val inner = JsonArray()
        inner.add(1)
        inner.add(2)
        val outer = JsonArray()
        outer.add(inner)
        assertEquals("[[1, 2]]", outer.toJsonString())
    }

    @Test
    fun `visit traverses elements with their index`() {
        val arr = JsonArray()
        arr.add("x")
        arr.add("y")
        val visited = mutableListOf<Int>()
        arr.visit { idx, _ -> visited.add(idx) }
        assertEquals(listOf(0, 1), visited)
    }
}
