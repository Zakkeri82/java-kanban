package controllers;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("saveData.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}