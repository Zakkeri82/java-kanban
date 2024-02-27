package controllers;

import enums.TypeTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private final File fileSave;

    public FileBackedTaskManager(File file) {
        this.fileSave = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileSave))) {
            String header = "id,type,name,status,description,epic\n";
            writer.write(header);
            getAllTasks().forEach(task -> {
                try {
                    writer.write(task.getId() + ", " + TypeTask.TASK + ", " + task.getNameTask()
                            + ", " + task.getStatus() + ", " + task.getDescription() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            getAllEpics().forEach(epic -> {
                try {
                    writer.write(epic.getId() + ", " + TypeTask.Epic + ", " + epic.getNameTask()
                            + ", " + epic.getStatus() + ", " + epic.getDescription() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtaskByEpic(Epic epic, Subtask subtask) {
        super.createSubtaskByEpic(epic, subtask);
        save();
    }
}
