package main.java.ru.yandex.practicum.task_tracker.history_managers;

import main.java.ru.yandex.practicum.task_tracker.tasks.Task;

import java.util.List;

public interface HistoryManager {

    // Добавление задачи в историю просмотров
    void add(Task task);

    // Удаление задачи из истории
    void remove(int id);

    // Вызов истории просмотров
    List<Task> getHistory();
}
