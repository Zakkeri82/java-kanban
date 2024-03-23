package controllers;

import enums.Status;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static controllers.InMemoryTaskManager.checkIntersectionTime;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    Task task;
    Task task2;
    Epic epic;
    Subtask subtask;
    Subtask subtask2;

    @Test
    void checkAddInMemoryTaskManagerDifferentType() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtaskByEpic(epic, subtask);
        assertEquals(task.getId(), taskManager.getTaskById(1).getId(), "Задача не найдена по созданному Id");
        assertEquals(epic.getId(), taskManager.getEpicById(2).getId(), "Эпик не найдена по созданному Id");
        assertEquals(subtask.getId(), taskManager.getSubtaskById(3).getId(), "Подзадача не найдена по созданному Id");
    }

    @Test
    void checkSubtaskHaveEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtaskByEpic(epic, subtask);
        assertEquals(subtask.getEpicId(), 1, "У подзадачи нет эпика");
    }

    @Test
    void checkStatusEpicNewWhereAllSubtaskNew() {
        taskManager.createEpic(epic);
        taskManager.createSubtaskByEpic(epic, subtask);
        taskManager.createSubtaskByEpic(epic, subtask2);
        assertEquals(epic.getStatus(), Status.NEW, "У эпика нет статуса NEW");
    }

    @Test
    void checkStatusEpicDoneWhereAllSubtaskDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtaskByEpic(epic, subtask);
        taskManager.createSubtaskByEpic(epic, subtask2);
        subtask.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getStatus(), Status.DONE, "У эпика нет статуса DONE");
    }

    @Test
    void checkStatusEpicInProgressWhereSubtaskNewAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtaskByEpic(epic, subtask);
        taskManager.createSubtaskByEpic(epic, subtask2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "У эпика нет статуса IN_PROGRESS");
    }

    @Test
    void checkStatusEpicInProgressWhereAllSubtaskInProgress() {
        taskManager.createEpic(epic);
        taskManager.createSubtaskByEpic(epic, subtask);
        taskManager.createSubtaskByEpic(epic, subtask2);
        subtask.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "У эпика нет статуса IN_PROGRESS");
    }

    @Test
    void checkIntersectionDifferentTimeTwoTask() {
        taskManager.createTask(task);
        taskManager.createTask(task2);
        assertFalse(checkIntersectionTime(task, task2));
    }

    @Test
    void checkIntersectionEqualTimeTwoTask() {
        taskManager.createTask(task);
        Task task3 = new Task("Задача2", "Описание2", LocalDateTime.now().minusMinutes(1), Duration.ofHours(5));
        taskManager.createTask(task3);
        assertTrue(checkIntersectionTime(task, task3));
    }
}
