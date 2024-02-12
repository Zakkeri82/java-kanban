package controllers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history;

    private Node first;

    private Node last;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node node;
        if (history.isEmpty()) {
            node = new Node(task, null, null);
            history.put(task.getId(), node);
            first = node;
        } else {
            node = new Node(task, last, null);
            history.put(task.getId(), node);
            if (history.size() == 2) {
                first.setNext(node);
            }
            last.setNext(node);
        }
        last = node;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        return getTasksHistory(first, tasks);
    }

    @Override
    public void remove(int id) {
        if(history.size() > 1) {
            removeNode(history.get(id));
        }
        history.remove(id);
    }

    private void removeNode(Node node) {
        if (history.isEmpty()) return;
        if (node.getNext() == null) {
            node.getPrev().setNext(null);
            last = node.getPrev();
        } else if (node.getPrev() == null) {
            node.getNext().setPrev(null);
            first = node.getNext();
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }

    private ArrayList<Task> getTasksHistory(Node node, ArrayList<Task> tasks) {
        tasks.add(node.getItem());
        if(node.getNext() != null) {
            getTasksHistory(node.getNext(), tasks);
        }
        return tasks;
    }
}