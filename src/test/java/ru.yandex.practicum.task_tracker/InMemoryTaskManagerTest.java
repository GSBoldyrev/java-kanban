package test.java.ru.yandex.practicum.task_tracker;

import main.java.ru.yandex.practicum.task_tracker.task_managers.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
