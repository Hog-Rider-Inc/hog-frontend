package lt.hogfood.hogfood

import lt.hogfood.hogfood.ui.history.formatDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatDateTest {

    @Test
    fun `formatDate converts ISO format to Lithuanian format`() {
        val result = formatDate("2026-03-29T12:00:00Z")
        assertEquals("29.03.2026", result)
    }

    @Test
    fun `formatDate handles single digit day and month`() {
        val result = formatDate("2026-01-05T08:30:00Z")
        assertEquals("05.01.2026", result)
    }

    @Test
    fun `formatDate returns original string on invalid input`() {
        val result = formatDate("invalid-date")
        assertEquals("invalid-date", result)
    }

    @Test
    fun `formatDate handles empty string`() {
        val result = formatDate("")
        assertEquals("", result)
    }
}