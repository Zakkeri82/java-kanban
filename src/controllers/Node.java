package controllers;

import tasks.Task;

public class Node {

    private final Task item;
    private Node prev;
    private Node next;

    Node(Task element, Node prev, Node next) {
        this.item = element;
        this.prev = prev;
        this.next = next;
    }

    public Task getItem() {
        return item;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}