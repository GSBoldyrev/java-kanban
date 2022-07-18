package main.java.ru.yandex.practicum.task_tracker.history_managers;

import main.java.ru.yandex.practicum.task_tracker.tasks.Task;

public class Node {
    public Task task;
    public Node previous;
    public Node next;

    public Node(Task task, Node previous, Node next) {
        this.task = task;
        this.previous = previous;
        this.next = next;
    }
}
