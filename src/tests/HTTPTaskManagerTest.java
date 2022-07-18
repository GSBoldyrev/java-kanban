package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;
import task_managers.HTTPTaskManager;

import java.io.IOException;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    KVServer kvServer;

    @Override
    public HTTPTaskManager createManager() {
        return new HTTPTaskManager("http://localhost:8078", "key");
    }

    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void shouldLoadFromEmptyServer() throws IOException, InterruptedException {
        HTTPTaskManager emptyManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");
        Assertions.assertEquals(0, emptyManager.getAllTasks().size());
        Assertions.assertEquals(0, emptyManager.getAllSubTasks().size());
        Assertions.assertEquals(0, emptyManager.getAllEpics().size());
        Assertions.assertEquals(0, emptyManager.getHistory().size());
        Assertions.assertEquals(0, emptyManager.getId());
        Assertions.assertNotNull(emptyManager);
    }

    @Test
    public void shouldLoadWithOneEmptyEpic() throws IOException, InterruptedException {
        add2TestEpics();
        add2TestTasks();
        manager.getEpic(0);
        manager.getEpic(1);
        HTTPTaskManager oneEpicManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");
        Assertions.assertEquals(2, oneEpicManager.getAllEpics().size());
        Assertions.assertEquals(2, oneEpicManager.getAllTasks().size());
        Assertions.assertEquals(2, oneEpicManager.getHistory().size());
        Assertions.assertTrue(oneEpicManager.getHistory().containsAll(oneEpicManager.getAllEpics()));
        Assertions.assertEquals(0, oneEpicManager.getEpic(0).getSubTasks().size());
    }

    @Test
    public void shouldLoadWithEmptyHistory() throws IOException, InterruptedException {
        add2TestEpics();
        add5TestSubTasks();
        add2TestTasks();
        HTTPTaskManager emptyHistoryManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");
        Assertions.assertEquals(2, emptyHistoryManager.getAllEpics().size());
        Assertions.assertEquals(2, emptyHistoryManager.getAllTasks().size());
        Assertions.assertEquals(5, emptyHistoryManager.getAllSubTasks().size());
        Assertions.assertEquals(0, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldLoadWithoutTimings() throws IOException, InterruptedException {
        add2TestEpics();
        manager.addSubTask(subTask_1);
        add2TestTasks();
        manager.getSubTask(2);
        manager.getEpic(0);
        manager.getTask(3);
        manager.getTask(4);
        HTTPTaskManager emptyHistoryManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");
        Assertions.assertEquals(2, emptyHistoryManager.getAllEpics().size());
        Assertions.assertEquals(2, emptyHistoryManager.getAllTasks().size());
        Assertions.assertEquals(1, emptyHistoryManager.getAllSubTasks().size());
        Assertions.assertEquals(4, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldHaveSameContent() throws IOException, InterruptedException {
        add2TestEpics();
        add5TestSubTasks();
        add2TestTasks();
        manager.getEpic(0);
        manager.getEpic(1);
        manager.getSubTask(2);
        manager.getSubTask(3);
        manager.getSubTask(4);
        manager.getSubTask(5);
        manager.getSubTask(6);
        manager.getTask(7);
        manager.getTask(8);
        HTTPTaskManager standardManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");
        Assertions.assertEquals(2, standardManager.getAllEpics().size());
        Assertions.assertEquals(2, standardManager.getAllTasks().size());
        Assertions.assertEquals(5, standardManager.getAllSubTasks().size());
        Assertions.assertEquals(9, standardManager.getHistory().size());
        Assertions.assertEquals(9, standardManager.getId());
        Assertions.assertTrue(standardManager.getAllTasks().contains(task_1));
        Assertions.assertTrue(standardManager.getAllTasks().contains(task_2));
        Assertions.assertEquals(5, standardManager.getEpic(0).getSubTasks().size());
        Assertions.assertEquals(0, standardManager.getEpic(1).getSubTasks().size());
        Assertions.assertTrue(standardManager.getEpic(0).getSubTasks().contains(subTask_5));
    }
}