package org.main

import lib.Scheduler

fun main() {
    val scheduleLength = System.getenv("SCHEDULE_LENGTH")?.toIntOrNull() ?: SCHEDULE_LENGTH
    Scheduler(scheduleLength).planSchedule(INPUT_PATH, OUTPUT_PATH)
}
