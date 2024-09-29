package lib

import lib.models.Plan
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists

private class ScheduleDebug(scheduleLength: Int) : Scheduler(scheduleLength) {
    override fun planSchedule(inputPath: String, outputPath: String) {
        super.planSchedule(inputPath, outputPath)
        val totalTasks = File(inputPath).inputStream().bufferedReader().readLines().size
        assertEquals(totalTasks - plannedIntervals.size, cancelledTasks.size,
            "Planned (${plannedIntervals.size}) and cancelled (${cancelledTasks.size}) tasks don't add up to the total ($totalTasks) tasks")
        for (i: Plan? in timetable.values) {
            if (i?.task != null) {
                val occurrences = timetable.values.filter { it?.task != null && it.task.id == i.task.id  }.size
                assertEquals(occurrences, i.task.estimateInDays, "Task duration does not match number of slots found in timetable")
            }
        }
    }

    override fun printSchedule() {}
}

class SchedulerTest {
    @BeforeEach
    fun deleteTestArtifacts() {
        Path(TEST_OUTPUT).deleteIfExists()
        Path(TEST_INPUT).deleteIfExists()
    }

    @Test
    fun testNonOverlappingIntervals() {
        File(TEST_INPUT).writeText("""
            1 2
            3 5
            9 2
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            3
            1 2
            3 7
            9 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testScheduleOverflow() {
        File(TEST_INPUT).writeText("""
            1 3
            4 3
            7 3
            9 4
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            3
            1 3
            4 6
            7 9
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testOverflowOnLimit() {
        File(TEST_INPUT).writeText("""
            1 3
            4 3
            7 3
            10 2
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            3
            1 3
            4 6
            7 9
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testFindFutureSlot() {
        File(TEST_INPUT).writeText("""
            1 3
            1 7
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            2
            1 3
            4 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testReschedule() {
        File(TEST_INPUT).writeText("""
            1 4
            2 2
            4 2
            7 2
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            4
            1 4
            5 6
            7 8
            9 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testOutOfBoundsTask() {
        File(TEST_INPUT).writeText("""
            3 5
            4 2
            1 2
            11 1
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            3
            1 2
            4 5
            6 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testLastDayTask() {
        File(TEST_INPUT).writeText("""
            1 5
            5 4
            10 1
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            3
            1 5
            6 9
            10 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testNotMovedBeforeReadyDate() {
        File(TEST_INPUT).writeText("""
            5 2
            6 2
            7 2
            5 2
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            3
            5 6
            7 8
            9 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testMultipleTasksOnSameDay() {
        File(TEST_INPUT).writeText("""
            1 3
            1 4
            1 2
            1 2
            1 3
        """.trimIndent())
        ScheduleDebug(SCHEDULE_LENGTH).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            4
            1 2
            3 4
            5 7
            8 10
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testModifiedScheduleLength() {
        File(TEST_INPUT).writeText("""
            5 2
            1 4
            12 4
            2 6
            8 7
        """.trimIndent())
        ScheduleDebug(20).planSchedule(TEST_INPUT, TEST_OUTPUT)
        assertEquals("""
            4
            1 4
            5 6
            7 12
            13 16
        """.trimIndent(), File(TEST_OUTPUT).inputStream().bufferedReader().readText())
    }

    @Test
    fun testForErrorsWithRandomInput() {
        for (i in 1..100) {
            val scheduleLength = (1..20).random()
            val input = (1..(1..scheduleLength).random()).joinToString("\n") {
                "${(1..scheduleLength).random()} ${(1..scheduleLength).random()}"
            }
            File(TEST_INPUT).writeText(input)
            assertDoesNotThrow(
                { ScheduleDebug(scheduleLength).planSchedule(TEST_INPUT, TEST_OUTPUT) },
                "Exception was thrown in the scheduler with schedule length '$scheduleLength' for input $input")
        }
    }
}