package lib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.*

class SchedulerIOTest {
    @OptIn(ExperimentalPathApi::class)
    @Test
    fun testWriteToNonExistingDirectory() {
        val path = "src/test/kotlin/test1/test2/testFile.txt"
        SchedulerIO.writeReport(path, listOf())
        Path("src/test/kotlin/test1").deleteRecursively()
    }

    @Test
    fun testNonDigitInput() {
        File(TEST_INPUT).writeText("""
            1 2
            3 5asd
            9 2
        """.trimIndent())
        val exception = assertThrows<Exception> { Scheduler(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT) }
        assertEquals("Cannot parse input file on line 2. Value was '3 5asd'. Expected two Positive integer values for both delivery day and estimate values.", exception.message)
    }

    @Test
    fun testZeroValueInput() {
        File(TEST_INPUT).writeText("""
            1 2
            3 1
            9 0
        """.trimIndent())
        val exception = assertThrows<Exception> { Scheduler(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT) }
        assertEquals("Cannot parse input file on line 3. Value was '9 0'. Expected two Positive integer values for both delivery day and estimate values.", exception.message)
    }

    @Test
    fun testMissingInput() {
        Path(TEST_INPUT).deleteIfExists()
        assertThrows<FileNotFoundException> { Scheduler(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT) }
    }
}