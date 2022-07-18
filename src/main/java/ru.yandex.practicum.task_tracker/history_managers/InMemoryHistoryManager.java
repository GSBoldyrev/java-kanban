package main.java.ru.yandex.practicum.task_tracker.history_managers;

import main.java.ru.yandex.practicum.task_tracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> history = new HashMap<>();

    // Добавление новой задачи в конец двусвязного списка
    public void linkLast(Task task) {
        final Node newTail = new Node(task, tail, null);
        if (head == null) {
            head = newTail;
        } else {
            tail.next = newTail;
        }
        tail = newTail;
    }

    // Метод возвращает список задач из двусвязного списка в List, в порядке их вызова, начиная с первой.
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    // Метод удаляет узел из списка
    public void removeNode(Node node) {
        if (node.previous == null && node.next == null) {
            head = null;
            return;
        }
        if (node.previous != null) {
            node.previous.next = node.next;
            if (node.next == null) {
                tail = node.previous;
            }
        }
        if (node.next != null) {
            node.next.previous = node.previous;
            if (node.previous == null) {
                head = node.next;
            }
        }
    }

    // Добавление задачи в историю. В первую очередь удаляется прошлый вызов задачи.
    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
        history.put(task.getId(), tail);
    }

    // Удаление задачи из двусвязного списка и таблицы.
    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    // Метод возвращает список задач в порядке их вызова, начиная с первой
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public String toString() {
        StringBuilder history = new StringBuilder();
        for (Task task : this.getHistory()) {
            history.append(",").append(task.getId());
        }
        history.delete(0, 1);
        return history.toString();
    }
}
