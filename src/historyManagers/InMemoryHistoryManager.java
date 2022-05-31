package historyManagers;

import tasks.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head; // начало списка
    private Node tail; // конец списка
    private final Map<Integer, Node> history = new HashMap<>(); // история теперь хранится здесь

    // добавление новой задачи в конец двусвязного списка
    public void linkLast (Task task) {
        final Node newTail = new Node(task, tail, null);
        if (head == null) {
            head = newTail;
        } else {
            tail.next = newTail;
        }
        tail = newTail;
    }

    // С применением цикла while и проверкой на null стало гораздо читабельнее и красивее!
    // И я правильно сделал, что удалил вообще переменную size? Кажется, что надобность в ней отпала
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head; // начинаем обход списка с головы
        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next; // берем следующий узел
        }
        return tasks;
    }

    // метод удаляет узел из списка
    public void removeNode (Node node) {
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

    @Override
    public void add(Task task) {
        remove(task.getId()); // если в истории уже есть задача с таким же id, удаляем ее
        linkLast(task); // потом добавляем в двусвязный список и в HashMap
        history.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
       return getTasks();
    }
}
