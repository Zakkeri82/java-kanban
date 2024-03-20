package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача1", "Описание1", LocalDateTime.now(), Duration.ofHours(1));
    }

    @Test
    void checkTaskWithOneId() {
        assertEquals(task.getId(), task.getId(), "Задачи с одинаковым id не равны друг другу");
    }
}