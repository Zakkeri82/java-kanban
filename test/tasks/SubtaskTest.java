package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask("Подзадача1", "Описание1", LocalDateTime.now(), Duration.ofHours(5));
    }

    @Test
    void checkSubtaskWithOneId() {
        assertEquals(subtask.getId(), subtask.getId(), "Подзадачи с одинаковым id не равны друг другу");
    }
}