package controllers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history;

    private final ArrayList<Node> altLinkedList;

    private Node first;

    private Node last;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
        this.altLinkedList = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        linkLast(task);
        history.put(task.getId(), altLinkedList.get(altLinkedList.size() - 1));

    }

    @Override
    public ArrayList<Task> getHistory() {
        if (history.isEmpty()) {
            return null;
        }
        return (ArrayList<Task>) getTasksHistory(first);
    }

    @Override
    public void remove(int id) {
        removeNode(history.remove(id));
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
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

    private List<Task> getTasksHistory(Node node) {
        ArrayList<Task> tasks = new ArrayList<>();
        while (node != null) {
            tasks.add(node.getItem());
            node = node.getNext();
        }
        return tasks;
    }

    private void linkLast(Task task) {
        remove(task.getId());
        Node node;
        if (altLinkedList.isEmpty()) {
            node = new Node(task, null, null);
            altLinkedList.add(node);
            first = node;
        } else {
            node = new Node(task, last, null);
            altLinkedList.add(node);
            if (altLinkedList.size() == 2) {
                first.setNext(node);
            }
            last.setNext(node);
        }
        last = node;
    }
}