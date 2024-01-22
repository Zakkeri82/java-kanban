package controllers;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task.clone());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}