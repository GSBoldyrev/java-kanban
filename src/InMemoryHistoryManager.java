import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private int size = 0; // размер двусвязного списка
    private Node head; // начало списка
    private Node tail; // конец списка
    private final Map<Integer, Node> history = new HashMap<>(); // теперь хранится здесь

    // добавление новой задачи в конец двусвязного списка
    public void linkLast (Task task) {
        final Node oldTail = tail;
        final Node newTail = new Node(task, oldTail, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        size++;
    }

    // метод беред задачи из двусвязного списка и помещает в возвращаемый ArrayList
    // при тестах метод выдавал ошибку NullPointerException, поэтому добавил проверку на null в цикле
    // не уверенб что это лучшие и правильный метод, поэтому жду комментариев :)
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head; // начинаем обход списка с головы
        for (int i = 0; i < size && (currentNode != null); i++) {
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
        size--;
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
