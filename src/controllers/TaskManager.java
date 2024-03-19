package controllers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void clearAllTasks();

    void clearAllSubtasks();

    void clearAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtaskByEpic(Epic epic, Subtask subtask);

    ArrayList<Subtask> getSubtasksByEpicId(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    HistoryManager getHistoryManager();

    ArrayList<Task> getPrioritizedTasks();
}