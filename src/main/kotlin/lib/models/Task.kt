package lib.models

data class Task(val deliveredOnDay: Int, val estimateInDays: Int) {
    val id = idCounter++
    companion object {
        @JvmStatic
        var idCounter = 0
    }
}
