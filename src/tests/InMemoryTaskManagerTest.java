package tests;

import task_managers.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
