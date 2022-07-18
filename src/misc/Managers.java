package misc;

import history_managers.HistoryManager;
import history_managers.InMemoryHistoryManager;
import task_managers.HTTPTaskManager;
import task_managers.TaskManager;

public class Managers {

    public static TaskManager getDefault(String url, String key) {
        return new HTTPTaskManager(url, key);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
