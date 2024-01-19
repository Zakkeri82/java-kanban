package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача1", "Описание1");
    }

    @Test
    void checkTaskWithOneId() {
        assertEquals(task, task, "Задачи с одинаковым id не равны друг другу");
    }
}