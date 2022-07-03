package tests;

import history_managers.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager manager;

    public InMemoryHistoryManager createManager() {
        return new InMemoryHistoryManager();
    }

    @BeforeEach
    public void updateManager() {
        manager = createManager();
    }

    @Test
    public void shouldAddOneTask() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        manager.add(task_1);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
    }

    @Test
    public void shouldAdd2DifferentTasks() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(2);
        manager.add(task_1);
        manager.add(task_2);
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
        Assertions.assertTrue(manager.getHistory().contains(task_2));
    }

    @Test
    public void shouldAdd2TasksWithSameIdAndKeepLastOne() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(1);
        manager.add(task_1);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
        manager.add(task_2);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_2));
    }

    @Test
    public void shouldAdd2TasksWithSameIdAndOneAnother() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(1);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(2);
        manager.add(task_1);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
        manager.add(task_2);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_2));
        manager.add(task_3);
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_2));
        Assertions.assertTrue(manager.getHistory().contains(task_3));
    }

    @Test
    public void shouldRemoveAloneTask() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        manager.add(task_1);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
        manager.remove(1);
        Assertions.assertEquals(0, manager.getHistory().size());
        Assertions.assertFalse(manager.getHistory().contains(task_1));
    }

    @Test
    public void shouldRemoveFirstTask() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(2);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(3);
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);
        Assertions.assertEquals(3, manager.getHistory().size());
        manager.remove(1);
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_2));
        Assertions.assertTrue(manager.getHistory().contains(task_3));
    }

    @Test
    public void shouldRemoveMiddleTask() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(2);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(3);
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);
        Assertions.assertEquals(3, manager.getHistory().size());
        manager.remove(2);
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
        Assertions.assertTrue(manager.getHistory().contains(task_3));
    }

    @Test
    public void shouldRemoveLastTask() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(2);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(3);
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);
        Assertions.assertEquals(3, manager.getHistory().size());
        manager.remove(3);
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(task_1));
        Assertions.assertTrue(manager.getHistory().contains(task_2));
    }

    @Test
    public void getEmptyHistory() {
        List<Task> emptyList = manager.getHistory();
        Assertions.assertEquals(0, emptyList.size());
    }

    @Test
    public void shouldGetHistoryFrom1To4() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(2);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(3);
        Task task_4 = new Task("T4", "D4");
        task_4.setId(4);
        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);
        manager.add(task_4);
        List<Task> list = manager.getHistory();
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals(task_1, list.get(0));
        Assertions.assertEquals(task_2, list.get(1));
        Assertions.assertEquals(task_3, list.get(2));
        Assertions.assertEquals(task_4, list.get(3));
    }

    @Test
    public void shouldGetHistoryFrom4To1() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(1);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(2);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(3);
        Task task_4 = new Task("T4", "D4");
        task_4.setId(4);
        manager.add(task_4);
        manager.add(task_3);
        manager.add(task_2);
        manager.add(task_1);
        List<Task> list = manager.getHistory();
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals(task_4, list.get(0));
        Assertions.assertEquals(task_3, list.get(1));
        Assertions.assertEquals(task_2, list.get(2));
        Assertions.assertEquals(task_1, list.get(3));
    }

    @Test
    public void shouldGetHistoryFrom2To1() {
        Task task_1 = new Task("T1", "D1");
        task_1.setId(4);
        Task task_2 = new Task("T2", "D2");
        task_2.setId(3);
        Task task_3 = new Task("T3", "D3");
        task_3.setId(3);
        Task task_4 = new Task("T4", "D4");
        task_4.setId(4);
        manager.add(task_4);
        manager.add(task_3);
        manager.add(task_2);
        manager.add(task_1);
        List<Task> list = manager.getHistory();
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(task_2, list.get(0));
        Assertions.assertEquals(task_1, list.get(1));
    }
}