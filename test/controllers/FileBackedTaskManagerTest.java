package controllers;

import exception.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    static File tempFile;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача1", "Описание1", LocalDateTime.now(), Duration.ofHours(5));
        task2 = new Task("Задача2", "Описание2", LocalDateTime.now().plusHours(6), Duration.ofHours(5));
        epic = new Epic("Эпик1", "ОписаниеЭпика1");
        subtask = new Subtask("Подзадача1", "ОписаниеПодзадачи1", LocalDateTime.now().plusHours(20), Duration.ofHours(6));
        subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадачи2", LocalDateTime.now().plusHours(30), Duration.ofHours(6));
        try {
            tempFile = File.createTempFile("test-file", ".txt");
        } catch (IOException e) {
            throw new ManagerSaveException("Тестовый файл не создался");
        }
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void afterEach() {
        tempFile.deleteOnExit();
    }

    @Test()
    void checkSaveAndLoadEmptyFile() {
        assertTrue(tempFile.exists(), "Файл не был создан");
        assertEquals(0, tempFile.length(), "Файл не пустой");

        assertEquals(0, taskManager.tasks.size(), "Присутствует Task");
        assertEquals(0, taskManager.subTasks.size(), "Присутствует Subtask");
        assertEquals(0, taskManager.epics.size(), "Присутствует Epic");
        assertNull(taskManager.getHistoryManager().getHistory(), "Присутствует история");
    }

    @Test()
    void checkManagerSaveException() {
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File("noExist.csv")),
                "Загрузка с не существующего файла должна приводить к исключению");
    }

    @Test
    void checkSaveAndLoadTaskToFile() {
        LocalDateTime startTask = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate = LocalDateTime.now().format(formatter);
        Duration duration = Duration.ofHours(4);
        Task task1 = new Task("Первая", "простая", startTask, duration);
        taskManager.createTask(task1);
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);

        List<String> loadStrings;
        try {
            loadStrings = Files.readAllLines(tempFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("id,type,name,status,description,epic,startTime,duration", loadStrings.get(0), "Файл пустой");
        assertEquals("1,Task,Первая,NEW,простая,null," + formatDate + "," + duration.toMinutes(), loadStrings.get(1), "Task в файл не сохранён");
        assertEquals("2,Epic,Первый епик,NEW,простой,null,null,null", loadStrings.get(2), "Epic в файл не сохранён");
        FileBackedTaskManager newfileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, newfileBackedTaskManager.tasks.size(), "Не правильная загрузка Task");
        assertEquals(1, newfileBackedTaskManager.epics.size(), "Не правильная загрузка Epic");
    }
}