package controllers;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private final File fileSave;

    public FileBackedTaskManager(File file) {
        this.fileSave = file;
    }

    /**
     * Сохраняет состояние менеджера в файл по строчно
     */
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileSave))) {
            writer.write("id,type,name,status,description,epic\n");
            getAllTasks().stream()
                    .map(CSVTaskManager::getStringFromTask)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ощибка записи в файл Task");
                        }
                    });
            getAllEpics().stream()
                    .map(CSVTaskManager::getStringFromTask)
                    .forEach(epicString -> {
                        try {
                            writer.write(epicString);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ощибка записи в файл Epic");
                        }
                    });
            getAllSubtasks().stream()
                    .map(CSVTaskManager::getStringFromTask)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ощибка записи в файл Subtask");
                        }
                    });
            try {
                if (getHistoryManager().getHistory() != null) {
                    writer.write(CSVTaskManager.historyToString(getHistoryManager()));
                } else {
                    writer.write("\nnull");
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ощибка записи в файл History");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не удалось открыть BufferedWriter");
        }
    }

    /**
     * Восстанавлвает состояния из сохраненного файла построчно
     *
     * @param file файл с сохраненным состоянием
     * @return восстановленый экземпляр менеджера задач
     */
    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath().toFile());
        List<String> loadStrings;
        Map<Integer, Task> temp = new HashMap<>();
        try {
            if (file.exists() && file.length() > 0) {
                loadStrings = Files.readAllLines(file.toPath());
                if (loadStrings.size() > 2) {
                    for (int i = 1; i < loadStrings.size() - 2; i++) {
                        Task task = CSVTaskManager.getTaskFromString(loadStrings.get(i));
                        temp.put(task.getId(), task);
                        if (task instanceof Epic) {
                            fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        } else if (task instanceof Subtask) {
                            fileBackedTaskManager.subTasks.put(task.getId(), (Subtask) task);
                        } else {
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                        }
                    }
                }
                List<Integer> id = CSVTaskManager.historyFromString(loadStrings.get(loadStrings.size() - 1));
                id.forEach(integer -> fileBackedTaskManager.getHistoryManager().add(temp.get(integer)));
            } else {
                System.out.println("Файл для загрузки данных отсутствует или пустой");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла");
        }
        return fileBackedTaskManager;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
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

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> arrSubtask = super.getSubtasksByEpicId(id);
        save();
        return arrSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }


    public static void main(String[] args) {
        System.out.println("Поехали!");
        FileBackedTaskManager fileBackedTaskManager = loadFromFile(new File("saveData.csv"));
        printAllTasks(fileBackedTaskManager);
    }

    private static void printAllTasks(TaskManager inMemoryTaskManager) {
        System.out.println("\nЗадачи:");
        for (Task task : inMemoryTaskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Task epic : inMemoryTaskManager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : inMemoryTaskManager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Task subtask : inMemoryTaskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория:");
        if (inMemoryTaskManager.getHistoryManager().getHistory() != null) {
            for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
                System.out.println(task);
            }
        } else {
            System.out.println("Истории запросов нет!");
        }
    }
}