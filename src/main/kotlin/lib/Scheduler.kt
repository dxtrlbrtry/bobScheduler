package lib

import lib.models.*

open class Scheduler(scheduleLength: Int) {
    protected val timetable: MutableMap<Int, Plan?> = mutableMapOf()
    protected val cancelledTasks: MutableList<Task> = mutableListOf()
    protected val plannedIntervals: MutableList<Interval> = mutableListOf()
    init { (1..scheduleLength).map { timetable[it] = null } }

    open fun planSchedule(inputPath: String, outputPath: String) {
        val tasks: List<Task> = FileIO.readTasks(inputPath)
            .sortedWith(compareBy({ it.estimateInDays }, { it.deliveredOnDay }))
        tasks.forEach { scheduleTask(it) }
        printSchedule()
        FileIO.writeReport(outputPath, plannedIntervals)
    }

    private fun scheduleTask(task: Task) {
        val interval = Interval(task.deliveredOnDay, task.deliveredOnDay + task.estimateInDays - 1)
        if (!canScheduleInterval(interval)) {
            var freeSlots = getFreeSlotsAfterDay(task.deliveredOnDay)
            if (freeSlots.size < task.estimateInDays) {
                cancelledTasks.add(task)
                return
            }
            var newInterval: Interval? = findFreeConsecutiveIntervalInFuture(task, freeSlots)
            while (newInterval == null) {
                delayFirstTaskRange(freeSlots)
                freeSlots = getFreeSlotsAfterDay(task.deliveredOnDay)
                newInterval = findFreeConsecutiveIntervalInFuture(task, freeSlots)
            }
            interval.start = newInterval.start
            interval.end = newInterval.end
        }
        updateTimetable(Plan(interval, task))
    }

    open fun printSchedule() {
        println("Timetable:")
        timetable.map { println("${it.key}, ${it.value?.task}") }
        println("Planned Tasks:")
        plannedIntervals.map { println("$it, ${timetable[it.start]?.task}") }
        println("Cancelled Tasks:")
        cancelledTasks.sortedWith(compareBy({ it.estimateInDays }, { it.deliveredOnDay })).map { println(it) }
    }

    private fun updateTimetable(plan: Plan) {
        (plan.interval.start..plan.interval.end).forEach { timetable[it] = plan}
        plannedIntervals.add(plan.interval)
        plannedIntervals.sortBy { it.start }
    }

    private fun canScheduleInterval(interval: Interval): Boolean {
        val validDays = timetable.keys.filter { it >= interval.start && it <= interval.end }
        return validDays.size == interval.end - interval.start + 1 && validDays.all { timetable[it] == null }
    }

    private fun findFreeConsecutiveIntervalInFuture(task: Task, freeSlots: IntArray): Interval? {
        if (task.estimateInDays == 1) { return Interval(freeSlots[0], freeSlots[0]) }
        var currentLength = 1
        for (i in 1..<freeSlots.size) {
            if (freeSlots[i - 1] + 1 != freeSlots[i]) {
                currentLength = 1
                continue
            }
            currentLength++
            if (currentLength == task.estimateInDays) {
                return Interval(freeSlots[i - currentLength + 1], freeSlots[i])
            }
        }
        return null
    }

    private fun getFreeSlotsAfterDay(afterDay: Int): IntArray {
        return timetable.filter { it.key >= afterDay && it.value == null }.map { it.key }.toIntArray()
    }

    private fun delayFirstTaskRange(freeSlots: IntArray) {
        val (start, end) = firstNonConsecutive(freeSlots)
            ?: throw Error("No non-consecutive free time slots found, task should already have found a slot or canceled")
        val firstMovableRange = Interval(start + 1, end)
        val rescheduledTaskIds = mutableListOf<Int>()
        for (i in firstMovableRange.end - 1 downTo firstMovableRange.start) {
            val slot = timetable[i]
                ?: throw Error("Expected all slots to be occupied in given time range")
            timetable[i + 1] = slot
            timetable[i] = null
            if (!rescheduledTaskIds.contains(slot.task.id)){
                slot.interval.start++
                slot.interval.end++
                rescheduledTaskIds.add(slot.task.id)
            }
        }
    }

    private fun firstNonConsecutive(array: IntArray): IntArray? {
        if (array.size < 2) {
            throw Error("Array size should be at least 2 for finding non-consecutive sequences.")
        }
        for (i in 1..<array.size) {
            if (array[i - 1] + 1 != array[i]) {
                return intArrayOf(array[i - 1], array[i])
            }
        }
        return null
    }
}
