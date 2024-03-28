package controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void initTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Экземпляр менеджера не инициализирован");
        TaskManager taskManager2 = Managers.getDefault();
        assertNotNull(taskManager2, "Экземпляр менеджера не инициализирован");
    }
}