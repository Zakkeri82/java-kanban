package controllers;

import exception.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static controllers.FileBackedTaskManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    static File tempFile;
    static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void beforeEach() {
        try {
            tempFile = File.createTempFile("test-file", ".txt");
        } catch (IOException e) {
            throw new ManagerSaveException("Тестовый файл не создался");
        }
        fileBackedTaskManager = loadFromFile(tempFile);
    }

    @AfterEach
    void afterEach() {
        tempFile.deleteOnExit();
    }

    @Test()
    void checkSaveAndLoadEmptyFile() {
        assertTrue(tempFile.exists(), "Файл не был создан");
        assertEquals(0, tempFile.length(), "Файл не пустой");

        assertEquals(0, fileBackedTaskManager.tasks.size(), "Присутствует Task");
        assertEquals(0, fileBackedTaskManager.subTasks.size(), "Присутствует Subtask");
        assertEquals(0, fileBackedTaskManager.epics.size(), "Присутствует Epic");
        assertNull(fileBackedTaskManager.getHistoryManager().getHistory(), "Присутствует история");
    }

    @Test
    void checkSaveAndLoadTaskToFile() {
        Task task1 = new Task("Первая", "простая");
        fileBackedTaskManager.createTask(task1);
        Epic epic = new Epic("Первый епик", "простой");
        fileBackedTaskManager.createEpic(epic);
        List<String> loadStrings;
        try {
            loadStrings = Files.readAllLines(tempFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("id,type,name,status,description,epic", loadStrings.get(0), "Файл пустой");
        assertEquals("1, Task, Первая, NEW, простая", loadStrings.get(1), "Task в файл не сохранён");
        assertEquals("2, Epic, Первый епик, NEW, простой, []", loadStrings.get(2), "Epic в файл не сохранён");
    }
}