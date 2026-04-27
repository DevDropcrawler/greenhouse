package nz.keeleysgreenhouse.app.ui.tasks

import nz.keeleysgreenhouse.app.data.entity.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TasksViewModelTest {

    private val today = LocalDate.of(2026, 4, 27)

    private fun task(id: Long, due: LocalDate, title: String = "T") =
        Task(id = id, plantingId = 1L, cropId = 1, dueDate = due.toString(), title = title, done = false)

    @Test
    fun groupsTasksByRelativeDate() {
        val tasks = listOf(
            task(1, today),
            task(2, today.plusDays(1)),
            task(3, today.plusDays(3)),
            task(4, today.plusDays(20)),
            task(5, today.minusDays(2))
        )
        val groups = TasksViewModel.group(tasks, today)

        assertEquals(setOf(1L, 5L), groups[TaskGroup.TODAY]!!.map { it.id }.toSet())
        assertEquals(listOf(2L), groups[TaskGroup.TOMORROW]!!.map { it.id })
        assertEquals(listOf(3L), groups[TaskGroup.THIS_WEEK]!!.map { it.id })
        assertEquals(listOf(4L), groups[TaskGroup.LATER]!!.map { it.id })
    }

    @Test
    fun emptyTaskListProducesEmptyGroups() {
        val groups = TasksViewModel.group(emptyList(), today)
        assertTrue(groups.isEmpty())
    }
}
