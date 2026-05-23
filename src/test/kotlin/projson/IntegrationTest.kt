package projson

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IntegrationTest {

    data class Date(val day: Int, val month: Int, val year: Int)
    class Task(val description: String, val deadline: Date?, val dependencies: List<Task>)

    @Test
    fun `spec example - Date toJson then setProperty`() {
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject
        json.setProperty("year", 2027)
        val text = json.toJsonString()

        assertEquals(true, text.contains("\"\$type\": \"Date\""))
        assertEquals(true, text.contains("\"day\": 31"))
        assertEquals(true, text.contains("\"month\": 4"))
        assertEquals(true, text.contains("\"year\": 2027"))
    }

    @Test
    fun `spec example - list of strings with add`() {
        val list = listOf("a", null, "b")
        val json = ProJson().toJson(list) as JsonArray
        json.add("c")
        assertEquals("[\"a\", null, \"b\", \"c\"]", json.toJsonString())
    }

    @Test
    fun `three tasks with dependencies`() {
        val t1 = Task("T1", Date(30, 2, 2026), emptyList())
        val t2 = Task("T2", Date(31, 4, 2026), emptyList())
        val t3 = Task("T3", null, listOf(t1, t2))
        val all = listOf(t1, t2, t3)

        val json = ProJson().toJson(all) as JsonArray
        assertEquals(3, json.size())

        val jt1 = json.get(0) as JsonObject
        assertEquals(JsonPrimitive("Task"), jt1.getProperty("\$type"))
        assertEquals(JsonPrimitive("T1"), jt1.getProperty("description"))

        val jt3 = json.get(2) as JsonObject
        assertEquals(JsonPrimitive("Task"), jt3.getProperty("\$type"))
        assertEquals(JsonPrimitive(null), jt3.getProperty("deadline"))

        val deps = jt3.getProperty("dependencies") as JsonArray
        assertEquals(2, deps.size())
        assertIs<JsonObject>(deps.get(0))
    }
}
