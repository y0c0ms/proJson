package projson

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JsonReferenceTest {

    @Test
    fun `reference serializes as dollar-ref object`() {
        val ref = JsonReference("9e2e6c64-3236-45b7-8b8a-11271c69e4df")
        println("uuid ref -> ${ref.toJsonString()}")
        assertEquals("{\"\$ref\": \"9e2e6c64-3236-45b7-8b8a-11271c69e4df\"}", ref.toJsonString())
    }

    @Test
    fun `toString delegates to toJsonString`() {
        val ref = JsonReference("abc-123")
        println("toString -> $ref")
        assertEquals("{\"\$ref\": \"abc-123\"}", ref.toString())
    }
}