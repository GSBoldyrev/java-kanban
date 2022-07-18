package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_managers.TaskManager;
import tasks.Epic;
import tasks.TaskStatus;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        task_1 = new Task("T1", "TD1", TaskStatus.NEW, 15, null);
        task_2 = new Task("T2", "TD2", TaskStatus.NEW, 15, null);
        epic_1 = new Epic("E1", "ED1");
        epic_2 = new Epic("E2", "ED2");
        subTask_1 = new SubTask("S1", "SD1", TaskStatus.NEW, 0, 15, null);
        subTask_2 = new SubTask("S2", "SD2", TaskStatus.NEW, 0, 15, null);
        subTask_3 = new SubTask("S3", "SD3", TaskStatus.NEW, 0, 15, null);
        subTask_4 = new SubTask("S4", "SD4", TaskStatus.NEW, 0, 15, null);
        subTask_5 = new SubTask("S5", "SD5", TaskStatus.NEW, 0, 15, null);
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
        add2TestTasks();
        Assertions.assertEquals(2, manager.getAllTasks().size());
        manager.deleteAllTasks();
        Assertions.assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void deleteAllSubTasks() {
        add2TestEpics();
        Assertions.assertEquals(2, manager.getAllEpics().size());
        subTask_3.setEpicId(1);
        subTask_4.setEpicId(1);
        subTask_5.setEpicId(1);
        add5TestSubTasks();
        Assertions.assertEquals(5, manager.getAllSubTasks().size());
        manager.deleteAllSubTasks();
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertEquals(0, epic_1.getSubTasks().size());
        Assertions.assertEquals(0, epic_2.getSubTasks().size());
    }

    @Test
    public void deleteAllEpics() {
        add2TestEpics();
        Assertions.assertEquals(2, manager.getAllEpics().size());
        subTask_3.setEpicId(1);
        subTask_4.setEpicId(1);
        subTask_5.setEpicId(1);
        add5TestSubTasks();
        Assertions.assertEquals(5, manager.getAllSubTasks().size());
        manager.deleteAllEpics();
        Assertions.assertEquals(0, manager.getAllEpics().size());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    public void shouldAddTaskWithNormalConditions() {
        int id = manager.addTask(task_1);
        Task savedTask = manager.getTask(id);
        Assertions.assertNotNull(savedTask, "Задача не сохранилась");
        Assertions.assertEquals(task_1, savedTask);
    }

    @Test
    public void shouldNotAddNullTask() {
        int id = manager.addTask(null);
        Assertions.assertEquals(-2, id);
    }

    @Test
    public void shouldNotAddTaskWithTimeIntersection() {
        task_1.setDuration(60);
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setDuration(60);
        task_2.setStartTime(LocalDateTime.parse("12:30 - 01.01.2022", FORMATTER));
        int id_1 = manager.addTask(task_1);
        int id_2 = manager.addTask(task_2);
        Assertions.assertEquals(0, id_1);
        Assertions.assertEquals(-2, id_2);
    }

    @Test
    public void shouldAddEpicWithNormalConditions() {
        int id = manager.addEpic(epic_1);
        Epic savedEpic = manager.getEpic(id);
        Assertions.assertNotNull(savedEpic, "Задача не сохранилась");
        Assertions.assertEquals(epic_1, savedEpic);
        Assertions.assertEquals(TaskStatus.NEW, epic_1.getStatus());
        Assertions.assertEquals(0, epic_1.getSubTasks().size());
    }

    @Test
    public void shouldNotAddNullEpic() {
        int id = manager.addEpic(null);
        Assertions.assertEquals(-2, id);
    }

    @Test
    public void shouldAddSubTaskWithNormalConditions() {
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        SubTask savedSubTask = manager.getSubTask(id);
        Assertions.assertNotNull(savedSubTask, "Задача не сохранилась");
        Assertions.assertEquals(savedSubTask, subTask_1);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(1, epic_1.getSubTasks().size());
    }

    @Test
    public void shouldNotAddNullSubTask() {
        int id = manager.addSubTask(null);
        Assertions.assertEquals(-2, id);
    }

    @Test
    public void shouldNotAddSubTaskWithTimeIntersection() {
        manager.addEpic(epic_1);
        subTask_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addSubTask(subTask_1);
        subTask_2.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        int id_2 = manager.addSubTask(subTask_2);
        Assertions.assertEquals(1, id_1);
        Assertions.assertEquals(-2, id_2);
    }

    @Test
    public void shouldNotAddSubtaskWithoutEpic() {
        int id = manager.addSubTask(subTask_1);
        Assertions.assertEquals(-2, id);
    }

    @Test
    public void shouldUpdateTaskWithNormalConditions() {
        int id = manager.addTask(task_1);
        task_2.setId(id);
        int updatedId = manager.updateTask(task_2);
        Task savedTask = manager.getTask(updatedId);
        Assertions.assertNotNull(savedTask, "Задача не сохранилась");
        Assertions.assertEquals(savedTask, task_2);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Assertions.assertEquals("TD2", manager.getTask(id).getDescription());
        Assertions.assertEquals(id, updatedId);
    }

    @Test
    public void shouldUpdateTaskWithSameStartTime() {
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id = manager.addTask(task_1);
        task_2.setId(id);
        task_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int updatedId = manager.updateTask(task_2);
        Assertions.assertEquals(0, updatedId);
    }

    @Test
    public void shouldNotUpdateTaskToNull() {
        int updated_id = manager.updateTask(null);
        Assertions.assertEquals(-2, updated_id);
    }

    @Test
    public void shouldNotUpdateTaskWithWrongId() {
        int id = manager.addTask(task_1);
        task_2.setId(id + 1);
        int updateId = manager.updateTask(task_2);
        Assertions.assertEquals(-3, updateId);
    }

    @Test
    public void shouldNotUpdateTaskWithTimeIntersection() {
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        add2TestTasks();
        Task task_3 = new Task("T3", "TD3", TaskStatus.NEW,
                60, LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_3.setId(task_1.getId());
        int updatedId = manager.updateTask(task_3);
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateSubTaskWithNormalConditions() {
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        subTask_2.setId(id);
        int updatedId = manager.updateSubTask(subTask_2);
        SubTask savedSubTask = manager.getSubTask(updatedId);
        Assertions.assertNotNull(savedSubTask, "Задача не сохранилась");
        Assertions.assertEquals(savedSubTask, subTask_2);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals("S2", manager.getSubTask(id).getName());
    }

    @Test
    public void shouldNotUpdateSubtaskWithoutEpic() {
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        subTask_2.setEpicId(1);
        subTask_2.setId(id);
        int updatedId = manager.updateSubTask(subTask_2);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateSubTaskWithSameStartTime() {
        manager.addEpic(epic_1);
        subTask_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id = manager.addSubTask(subTask_1);
        subTask_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        subTask_2.setId(id);
        int updatedId = manager.updateSubTask(subTask_2);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(id, updatedId);
    }

    @Test
    public void shouldNotUpdateNullSubTask() {
        int updatedId = manager.updateSubTask(null);
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldNotUpdateSubTaskWithWrongId() {
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        subTask_2.setId(id + 1);
        int updatedId = manager.updateSubTask(subTask_2);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(-3, updatedId);
    }

    @Test
    public void shouldNotUpdateSubTaskWithTimeIntersection() {
        manager.addEpic(epic_1);
        subTask_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        subTask_2.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        subTask_3.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        subTask_3.setId(id_1);
        int updatedId = manager.updateSubTask(subTask_3);
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateEpicWithNormalConditions() {
        manager.addEpic(epic_1);
        subTask_1.setStatus(TaskStatus.DONE);
        subTask_2.setStatus(TaskStatus.DONE);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        Assertions.assertEquals(TaskStatus.DONE, manager.getEpic(0).getStatus());
        Assertions.assertEquals(2, manager.getEpic(0).getSubTasks().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        epic_2.setId(0);
        int updatedId = manager.updateEpic(epic_2);
        Epic savedEpic = manager.getEpic(0);
        Assertions.assertNotNull(savedEpic, "Задача не сохранилась");
        Assertions.assertEquals(TaskStatus.NEW, manager.getEpic(updatedId).getStatus());
        Assertions.assertEquals(0, manager.getEpic(updatedId).getSubTasks().size());
        Assertions.assertEquals("ED2", manager.getEpic(updatedId).getDescription());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertEquals(0, updatedId);
    }

    @Test
    public void shouldNotUpdateNullEpic() {
        int updatedId = manager.updateEpic(null);
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldNotUpdateEpicWithWrongId() {
        int epicId = manager.addEpic(epic_1);
        epic_2.setId(epicId + 1);
        int updatedId = manager.updateEpic(epic_2);
        Assertions.assertEquals(-3, updatedId);
    }

    @Test
    public void shouldRemoveTaskWIthNormalConditions() {
        int id = manager.addTask(task_1);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Task removedTask = manager.removeTask(id);
        Assertions.assertEquals(0, manager.getAllTasks().size());
        Assertions.assertNull(manager.getTask(id));
        Assertions.assertEquals(task_1, removedTask);
    }

    @Test
    public void shouldNotRemoveTaskWithWrongId() {
        Task removedTask = manager.removeTask(1);
        Assertions.assertNull(removedTask);
    }

    @Test
    public void shouldRemoveSubTaskWithNormalConditions() {
        manager.addEpic(epic_1);
        subTask_1.setStatus(TaskStatus.DONE);
        int id = manager.addSubTask(subTask_1);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(1, manager.getEpic(0).getSubTasks().size());
        SubTask removedSubTask = (SubTask) manager.removeSubTask(id);
        Assertions.assertEquals(TaskStatus.NEW, manager.getEpic(0).getStatus());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertEquals(0, manager.getEpic(0).getSubTasks().size());
        Assertions.assertNull(manager.getSubTask(id));
        Assertions.assertEquals(subTask_1, removedSubTask);
    }

    @Test
    public void shouldNotRemoveSubTaskWithWrongId() {
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        SubTask removedSubTask = (SubTask) manager.removeSubTask(id + 1);
        Assertions.assertNull(removedSubTask);
    }

    @Test
    public void shouldRemoveEpicWithNormalConditions() {
        manager.addEpic(epic_1);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Epic removedEpic = (Epic) manager.removeEpic(0);
        Assertions.assertEquals(0, manager.getAllEpics().size());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertNull(manager.getEpic(0));
        Assertions.assertEquals(epic_1, removedEpic);
    }

    @Test
    public void shouldNotRemoveEpicWithWrongId() {
        int epicId = manager.addEpic(epic_1);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Epic removedEpic = (Epic) manager.removeEpic(epicId + 1);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertNull(removedEpic);
    }

    @Test
    public void getAllTasks() {
        Assertions.assertEquals(0, manager.getAllTasks().size());
        add2TestTasks();
        Assertions.assertEquals(2, manager.getAllTasks().size());
        Assertions.assertTrue(manager.getAllTasks().contains(task_1));
        Assertions.assertTrue(manager.getAllTasks().contains(task_2));
    }

    @Test
    public void getAllSubTasks() {
        manager.addEpic(epic_1);
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        manager.addSubTask(subTask_1);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertTrue(manager.getAllSubTasks().contains(subTask_1));
        manager.addSubTask(subTask_2);
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Assertions.assertTrue(manager.getAllSubTasks().contains(subTask_1));
        Assertions.assertTrue(manager.getAllSubTasks().contains(subTask_2));
    }

    @Test
    public void getAllEpics() {
        Assertions.assertEquals(0, manager.getAllEpics().size());
        add2TestEpics();
        Assertions.assertEquals(2, manager.getAllEpics().size());
        Assertions.assertTrue(manager.getAllEpics().contains(epic_1));
        Assertions.assertTrue(manager.getAllEpics().contains(epic_2));
    }

    @Test
    public void getTask() {
        int id_1 = manager.addTask(task_1);
        Assertions.assertEquals(task_1, manager.getTask(id_1));
        Assertions.assertNull(manager.getTask(45));
    }

    @Test
    public void getSubTask() {
        manager.addEpic(epic_1);
        int id = manager.addSubTask(subTask_1);
        Assertions.assertEquals(subTask_1, manager.getSubTask(id));
        Assertions.assertNull(manager.getSubTask(45));
    }

    @Test
    public void getEpic() {
        int id = manager.addEpic(epic_1);
        Assertions.assertEquals(epic_1, manager.getEpic(id));
        Assertions.assertNull(manager.getTask(45));
    }

    @Test
    public void getSubTasksFromEpic() {
        manager.addEpic(epic_1);
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        List<SubTask> fromEpic = manager.getSubTasksFromEpic(0);
        Assertions.assertTrue(manager.getAllSubTasks().containsAll(fromEpic));
        Assertions.assertTrue(fromEpic.contains(subTask_1));
        Assertions.assertEquals(2, fromEpic.size());
    }

    @Test
    public void shouldNotGetSubTasksFromEpicWithWrongId() {
        int epicId = manager.addEpic(epic_1);
        List<SubTask> fromEpic = manager.getSubTasksFromEpic(epicId + 1);
        Assertions.assertNull(fromEpic);
    }

    @Test
    public void getHistory() {
        int epicId = manager.addEpic(epic_1);
        int id = manager.addTask(task_1);
        Assertions.assertEquals(0, manager.getHistory().size());
        Task firstCall = manager.getTask(id);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(firstCall));
        Epic secondCall = manager.getEpic(epicId);
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertTrue(manager.getHistory().contains(secondCall));
    }

    @Test
    public void getPrioritizedTasks() {
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
        Set<Task> prioritized = manager.getPrioritizedTasks();
        System.out.println(prioritized);
        Assertions.assertTrue(prioritized.containsAll(manager.getAllTasks()));
        Assertions.assertTrue(prioritized.containsAll(manager.getAllSubTasks()));
        Assertions.assertEquals(7, prioritized.size());
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
            Assertions.assertEquals(task, tester.get(i));
            i++;
        }
    }

    @Test
    public void getPrioritizedTasksWithSomeNullTimes() {
        task_2.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        add2TestEpics();
        add2TestTasks();
        subTask_1.setStartTime(LocalDateTime.parse("10:30 - 01.01.2022", FORMATTER));
        subTask_3.setStartTime(LocalDateTime.parse("11:30 - 01.01.2022", FORMATTER));
        subTask_5.setStartTime(LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        add5TestSubTasks();
        Set<Task> prioritized = manager.getPrioritizedTasks();
        System.out.println(prioritized);
        Assertions.assertTrue(prioritized.containsAll(manager.getAllTasks()));
        Assertions.assertTrue(prioritized.containsAll(manager.getAllSubTasks()));
        Assertions.assertEquals(7, prioritized.size());
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
            Assertions.assertEquals(task, tester.get(i));
            i++;
        }
    }
}