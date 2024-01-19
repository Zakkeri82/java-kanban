package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    TaskManager inMemoryTaskManager;

    Task task;

    Epic epic;

    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
        task = new Task("Задача1", "Описание1");
        epic = new Epic("Эпик1", "Описание1");
        subtask = new Subtask("Подзадача1", "Описание1");
    }

    @Test
    void checkAddInMemoryTaskManagerDifferentType() {
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtaskByEpic(epic,subtask);
        assertEquals(task.getId(), inMemoryTaskManager.getTaskById(1).getId(), "Задача не найдена по созданному Id");
        assertEquals(epic.getId(), inMemoryTaskManager.getEpicById(2).getId(), "Эпик не найдена по созданному Id");
        assertEquals(subtask.getId(), inMemoryTaskManager.getSubtaskById(3).getId(), "Подзадача не найдена по созданному Id");
    }
}