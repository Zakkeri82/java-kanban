package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    TaskManager manager;

    Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        manager = new InMemoryTaskManager();
        task = new Task("Задача1", "Описание1", LocalDateTime.now(), Duration.ofHours(10));
    }

    @Test
    void addHistory1Task() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Размер истории не равен еденице.");
    }

    @Test
    void checkVersionPreviousTaskData() {
        manager.createTask(task);
        historyManager.add(task);
        task.setDescription("Измененное описание");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(history.get(history.size() - 1).getDescription(), "Измененное описание",
                "Предыдущай версия задачи не изменилась в истории просмотров");
    }

    @Test
    void checkIsEmptyHistory() {
        manager.createTask(task);
        final List<Task> history = historyManager.getHistory();
        assertNull(history, "История не пустая");
    }

    @Test
    void checkHaveNotDuplicationTask() {
        manager.createTask(task);
        historyManager.add(task);
        task.setDescription("Измененное описание");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(history.size(), 1, "Продублировано добавление задачи в историю просмотров");
    }
}