package lib

import lib.models.Interval
import lib.models.Task
import java.io.File
import java.nio.file.Files
import kotlin.io.path.*

class SchedulerIO {
    companion object {
        @JvmStatic
        fun readTasks(path: String): List<Task> {
            val tasks = mutableListOf<Task>()
            val lines = File(path).inputStream().bufferedReader().readLines()
            lines.forEach {
                val (deliveryDay, estimate) = it.split(" ")
                if (!deliveryDay.all { c -> c.isDigit() } || !estimate.all { c -> c.isDigit() }) {
                    throw Exception("Cannot parse input file on line ${lines.indexOf(it) + 1}. Value was '$it'. Expected two Positive integer values for both delivery day and estimate values.")
                }
                val readyInt = deliveryDay.toInt()
                val durationInt = estimate.toInt()
                if (readyInt <= 0 || durationInt <= 0) {
                    throw Exception("Cannot parse input file on line ${lines.indexOf(it) + 1}. Value was '$it'. Expected two Positive integer values for both delivery day and estimate values.")
                }
                tasks.add(Task(readyInt, durationInt))
            }
            return tasks
        }

        @JvmStatic
        fun writeReport(path: String, plannedIntervals: List<Interval>) {
            Files.createDirectories(Path(path).parent)
            File(path).writeText("${plannedIntervals.size}\n${plannedIntervals.joinToString("\n") { "${it.start} ${it.end}" }}")
        }
    }
}