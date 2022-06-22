package misc;

import history_managers.HistoryManager;
import history_managers.InMemoryHistoryManager;
import task_managers.FileBackedTaskManager;
import task_managers.TaskManager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
