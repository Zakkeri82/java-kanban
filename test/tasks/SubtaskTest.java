package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask("Подзадача1", "Описание1");
    }

    @Test
    void checkSubtaskWithOneId() {
        assertEquals(subtask, subtask, "Подзадачи с одинаковым id не равны друг другу");
    }
}