import java.util.List;

public interface HistoryManager {

    // Добавление задачи в историю просмотров
    void add(Task task);

    // Вызов истории просмотров
    List<Task> getHistory();
}
