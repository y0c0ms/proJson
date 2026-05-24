package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReferencesTest {

    // ── Test models ───────────────────────────────────────────────────────────

    data class DateValue(val day: Int, val month: Int, val year: Int)

    class Task(
        val description: String,
        val deadline: DateValue?,
        @Reference val dependencies: List<Task> = emptyList()
    )

    class Node(
        val label: String,
        @Reference var next: Node? = null
    )

    // ── Test 1: regular class gets $id ────────────────────────────────────────

    @Test
    fun `regular class gets $id property`() {
        val t = Task("T1", null)
        val json = ProJson().toJson(t) as JsonObject
        assertNotNull(json.getProperty("\$id"))
    }

    // ── Test 2: same object in @Reference field emits $ref ───────────────────

    @Test
    fun `same object in @Reference field emits $ref`() {
        val t1 = Task("T1", null)
        val t3 = Task("T3", null, listOf(t1))
        val json = ProJson().toJson(listOf(t1, t3)) as JsonArray
        val j1 = json.get(0) as JsonObject
        val j3 = json.get(1) as JsonObject
        val uuid = (j1.getProperty("\$id") as JsonPrimitive).value as String
        val deps = j3.getProperty("dependencies") as JsonArray
        assertEquals(uuid, (deps.get(0) as JsonReference).id)
    }

    // ── Test 3: @Reference field emits $ref ───────────────────────────────────

    @Test
    fun `@Reference field emits $ref instead of inlining`() {
        val t1 = Task("T1", null)
        val t3 = Task("T3", null, listOf(t1))
        val json = ProJson().toJson(listOf(t1, t3)) as JsonArray
        val j1 = json.get(0) as JsonObject
        val j3 = json.get(1) as JsonObject
        val id1 = (j1.getProperty("\$id") as JsonPrimitive).value as String
        val deps = j3.getProperty("dependencies") as JsonArray
        val dep = deps.get(0) as JsonReference
        assertEquals(id1, dep.id)
    }

    // ── Test 4: data class has no $id ─────────────────────────────────────────

    @Test
    fun `data class does not get $id property`() {
        val d = DateValue(15, 6, 2026)
        val json = ProJson().toJson(d) as JsonObject
        assertNull(json.getProperty("\$id"))
    }

    // ── Test 5: @Reference field with unseen object inlines it with $id ─────────

    @Test
    fun `@Reference field inlines object with $id when not previously seen`() {
        val t1 = Task("T1", null)
        val t3 = Task("T3", null, listOf(t1))
        // Only t3 is in the top-level list — t1 is only reachable via @Reference
        val json = ProJson().toJson(listOf(t3)) as JsonArray
        val j3 = json.get(0) as JsonObject
        val deps = j3.getProperty("dependencies") as JsonArray
        // t1 was never seen before, so it must be inlined (not a $ref)
        val dep = deps.get(0) as JsonObject
        assertNotNull(dep.getProperty("\$id"))
        assertEquals(JsonPrimitive("T1"), dep.getProperty("description"))
    }

    // ── Test 6: circular reference does not overflow ──────────────────────────

    @Test
    fun `circular reference does not cause StackOverflowError`() {
        val a = Node("A")
        val b = Node("B")
        a.next = b
        b.next = a
        val json = ProJson().toJson(listOf(a, b))
        assertNotNull(json)
    }

    // ── Test 6: list of tasks matches spec ────────────────────────────────────

    @Test
    fun `list of tasks matches the spec JSON structure`() {
        val t1 = Task("T1", DateValue(1, 1, 2026))
        val t2 = Task("T2", DateValue(2, 2, 2026))
        val t3 = Task("T3", null, listOf(t1, t2))

        val json = ProJson().toJson(listOf(t1, t2, t3)) as JsonArray

        val j1 = json.get(0) as JsonObject
        val j2 = json.get(1) as JsonObject
        val j3 = json.get(2) as JsonObject

        val id1 = (j1.getProperty("\$id") as JsonPrimitive).value as String
        val id2 = (j2.getProperty("\$id") as JsonPrimitive).value as String
        assertNotNull(id1)
        assertNotNull(id2)

        val deps = j3.getProperty("dependencies") as JsonArray
        val dep1 = deps.get(0) as JsonReference
        val dep2 = deps.get(1) as JsonReference
        assertEquals(id1, dep1.id)
        assertEquals(id2, dep2.id)
    }
}