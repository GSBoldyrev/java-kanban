package main.java.ru.yandex.practicum.task_tracker.misc;

import main.java.ru.yandex.practicum.task_tracker.history_managers.HistoryManager;
import main.java.ru.yandex.practicum.task_tracker.history_managers.InMemoryHistoryManager;
import main.java.ru.yandex.practicum.task_tracker.task_managers.HTTPTaskManager;
import main.java.ru.yandex.practicum.task_tracker.task_managers.TaskManager;

public class Managers {

    public static TaskManager getDefault(String url, String key) {
        return new HTTPTaskManager(url, key);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
