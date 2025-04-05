package br.dev.lucasena.planner.ui.viewmodel

data class NewActivity(
    val name: String? = null,
    val date: SetDate? = null,
    val time: SetTime? = null,
) {
    fun isFilled(): Boolean = !name.isNullOrEmpty() && date != null && time != null
}

data class SetDate(
    val year: Int,
    val month: Int,
    val day: Int
)

data class SetTime(
    val hour: Int,
    val minute: Int
)