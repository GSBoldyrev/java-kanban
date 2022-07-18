package test.java.ru.yandex.practicum.task_tracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.ru.yandex.practicum.task_tracker.servers.KVServer;
import main.java.ru.yandex.practicum.task_tracker.task_managers.HTTPTaskManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
        // Act
        HTTPTaskManager emptyManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");

        // Assert
        assertEquals(0, emptyManager.getAllTasks().size());
        assertEquals(0, emptyManager.getAllSubTasks().size());
        assertEquals(0, emptyManager.getAllEpics().size());
        assertEquals(0, emptyManager.getHistory().size());
        assertEquals(0, emptyManager.getId());
        assertNotNull(emptyManager);
    }

    @Test
    public void shouldLoadWithOneEmptyEpic() throws IOException, InterruptedException {
        // Arrange
        add2TestEpics();
        add2TestTasks();
        manager.getEpic(0);
        manager.getEpic(1);

        // Act
        HTTPTaskManager oneEpicManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");

        // Assert
        assertEquals(2, oneEpicManager.getAllEpics().size());
        assertEquals(2, oneEpicManager.getAllTasks().size());
        assertEquals(2, oneEpicManager.getHistory().size());
        assertTrue(oneEpicManager.getHistory().containsAll(oneEpicManager.getAllEpics()));
        assertEquals(0, oneEpicManager.getEpic(0).getSubTasks().size());
    }

    @Test
    public void shouldLoadWithEmptyHistory() throws IOException, InterruptedException {
        // Arrange
        add2TestEpics();
        add5TestSubTasks();
        add2TestTasks();

        // Act
        HTTPTaskManager emptyHistoryManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");

        // Assert
        assertEquals(2, emptyHistoryManager.getAllEpics().size());
        assertEquals(2, emptyHistoryManager.getAllTasks().size());
        assertEquals(5, emptyHistoryManager.getAllSubTasks().size());
        assertEquals(0, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldLoadWithoutTimings() throws IOException, InterruptedException {
        // Arrange
        add2TestEpics();
        manager.addSubTask(subTask_1);
        add2TestTasks();
        manager.getSubTask(2);
        manager.getEpic(0);
        manager.getTask(3);
        manager.getTask(4);

        // Act
        HTTPTaskManager emptyHistoryManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");

        // Assert
        assertEquals(2, emptyHistoryManager.getAllEpics().size());
        assertEquals(2, emptyHistoryManager.getAllTasks().size());
        assertEquals(1, emptyHistoryManager.getAllSubTasks().size());
        assertEquals(4, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldHaveSameContent() throws IOException, InterruptedException {
        // Arrange
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

        // Act
        HTTPTaskManager standardManager = HTTPTaskManager.loadFromServer("http://localhost:8078", "key");

        // Assert
        assertEquals(2, standardManager.getAllEpics().size());
        assertEquals(2, standardManager.getAllTasks().size());
        assertEquals(5, standardManager.getAllSubTasks().size());
        assertEquals(9, standardManager.getHistory().size());
        assertEquals(9, standardManager.getId());
        assertTrue(standardManager.getAllTasks().contains(task_1));
        assertTrue(standardManager.getAllTasks().contains(task_2));
        assertEquals(5, standardManager.getEpic(0).getSubTasks().size());
        assertEquals(0, standardManager.getEpic(1).getSubTasks().size());
        assertTrue(standardManager.getEpic(0).getSubTasks().contains(subTask_5));
    }
}