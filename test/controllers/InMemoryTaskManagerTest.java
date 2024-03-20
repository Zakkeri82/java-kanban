package controllers;

import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        task = new Task("Задача1", "Описание1", LocalDateTime.now(), Duration.ofHours(5));
        task2 = new Task("Задача2", "Описание2", LocalDateTime.now().plusHours(6), Duration.ofHours(5));
        epic = new Epic("Эпик1", "ОписаниеЭпика1");
        subtask = new Subtask("Подзадача1", "ОписаниеПодзадачи1", LocalDateTime.now().plusHours(20), Duration.ofHours(6));
        subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадачи2", LocalDateTime.now().plusHours(30), Duration.ofHours(6));
    }
}