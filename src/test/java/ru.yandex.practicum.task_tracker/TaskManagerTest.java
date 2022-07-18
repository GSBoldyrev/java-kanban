package test.java.ru.yandex.practicum.task_tracker;

import main.java.ru.yandex.practicum.task_tracker.task_managers.TaskManager;
import main.java.ru.yandex.practicum.task_tracker.tasks.Epic;
import main.java.ru.yandex.practicum.task_tracker.tasks.SubTask;
import main.java.ru.yandex.practicum.task_tracker.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static main.java.ru.yandex.practicum.task_tracker.tasks.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
    protected Task task_1;
    protected Task task_2;
    protected Epic epic_1;
    protected Epic epic_2;
    protected SubTask subTask_1;
    protected SubTask subTask_2;
    protected SubTask subTask_3;
    protected SubTask subTask_4;
    protected SubTask subTask_5;

    public abstract T createManager();

    @BeforeEach
    public void updateManager() {
        manager = createManager();
        task_1 = new Task("T1", "TD1", NEW, 15, null);
        task_2 = new Task("T2", "TD2", NEW, 15, null);
        epic_1 = new Epic("E1", "ED1");
        epic_2 = new Epic("E2", "ED2");
        subTask_1 = new SubTask("S1", "SD1", NEW, 0, 15, null);
        subTask_2 = new SubTask("S2", "SD2", NEW, 0, 15, null);
        subTask_3 = new SubTask("S3", "SD3", NEW, 0, 15, null);
        subTask_4 = new SubTask("S4", "SD4", NEW, 0, 15, null);
        subTask_5 = new SubTask("S5", "SD5", NEW, 0, 15, null);
    }

    public void add2TestTasks() {
        manager.addTask(task_1);
        manager.addTask(task_2);
    }

    public void add2TestEpics() {
        manager.addEpic(epic_1);
        manager.addEpic(epic_2);
    }

    public void add5TestSubTasks() {
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        manager.addSubTask(subTask_3);
        manager.addSubTask(subTask_4);
        manager.addSubTask(subTask_5);
    }

    @Test
    public void deleteAllTasks() {
        // Arrange
        add2TestTasks();

        // Act
        manager.deleteAllTasks();

        // Assert
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void deleteAllSubTasks() {
        // Arrange
        add2TestEpics();
        subTask_3.setEpicId(1);
        subTask_4.setEpicId(1);
        subTask_5.setEpicId(1);
        add5TestSubTasks();

        // Act
        manager.deleteAllSubTasks();

        // Assert
        assertEquals(0, manager.getAllSubTasks().size());
        assertEquals(0, epic_1.getSubTasks().size());
        assertEquals(0, epic_2.getSubTasks().size());
    }

    @Test
    public void deleteAllEpics() {
        // Arrange
        add2TestEpics();
        subTask_3.setEpicId(1);
        subTask_4.setEpicId(1);
        subTask_5.setEpicId(1);
        add5TestSubTasks();

        // Act
        manager.deleteAllEpics();

        // Assert
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    public void shouldAddTaskWithNormalConditions() {
        // Act
        int id = manager.addTask(task_1);
        Task savedTask = manager.getTask(id);

        // Assert
        assertNotNull(savedTask, "Задача не сохранилась");
        assertEquals(task_1, savedTask);
    }

    @Test
    public void shouldNotAddNullTask() {
        // Act
        int id = manager.addTask(null);

        // Assert
        assertEquals(-2, id);
    }

    @Test
    public void shouldNotAddTaskWithTimeIntersection() {
        // Arrange
        task_1.setDuration(60);
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setDuration(60);
        task_2.setStartTime(LocalDateTime.parse("12:30 - 01.01.2022", FORMATTER));

        // Act
        int id_1 = manager.addTask(task_1);
        int id_2 = manager.addTask(task_2);

        // Assert
        assertEquals(0, id_1);
        assertEquals(-2, id_2);
    }

    @Test
    public void shouldAddEpicWithNormalConditions() {
        // Act
        int id = manager.addEpic(epic_1);
        Epic savedEpic = manager.getEpic(id);

        // Assert
        assertNotNull(savedEpic, "Задача не сохранилась");
        assertEquals(epic_1, savedEpic);
        assertEquals(NEW, epic_1.getStatus());
        assertEquals(0, epic_1.getSubTasks().size());
    }

    @Test
    public void shouldNotAddNullEpic() {
        // Act
        int id = manager.addEpic(null);

        // Assert
        assertEquals(-2, id);
    }

    @Test
    public void shouldAddSubTaskWithNormalConditions() {
        // Arrange
        manager.addEpic(epic_1);

        // Act
        int id = manager.addSubTask(subTask_1);
        SubTask savedSubTask = manager.getSubTask(id);

        // Assert
        assertNotNull(savedSubTask, "Задача не сохранилась");
        assertEquals(savedSubTask, subTask_1);
        assertEquals(1, manager.getAllSubTasks().size());
        assertEquals(1, epic_1.getSubTasks().size());
    }

    @Test
    public void shouldNotAddNullSubTask() {
        // Act
        int id = manager.addSubTask(null);

        // Assert
        assertEquals(-2, id);
    }

    @Test
    public void shouldNotAddSubTaskWithTimeIntersection() {
        // Arrange
        manager.addEpic(epic_1);
        subTask_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addSubTask(subTask_1);
        subTask_2.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));

        // Act
        int id_2 = manager.addSubTask(subTask_2);

        // Assert
        assertEquals(1, id_1);
        assertEquals(-2, id_2);
    }

    @Test
    public void shouldNotAddSubtaskWithoutEpic() {
        // Act
        int id = manager.addSubTask(subTask_1);

        // Assert
        assertEquals(-2, id);
    }

    @Test
    public void shouldUpdateTaskWithNormalConditions() {
        // Act
        int id = manager.addTask(task_1);
        task_2.setId(id);

        // Arrange
        int updatedId = manager.updateTask(task_2);
        Task savedTask = manager.getTask(updatedId);

        // Assert
        assertNotNull(savedTask, "Задача не сохранилась");
        assertEquals(savedTask, task_2);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals("TD2", manager.getTask(id).getDescription());
        assertEquals(id, updatedId);
    }

    @Test
    public void shouldUpdateTaskWithSameStartTime() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id = manager.addTask(task_1);
        task_2.setId(id);
        task_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));

        // Act
        int updatedId = manager.updateTask(task_2);

        // Assert
        assertEquals(0, updatedId);
    }

    @Test
    public void shouldNotUpdateTaskToNull() {
        // Act
        int updated_id = manager.updateTask(null);

        // Assert
        assertEquals(-2, updated_id);
    }

    @Test
    public void shouldNotUpdateTaskWithWrongId() {
        // Arrange
        int id = manager.addTask(task_1);
        task_2.setId(id + 1);

        // Act
        int updateId = manager.updateTask(task_2);

        // Assert
        assertEquals(-3, updateId);
    }

    @Test
    public void shouldNotUpdateTaskWithTimeIntersection() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        add2TestTasks();
        Task task_3 = new Task("T3", "TD3", NEW,
                60, LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_3.setId(task_1.getId());

        // Act
        int updatedId = manager.updateTask(task_3);

        // Assert
        assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateSubTaskWithNormalConditions() {
        // Arrange
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        subTask_2.setId(id);

        // Act
        int updatedId = manager.updateSubTask(subTask_2);
        SubTask savedSubTask = manager.getSubTask(updatedId);

        // Assert
        assertNotNull(savedSubTask, "Задача не сохранилась");
        assertEquals(savedSubTask, subTask_2);
        assertEquals(1, manager.getAllSubTasks().size());
        assertEquals("S2", manager.getSubTask(id).getName());
    }

    @Test
    public void shouldNotUpdateSubtaskWithoutEpic() {
        // Arrange
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        subTask_2.setEpicId(1);
        subTask_2.setId(id);

        // Act
        int updatedId = manager.updateSubTask(subTask_2);

        // Assert
        assertEquals(1, manager.getAllSubTasks().size());
        assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateSubTaskWithSameStartTime() {
        // Arrange
        manager.addEpic(epic_1);
        subTask_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id = manager.addSubTask(subTask_1);
        subTask_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        subTask_2.setId(id);

        // Act
        int updatedId = manager.updateSubTask(subTask_2);

        // Assert
        assertEquals(1, manager.getAllSubTasks().size());
        assertEquals(id, updatedId);
    }

    @Test
    public void shouldNotUpdateNullSubTask() {
        // Act
        int updatedId = manager.updateSubTask(null);

        // Assert
        assertEquals(-2, updatedId);
    }

    @Test
    public void shouldNotUpdateSubTaskWithWrongId() {
        // Arrange
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        subTask_2.setId(id + 1);

        // Act
        int updatedId = manager.updateSubTask(subTask_2);

        // Assert
        assertEquals(1, manager.getAllSubTasks().size());
        assertEquals(-3, updatedId);
    }

    @Test
    public void shouldNotUpdateSubTaskWithTimeIntersection() {
        // Arrange
        manager.addEpic(epic_1);
        subTask_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        subTask_2.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        subTask_3.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        subTask_3.setId(id_1);

        // Act
        int updatedId = manager.updateSubTask(subTask_3);

        // Assert
        assertEquals(2, manager.getAllSubTasks().size());
        assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateEpicWithNormalConditions() {
        // Arrange
        manager.addEpic(epic_1);
        subTask_1.setStatus(DONE);
        subTask_2.setStatus(DONE);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        epic_2.setId(0);

        // Act
        int updatedId = manager.updateEpic(epic_2);
        Epic savedEpic = manager.getEpic(0);

        // Assert
        assertNotNull(savedEpic, "Задача не сохранилась");
        assertEquals(NEW, manager.getEpic(updatedId).getStatus());
        assertEquals(0, manager.getEpic(updatedId).getSubTasks().size());
        assertEquals("ED2", manager.getEpic(updatedId).getDescription());
        assertEquals(0, manager.getAllSubTasks().size());
        assertEquals(0, updatedId);
    }

    @Test
    public void shouldNotUpdateNullEpic() {
        // Act
        int updatedId = manager.updateEpic(null);

        // Assert
        assertEquals(-2, updatedId);
    }

    @Test
    public void shouldNotUpdateEpicWithWrongId() {
        // Arrange
        int epicId = manager.addEpic(epic_1);
        epic_2.setId(epicId + 1);

        // Act
        int updatedId = manager.updateEpic(epic_2);

        // Assert
        assertEquals(-3, updatedId);
    }

    @Test
    public void shouldRemoveTaskWIthNormalConditions() {
        // Arrange
        int id = manager.addTask(task_1);

        // Act
        Task removedTask = manager.removeTask(id);

        // Assert
        assertEquals(0, manager.getAllTasks().size());
        assertNull(manager.getTask(id));
        assertEquals(task_1, removedTask);
    }

    @Test
    public void shouldNotRemoveTaskWithWrongId() {
        // Act
        Task removedTask = manager.removeTask(1);

        // Assert
        assertNull(removedTask);
    }

    @Test
    public void shouldRemoveSubTaskWithNormalConditions() {
        // Arrange
        manager.addEpic(epic_1);
        subTask_1.setStatus(DONE);
        int id = manager.addSubTask(subTask_1);

        // Act
        SubTask removedSubTask = (SubTask) manager.removeSubTask(id);

        // Assert
        assertEquals(NEW, manager.getEpic(0).getStatus());
        assertEquals(0, manager.getAllSubTasks().size());
        assertEquals(0, manager.getEpic(0).getSubTasks().size());
        assertNull(manager.getSubTask(id));
        assertEquals(subTask_1, removedSubTask);
    }

    @Test
    public void shouldNotRemoveSubTaskWithWrongId() {
        // Arrange
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);

        // Act
        SubTask removedSubTask = (SubTask) manager.removeSubTask(id + 1);

        // Assert
        assertNull(removedSubTask);
    }

    @Test
    public void shouldRemoveEpicWithNormalConditions() {
        // Arrange
        manager.addEpic(epic_1);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);

        // Act
        Epic removedEpic = (Epic) manager.removeEpic(0);

        // Assert
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubTasks().size());
        assertNull(manager.getEpic(0));
        assertEquals(epic_1, removedEpic);
    }

    @Test
    public void shouldNotRemoveEpicWithWrongId() {
        // Arrange
        int epicId = manager.addEpic(epic_1);

        // Act
        Epic removedEpic = (Epic) manager.removeEpic(epicId + 1);

        // Assert
        assertEquals(1, manager.getAllEpics().size());
        assertNull(removedEpic);
    }

    @Test
    public void getAllTasks() {
        // Act
        add2TestTasks();

        // Assert
        assertEquals(2, manager.getAllTasks().size());
        assertTrue(manager.getAllTasks().contains(task_1));
        assertTrue(manager.getAllTasks().contains(task_2));
    }

    @Test
    public void getAllSubTasks() {
        // Arrange
        manager.addEpic(epic_1);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);

        // Act
        // Assert
        assertEquals(2, manager.getAllSubTasks().size());
        assertTrue(manager.getAllSubTasks().contains(subTask_1));
        assertTrue(manager.getAllSubTasks().contains(subTask_2));
    }

    @Test
    public void getAllEpics() {
        // Arrange
        add2TestEpics();

        // Act
        // Assert
        assertEquals(2, manager.getAllEpics().size());
        assertTrue(manager.getAllEpics().contains(epic_1));
        assertTrue(manager.getAllEpics().contains(epic_2));
    }

    @Test
    public void getTask() {
        // Arrange
        int id_1 = manager.addTask(task_1);

        // Act
        // Assert
        assertEquals(task_1, manager.getTask(id_1));
        assertNull(manager.getTask(45));
    }

    @Test
    public void getSubTask() {
        // Arrange
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);

        // Act
        // Assert
        assertEquals(subTask_1, manager.getSubTask(id));
        assertNull(manager.getSubTask(45));
    }

    @Test
    public void getEpic() {
        // Arrange
        int id = manager.addEpic(epic_1);

        // Act
        // Assert
        assertEquals(epic_1, manager.getEpic(id));
        assertNull(manager.getTask(45));
    }

    @Test
    public void getSubTasksFromEpic() {
        // Arrange
        manager.addEpic(epic_1);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);

        // Act
        List<SubTask> fromEpic = manager.getSubTasksFromEpic(0);

        // Assert
        assertTrue(manager.getAllSubTasks().containsAll(fromEpic));
        assertTrue(fromEpic.contains(subTask_1));
        assertEquals(2, fromEpic.size());
    }

    @Test
    public void shouldNotGetSubTasksFromEpicWithWrongId() {
        // Arrange
        int epicId = manager.addEpic(epic_1);

        // Act
        List<SubTask> fromEpic = manager.getSubTasksFromEpic(epicId + 1);

        // Assert
        assertNull(fromEpic);
    }

    @Test
    public void getHistory() {
        // Arrange
        int epicId = manager.addEpic(epic_1);
        int id = manager.addTask(task_1);
        manager.getTask(id);
        Epic secondCall = manager.getEpic(epicId);

        // Act
        // Assert
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(secondCall));
    }

    @Test
    public void getPrioritizedTasks() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("10:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        add2TestEpics();
        add2TestTasks();
        subTask_1.setStartTime(LocalDateTime.parse("10:30 - 01.01.2022", FORMATTER));
        subTask_2.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        subTask_3.setStartTime(LocalDateTime.parse("11:30 - 01.01.2022", FORMATTER));
        subTask_4.setStartTime(LocalDateTime.parse("12:30 - 01.01.2022", FORMATTER));
        subTask_5.setStartTime(LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        add5TestSubTasks();

        // Act
        Set<Task> prioritized = manager.getPrioritizedTasks();

        // Assert
        assertTrue(prioritized.containsAll(manager.getAllTasks()));
        assertTrue(prioritized.containsAll(manager.getAllSubTasks()));
        assertEquals(7, prioritized.size());
        List<Task> tester = new ArrayList<>();
        tester.add(task_1);
        tester.add(subTask_1);
        tester.add(subTask_5);
        tester.add(subTask_3);
        tester.add(task_2);
        tester.add(subTask_4);
        tester.add(subTask_2);
        int i = 0;
        for (Task task : prioritized) {
            assertEquals(task, tester.get(i));
            i++;
        }
    }

    @Test
    public void getPrioritizedTasksWithSomeNullTimes() {
        // Arrange
        task_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        add2TestEpics();
        add2TestTasks();
        subTask_1.setStartTime(LocalDateTime.parse("10:30 - 01.01.2022", FORMATTER));
        subTask_3.setStartTime(LocalDateTime.parse("11:30 - 01.01.2022", FORMATTER));
        subTask_5.setStartTime(LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        add5TestSubTasks();

        // Act
        Set<Task> prioritized = manager.getPrioritizedTasks();

        // Assert
        assertTrue(prioritized.containsAll(manager.getAllTasks()));
        assertTrue(prioritized.containsAll(manager.getAllSubTasks()));
        assertEquals(7, prioritized.size());
        List<Task> tester = new ArrayList<>();
        tester.add(subTask_1);
        tester.add(subTask_5);
        tester.add(subTask_3);
        tester.add(task_2);
        tester.add(task_1);
        tester.add(subTask_2);
        tester.add(subTask_4);
        int i = 0;
        for (Task task : prioritized) {
            assertEquals(task, tester.get(i));
            i++;
        }
    }
}