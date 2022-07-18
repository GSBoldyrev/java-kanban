package test.java.ru.yandex.practicum.task_tracker;

import main.java.ru.yandex.practicum.task_tracker.history_managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.ru.yandex.practicum.task_tracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager manager;
    private Task task_1;
    private Task task_2;
    private Task task_3;
    private Task task_4;

    public InMemoryHistoryManager createManager() {
        return new InMemoryHistoryManager();
    }

    @BeforeEach
    public void updateManager() {
        manager = createManager();
        task_1 = new Task("T1", "D1");
        task_1.setId(1);
        task_2 = new Task("T2", "D2");
        task_2.setId(2);
        task_3 = new Task("T3", "D3");
        task_3.setId(3);
        task_4 = new Task("T4", "D4");
        task_4.setId(4);
    }

    @Test
    public void shouldAddOneTask() {
        // Act
        manager.add(task_1);

        // Assert
        assertEquals(1, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_1));
    }

    @Test
    public void shouldAdd2DifferentTasks() {
        // Act
        manager.add(task_1);
        manager.add(task_2);

        // Assert
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_1));
        assertTrue(manager.getHistory().contains(task_2));
    }

    @Test
    public void shouldAdd2TasksWithSameIdAndKeepLastOne() {
        // Arrange
        task_2.setId(1);

        // Act
        manager.add(task_1);
        manager.add(task_2);

        // Assert
        assertEquals(1, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_2));
    }

    @Test
    public void shouldAdd2TasksWithSameIdAndOneAnother() {
        // Arrange
        task_2.setId(1);
        task_3.setId(2);

        // Act
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);

        // Assert
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_2));
        assertTrue(manager.getHistory().contains(task_3));
    }

    @Test
    public void shouldRemoveAloneTask() {
        // Arrange
        manager.add(task_1);

        // Act
        manager.remove(1);

        // Assert
        assertEquals(0, manager.getHistory().size());
        assertFalse(manager.getHistory().contains(task_1));
    }

    @Test
    public void shouldRemoveFirstTask() {
        // Arrange
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);

        // Act
        manager.remove(1);

        // Assert
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_2));
        assertTrue(manager.getHistory().contains(task_3));
    }

    @Test
    public void shouldRemoveMiddleTask() {
        // Arrange
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);

        // Act
        manager.remove(2);

        // Assert
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_1));
        assertTrue(manager.getHistory().contains(task_3));
    }

    @Test
    public void shouldRemoveLastTask() {
        // Arrange
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);

        // Act
        manager.remove(3);

        // Assert
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task_1));
        assertTrue(manager.getHistory().contains(task_2));
    }

    @Test
    public void getEmptyHistory() {
        // Act
        List<Task> emptyList = manager.getHistory();

        // Assert
        assertEquals(0, emptyList.size());
    }

    @Test
    public void shouldGetHistoryFrom1To4() {
        // Arrange
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);
        manager.add(task_4);

        // Act
        List<Task> list = manager.getHistory();

        // Assert
        assertEquals(4, list.size());
        assertEquals(task_1, list.get(0));
        assertEquals(task_2, list.get(1));
        assertEquals(task_3, list.get(2));
        assertEquals(task_4, list.get(3));
    }

    @Test
    public void shouldGetHistoryFrom4To1() {
        // Arrange
        manager.add(task_4);
        manager.add(task_3);
        manager.add(task_2);
        manager.add(task_1);

        // Act
        List<Task> list = manager.getHistory();

        // Assert
        assertEquals(4, list.size());
        assertEquals(task_4, list.get(0));
        assertEquals(task_3, list.get(1));
        assertEquals(task_2, list.get(2));
        assertEquals(task_1, list.get(3));
    }

    @Test
    public void shouldGetHistoryFrom2To1() {
        // Arrange
        task_1.setId(4);
        task_2.setId(3);
        manager.add(task_4);
        manager.add(task_3);
        manager.add(task_2);
        manager.add(task_1);

        // Act
        List<Task> list = manager.getHistory();

        // Assert
        assertEquals(2, list.size());
        assertEquals(task_2, list.get(0));
        assertEquals(task_1, list.get(1));
    }
}