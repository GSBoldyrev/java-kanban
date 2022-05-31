package misc;

import historyManagers.HistoryManager;
import historyManagers.InMemoryHistoryManager;
import taskManagers.InMemoryTaskManager;
import taskManagers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
