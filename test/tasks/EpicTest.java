package tasks;

import controllers.Managers;
import controllers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {

    TaskManager inMemoryTaskManager;

    Epic epic;

    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
        epic = new Epic("Эпик1", "Описание1");
        subtask = new Subtask("Подзадача1", "Описание1", LocalDateTime.now(), Duration.ofHours(10));
    }

    @Test
    void checkEpicWithOneId() {
        assertEquals(epic, epic, "Эпики с одинаковым id не равны друг другу");
    }

    @Test
    void checkAddEpicHowSubtask() {
        inMemoryTaskManager.createSubtaskByEpic(new Epic("Не созданный эпик", "Нужна инициализация в списке"), subtask);
        assertNull(inMemoryTaskManager.getSubtaskById(1), "Подзадача создалась");
    }
}