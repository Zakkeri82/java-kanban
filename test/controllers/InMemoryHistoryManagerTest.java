package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Задача1", "Описание1");
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
        historyManager.add(task);
        task.setDescription("Измененное описание");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(history.get(0).getDescription(), "Измененное описание",
                "Предыдущай версия задачи изменилась в истории просмотров");
    }
}