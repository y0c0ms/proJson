package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonArrayTest {

    @Test
    fun `empty array serializes to empty brackets`() {
        val arr = JsonArray()
        println("empty array -> ${arr.toJsonString()}")
        assertEquals("[]", arr.toJsonString())
    }

    @Test
    fun `add string element`() {
        val arr = JsonArray()
        arr.add("a")
        println("string element -> ${arr.toJsonString()}")
        assertEquals("[\"a\"]", arr.toJsonString())
    }

    @Test
    fun `add null element`() {
        val arr = JsonArray()
        arr.add(null)
        println("null element -> ${arr.toJsonString()}")
        assertEquals("[null]", arr.toJsonString())
    }

    @Test
    fun `add multiple elements`() {
        val arr = JsonArray()
        arr.add("a")
        arr.add(null)
        arr.add("b")
        println("multiple elements -> ${arr.toJsonString()}")
        assertEquals("[\"a\", null, \"b\"]", arr.toJsonString())
    }

    @Test
    fun `spec example - add to existing list`() {
        val arr = JsonArray()
        arr.add("a")
        arr.add(null)
        arr.add("b")
        arr.add("c")
        println("spec list example -> ${arr.toJsonString()}")
        assertEquals("[\"a\", null, \"b\", \"c\"]", arr.toJsonString())
    }

    @Test
    fun `remove by index`() {
        val arr = JsonArray()
        arr.add("a")
        arr.add("b")
        arr.add("c")
        arr.remove(1)
        println("after remove -> ${arr.toJsonString()}")
        assertEquals("[\"a\", \"c\"]", arr.toJsonString())
    }

    @Test
    fun `get by index`() {
        val arr = JsonArray()
        arr.add("hello")
        println("get(0) -> ${arr.get(0)}")
        assertEquals(JsonPrimitive("hello"), arr.get(0))
    }

    @Test
    fun `size returns element count`() {
        val arr = JsonArray()
        arr.add(1)
        arr.add(2)
        println("size -> ${arr.size()}")
        assertEquals(2, arr.size())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val arr = JsonArray()
        arr.add(42)
        println("toString -> $arr")
        assertEquals("[42]", arr.toString())
    }

    @Test
    fun `add complex type throws`() {
        val arr = JsonArray()
        val ex = assertFailsWith<IllegalArgumentException> {
            arr.add(listOf(1, 2))
        }
        println("complex type threw -> ${ex.message}")
    }

    @Test
    fun `nested array serializes correctly`() {
        val inner = JsonArray()
        inner.add(1)
        inner.add(2)
        val outer = JsonArray()
        outer.add(inner)
        println("nested -> ${outer.toJsonString()}")
        assertEquals("[[1, 2]]", outer.toJsonString())
    }

    @Test
    fun `visit traverses elements with their index`() {
        val arr = JsonArray()
        arr.add("x")
        arr.add("y")
        val visited = mutableListOf<Int>()
        arr.visit { idx, _ -> visited.add(idx) }
        println("visited indices -> $visited")
        assertEquals(listOf(0, 1), visited)
    }
}