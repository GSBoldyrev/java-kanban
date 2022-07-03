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

    private T manager;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    public abstract T createManager();

    @BeforeEach
    public void updateManager() {
        manager = createManager();
    }

    @Test
    public void deleteAllTasks() {
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ");
        Task task_2 = new Task("ИГРА", "АВАЛОН");
        manager.addTask(task_1);
        manager.addTask(task_2);
        Assertions.assertEquals(2, manager.getAllTasks().size());
        manager.deleteAllTasks();
        Assertions.assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void deleteAllSubTasks() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        Epic epic_2 = new Epic("ИГРА", "НАСТОЛКИ");
        manager.addEpic(epic_1);
        manager.addEpic(epic_2);
        Assertions.assertEquals(2, manager.getAllEpics().size());
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epic_1.getId());
        first.setStatus(TaskStatus.NEW);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epic_1.getId());
        second.setStatus(TaskStatus.NEW);
        SubTask third = new SubTask("МАРС", "ЕВРО", epic_2.getId());
        third.setStatus(TaskStatus.NEW);
        SubTask fourth = new SubTask("ДЮНА", "АРЕАКОНТРОЛЬ", epic_2.getId());
        fourth.setStatus(TaskStatus.NEW);
        SubTask fifth = new SubTask("ТАЛИСМАН", "АМЕРИ", epic_2.getId());
        fifth.setStatus(TaskStatus.NEW);
        manager.addSubTask(first);
        manager.addSubTask(second);
        manager.addSubTask(third);
        manager.addSubTask(fourth);
        manager.addSubTask(fifth);
        Assertions.assertEquals(5, manager.getAllSubTasks().size());
        manager.deleteAllSubTasks();
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertEquals(0, epic_1.getSubTasks().size());
        Assertions.assertEquals(0, epic_2.getSubTasks().size());
    }

    @Test
    public void deleteAllEpics() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        Epic epic_2 = new Epic("ИГРА", "НАСТОЛКИ");
        manager.addEpic(epic_1);
        manager.addEpic(epic_2);
        Assertions.assertEquals(2, manager.getAllEpics().size());
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epic_1.getId());
        first.setStatus(TaskStatus.NEW);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epic_1.getId());
        second.setStatus(TaskStatus.NEW);
        SubTask third = new SubTask("МАРС", "ЕВРО", epic_2.getId());
        third.setStatus(TaskStatus.NEW);
        SubTask fourth = new SubTask("ДЮНА", "АРЕАКОНТРОЛЬ", epic_2.getId());
        fourth.setStatus(TaskStatus.NEW);
        SubTask fifth = new SubTask("ТАЛИСМАН", "АМЕРИ", epic_2.getId());
        fifth.setStatus(TaskStatus.NEW);
        manager.addSubTask(first);
        manager.addSubTask(second);
        manager.addSubTask(third);
        manager.addSubTask(fourth);
        manager.addSubTask(fifth);
        Assertions.assertEquals(5, manager.getAllSubTasks().size());
        manager.deleteAllEpics();
        Assertions.assertEquals(0, manager.getAllEpics().size());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    public void shouldAddTaskWithNormalConditions() {
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ");
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
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ", TaskStatus.NEW,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        Task task_2 = new Task("ИГРА", "АВАЛОН", TaskStatus.NEW,
                60, LocalDateTime.parse("12:30 - 01.01.2022", FORMATTER));
        int id_1 = manager.addTask(task_1);
        int id_2 = manager.addTask(task_2);
        Assertions.assertEquals(0, id_1);
        Assertions.assertEquals(-2, id_2);
    }

    @Test
    public void shouldAddEpicWithNormalConditions() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
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
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        subTask.setStatus(TaskStatus.NEW);
        int id = manager.addSubTask(subTask);
        SubTask savedSubTask = manager.getSubTask(id);
        Assertions.assertNotNull(savedSubTask, "Задача не сохранилась");
        Assertions.assertEquals(savedSubTask, subTask);
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
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ",
                TaskStatus.NEW, epicId, 30, LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addSubTask(first);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ",
                TaskStatus.NEW, epicId, 60, LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        int id_2 = manager.addSubTask(second);
        Assertions.assertEquals(0, epicId);
        Assertions.assertEquals(1, id_1);
        Assertions.assertEquals(-2, id_2);
    }
    @Test
    public void shouldNotAddSubtaskWithoutEpic() {
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", 1);
        int id = manager.addSubTask(subTask);
        Assertions.assertEquals(-2, id);
    }

    @Test
    public void shouldUpdateTaskWithNormalConditions() {
        Task task_1 = new Task("ИГРА", "АВАЛОН");
        int id = manager.addTask(task_1);
        Task task_2 = new Task("ИГРА", "НЕЧТО");
        task_2.setId(id);
        int updatedId = manager.updateTask(task_2);
        Task savedTask = manager.getTask(updatedId);
        Assertions.assertNotNull(savedTask, "Задача не сохранилась");
        Assertions.assertEquals(savedTask, task_2);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Assertions.assertEquals("НЕЧТО", manager.getTask(id).getDescription());
        Assertions.assertEquals(id, updatedId);
    }

    @Test
    public void shouldUpdateTaskWithSameStartTime() {
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ", TaskStatus.NEW,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id = manager.addTask(task_1);
        Task task_2 = new Task("ИГРА", "АВАЛОН", TaskStatus.NEW,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setId(id);
        int updatedId = manager.updateTask(task_2);
        Assertions.assertEquals(0, updatedId);
    }

    @Test
    public void shouldNotUpdateTaskToNull() {
        Task task_1 = new Task("ИГРА", "АВАЛОН");
        manager.addTask(task_1);
        int updated_id = manager.updateTask(null);
        Assertions.assertEquals(-2, updated_id);
    }

    @Test
    public void shouldNotUpdateTaskWithWrongId() {
        Task task_1 = new Task("ИГРА", "АВАЛОН");
        int id = manager.addTask(task_1);
        Task task_2 = new Task("ИГРА", "НЕЧТО");
        task_2.setId(id+1);
        int updateId = manager.updateTask(task_2);
        Assertions.assertEquals(-3, updateId);
    }

    @Test
    public void shouldNotUpdateTaskWithTimeIntersection() {
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ", TaskStatus.NEW,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addTask(task_1);
        Task task_2 = new Task("КУХНЯ", "ОБЕД", TaskStatus.NEW,
                60, LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        manager.addTask(task_2);
        Task updatedTAsk = new Task("ИГРА", "АВАЛОН", TaskStatus.NEW,
                60, LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        updatedTAsk.setId(id_1);
        int updatedId = manager.updateTask(updatedTAsk);
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateSubTaskWithNormalConditions() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        int id = manager.addSubTask(subTask);
        SubTask updatedSubTask = new SubTask("ЗЕРКАЛА", "ВЫТИРАТЬ", epicId);
        updatedSubTask.setId(id);
        int updatedId = manager.updateSubTask(updatedSubTask);
        SubTask savedSubTask = manager.getSubTask(updatedId);
        Assertions.assertNotNull(savedSubTask, "Задача не сохранилась");
        Assertions.assertEquals(savedSubTask, updatedSubTask);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals("ЗЕРКАЛА", manager.getSubTask(id).getName());
    }

    @Test
    public void shouldNotUpdateSubtaskWithoutEpic() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        int id = manager.addSubTask(subTask);
        SubTask updatedSubTask = new SubTask("ЗЕРКАЛА", "ВЫТИРАТЬ", (epicId+1));
        updatedSubTask.setId(id);
        int updatedId = manager.updateSubTask(updatedSubTask);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateSubTaskWithSameStartTime() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", TaskStatus.NEW, epicId,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id = manager.addSubTask(subTask);
        SubTask updatedSubTask = new SubTask("ЗЕРКАЛА", "ВЫТИРАТЬ", TaskStatus.NEW, epicId,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        updatedSubTask.setId(id);
        int updatedId = manager.updateSubTask(updatedSubTask);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(id, updatedId);
    }

    @Test
    public void shouldNotUpdateNullSubTask() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        int id = manager.addSubTask(subTask);
        int updatedId = manager.updateSubTask(null);
        Assertions.assertEquals(1, id);
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldNotUpdateSubTaskWithWrongId() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        int id = manager.addSubTask(subTask);
        SubTask updatedSubTask = new SubTask("ЗЕРКАЛА", "ВЫТИРАТЬ", epicId);
        updatedSubTask.setId(id+1);
        int updatedId = manager.updateSubTask(updatedSubTask);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(-3, updatedId);
    }

    @Test
    public void shouldNotUpdateSubTaskWithTimeIntersection() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask_1 = new SubTask("ПОЛЫ", "МЫТЬ", TaskStatus.NEW, epicId,
                60, LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id_1 = manager.addSubTask(subTask_1);
        SubTask subTask_2 = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", TaskStatus.NEW, epicId,
                60, LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        manager.addSubTask(subTask_2);
        SubTask updatedSubTask = new SubTask("ЗЕРКАЛА", "ВЫТИРАТЬ", TaskStatus.NEW, epicId,
                60, LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        updatedSubTask.setId(id_1);
        int updatedId = manager.updateSubTask(updatedSubTask);
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldUpdateEpicWithNormalConditions() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        first.setStatus(TaskStatus.DONE);
        manager.addSubTask(first);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epicId);
        second.setStatus(TaskStatus.DONE);
        manager.addSubTask(second);
        Assertions.assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus());
        Assertions.assertEquals(2, manager.getEpic(epicId).getSubTasks().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Epic epic_2 = new Epic("УБОРКА", "КОРИДОР");
        epic_2.setId(epicId);
        int updatedId = manager.updateEpic(epic_2);
        Epic savedEpic = manager.getEpic(epicId);
        Assertions.assertNotNull(savedEpic, "Задача не сохранилась");
        Assertions.assertEquals(TaskStatus.NEW, manager.getEpic(updatedId).getStatus());
        Assertions.assertEquals(0, manager.getEpic(updatedId).getSubTasks().size());
        Assertions.assertEquals("КОРИДОР", manager.getEpic(updatedId).getDescription());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertEquals(epicId, updatedId);
    }

    @Test
    public void shouldNotUpdateNullEpic() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        Assertions.assertEquals(0, epicId);
        int updatedId = manager.updateEpic(null);
        Assertions.assertEquals(-2, updatedId);
    }

    @Test
    public void shouldNotUpdateEpicWithWrongId() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        first.setStatus(TaskStatus.DONE);
        manager.addSubTask(first);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epicId);
        second.setStatus(TaskStatus.DONE);
        manager.addSubTask(second);
        Assertions.assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus());
        Assertions.assertEquals(2, manager.getEpic(epicId).getSubTasks().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Epic epic_2 = new Epic("УБОРКА", "КОРИДОР");
        epic_2.setId(epicId+1);
        int updatedId = manager.updateEpic(epic_2);
        Assertions.assertEquals(-3, updatedId);
    }

    @Test
    public void shouldRemoveTaskWIthNormalConditions() {
        Task task = new Task("УБОРКА", "МЫТЬ ПОЛЫ");
        int id = manager.addTask(task);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Task removedTask = manager.removeTask(id);
        Assertions.assertEquals(0, manager.getAllTasks().size());
        Assertions.assertNull(manager.getTask(id));
        Assertions.assertEquals(task, removedTask);
    }

    @Test
    public void shouldNotRemoveTaskWithWrongId() {
        Task task = new Task("УБОРКА", "МЫТЬ ПОЛЫ");
        int id = manager.addTask(task);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Task removedTask = manager.removeTask(id+1);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Assertions.assertNull(removedTask);
    }

    @Test
   public void shouldRemoveSubTaskWithNormalConditions() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        subTask.setStatus(TaskStatus.DONE);
        int id = manager.addSubTask(subTask);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(1, manager.getEpic(epicId).getSubTasks().size());
        SubTask removedSubTask = (SubTask) manager.removeSubTask(id);
        Assertions.assertEquals(TaskStatus.NEW, manager.getEpic(epicId).getStatus());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertEquals(0, manager.getEpic(epicId).getSubTasks().size());
        Assertions.assertNull(manager.getSubTask(id));
        Assertions.assertEquals(subTask, removedSubTask);
    }

    @Test
    public void shouldNotRemoveSubTaskWithWrongId() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        subTask.setStatus(TaskStatus.DONE);
        int id = manager.addSubTask(subTask);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(1, manager.getEpic(epicId).getSubTasks().size());
        SubTask removedSubTask = (SubTask) manager.removeSubTask(id+1);
        Assertions.assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus());
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertEquals(1, manager.getEpic(epicId).getSubTasks().size());
        Assertions.assertNull(removedSubTask);
    }

    @Test
    public void shouldRemoveEpicWithNormalConditions() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        first.setStatus(TaskStatus.DONE);
        manager.addSubTask(first);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epicId);
        second.setStatus(TaskStatus.DONE);
        manager.addSubTask(second);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Epic removedEpic = (Epic) manager.removeEpic(epicId);
        Assertions.assertEquals(0, manager.getAllEpics().size());
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        Assertions.assertNull(manager.getEpic(epicId));
        Assertions.assertEquals(epic_1, removedEpic);
    }

    @Test
    public void shouldNotRemoveEpicWithWrongId() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        first.setStatus(TaskStatus.DONE);
        manager.addSubTask(first);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epicId);
        second.setStatus(TaskStatus.DONE);
        manager.addSubTask(second);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Epic removedEpic = (Epic) manager.removeEpic(epicId+1);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Assertions.assertNull(removedEpic);
    }

    @Test
    public void getAllTasks() {
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ");
        Task task_2 = new Task("ИГРА", "АВАЛОН");
        Assertions.assertEquals(0, manager.getAllTasks().size());
        int id_1 = manager.addTask(task_1);
        Assertions.assertEquals(1, manager.getAllTasks().size());
        Assertions.assertTrue(manager.getAllTasks().contains(task_1));
        int id_2 = manager.addTask(task_2);
        Assertions.assertEquals(2, manager.getAllTasks().size());
        Assertions.assertTrue(manager.getAllTasks().contains(task_1));
        Assertions.assertTrue(manager.getAllTasks().contains(task_2));
    }

    @Test
    public void getAllSubTasks() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic_1);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        first.setStatus(TaskStatus.DONE);
        Assertions.assertEquals(0, manager.getAllSubTasks().size());
        manager.addSubTask(first);
        Assertions.assertEquals(1, manager.getAllSubTasks().size());
        Assertions.assertTrue(manager.getAllSubTasks().contains(first));
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epicId);
        second.setStatus(TaskStatus.DONE);
        manager.addSubTask(second);
        Assertions.assertEquals(2, manager.getAllSubTasks().size());
        Assertions.assertTrue(manager.getAllSubTasks().contains(first));
        Assertions.assertTrue(manager.getAllSubTasks().contains(second));
    }

    @Test
    public void getAllEpics() {
        Epic epic_1 = new Epic("УБОРКА", "КУХНЯ");
        Epic epic_2 = new Epic("ИГРА", "НАСТОЛКИ");
        Assertions.assertEquals(0, manager.getAllEpics().size());
        manager.addEpic(epic_1);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertTrue(manager.getAllEpics().contains(epic_1));
        manager.addEpic(epic_2);
        Assertions.assertEquals(2, manager.getAllEpics().size());
        Assertions.assertTrue(manager.getAllEpics().contains(epic_1));
        Assertions.assertTrue(manager.getAllEpics().contains(epic_2));
    }

    @Test
    public void getTask() {
        Task task_1 = new Task("УБОРКА", "МЫТЬ ПОЛЫ");
        int id_1 = manager.addTask(task_1);
        Assertions.assertEquals(task_1, manager.getTask(id_1));
        Assertions.assertNull(manager.getTask(45));
    }

    @Test
    public void getSubTask() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask subTask = new SubTask("ПОЛЫ", "МЫТЬ", epicId);
        subTask.setStatus(TaskStatus.DONE);
        int id = manager.addSubTask(subTask);
        Assertions.assertEquals(subTask, manager.getSubTask(id));
        Assertions.assertNull(manager.getSubTask(45));
    }

    @Test
    public void getEpic() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int id = manager.addEpic(epic);
        Assertions.assertEquals(epic, manager.getEpic(id));
        Assertions.assertNull(manager.getTask(45));
    }

    @Test
    public void getSubTasksFromEpic() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        SubTask first = new SubTask("ПОЛЫ", "МЫТЬ", epic.getId());
        first.setStatus(TaskStatus.NEW);
        SubTask second = new SubTask("ПЫЛЬ", "ВЫТИРАТЬ", epic.getId());
        second.setStatus(TaskStatus.NEW);
        int id_1 = manager.addSubTask(first);
        int id_2 = manager.addSubTask(second);
        List<SubTask> fromEpic = manager.getSubTasksFromEpic(epicId);
        Assertions.assertTrue(manager.getAllSubTasks().containsAll(fromEpic));
        Assertions.assertTrue(fromEpic.contains(first));
        Assertions.assertEquals(2, fromEpic.size());
    }

    @Test
    public void shouldNotGetSubTasksFromEpicWithWrongId() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        List<SubTask> fromEpic = manager.getSubTasksFromEpic(epicId + 1);
        Assertions.assertNull(fromEpic);
    }

    @Test
    public void getHistory() {
        Epic epic = new Epic("УБОРКА", "КУХНЯ");
        int epicId = manager.addEpic(epic);
        Task task = new Task("ИГРА", "АВАЛОН");
        int id = manager.addTask(task);
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
        Task task_1 = new Task("T1", "TD1", TaskStatus.NEW, 15,
                LocalDateTime.parse("10:00 - 01.01.2022", FORMATTER));
        Task task_2 = new Task("T2", "TD2", TaskStatus.NEW, 15,
                LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id_0 = manager.addTask(task_1);
        int id_1 = manager.addTask(task_2);
        Epic epic_1 = new Epic("E1", "ED1");
        Epic epic_2 = new Epic("E2", "ED2");
        int id_2 = manager.addEpic(epic_1);
        int id_3 = manager.addEpic(epic_2);
        SubTask first = new SubTask("S1", "SD1", TaskStatus.NEW, id_2, 15,
                LocalDateTime.parse("10:30 - 01.01.2022", FORMATTER));
        SubTask second = new SubTask("S2", "SD2", TaskStatus.NEW, id_2, 15,
                LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        SubTask third = new SubTask("S3", "SD3", TaskStatus.NEW, id_3, 15,
                LocalDateTime.parse("11:30 - 01.01.2022", FORMATTER));
        SubTask fourth = new SubTask("S4", "SD4", TaskStatus.NEW, id_3, 15,
                LocalDateTime.parse("12:30 - 01.01.2022", FORMATTER));
        SubTask fifth = new SubTask("S5", "SD5", TaskStatus.NEW, id_3, 15,
                LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        int id_4 = manager.addSubTask(first);
        int id_5 = manager.addSubTask(second);
        int id_6 = manager.addSubTask(third);
        int id_7 = manager.addSubTask(fourth);
        int id_8 = manager.addSubTask(fifth);
        Set<Task> prioritized = manager.getPrioritizedTasks();
        System.out.println(prioritized);
        Assertions.assertTrue(prioritized.containsAll(manager.getAllTasks()));
        Assertions.assertTrue(prioritized.containsAll(manager.getAllSubTasks()));
        Assertions.assertEquals(7, prioritized.size());
        List<Task> tester = new ArrayList<>();
        tester.add(task_1);
        tester.add(first);
        tester.add(fifth);
        tester.add(third);
        tester.add(task_2);
        tester.add(fourth);
        tester.add(second);
        int i = 0;
        for (Task task: prioritized) {
           Assertions.assertEquals(task, tester.get(i));
           i++;
        }
    }

    @Test
    public void getPrioritizedTasksWithSomeNullTimes() {
        Task task_1 = new Task("T1", "TD1", TaskStatus.NEW, 15,
                null);
        Task task_2 = new Task("T2", "TD2", TaskStatus.NEW, 15,
                LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        int id_0 = manager.addTask(task_1);
        int id_1 = manager.addTask(task_2);
        Epic epic_1 = new Epic("E1", "ED1");
        Epic epic_2 = new Epic("E2", "ED2");
        int id_2 = manager.addEpic(epic_1);
        int id_3 = manager.addEpic(epic_2);
        SubTask first = new SubTask("S1", "SD1", TaskStatus.NEW, id_2, 15,
                LocalDateTime.parse("10:30 - 01.01.2022", FORMATTER));
        SubTask second = new SubTask("S2", "SD2", TaskStatus.NEW, id_2, 15,
                null);
        SubTask third = new SubTask("S3", "SD3", TaskStatus.NEW, id_3, 15,
                LocalDateTime.parse("11:30 - 01.01.2022", FORMATTER));
        SubTask fourth = new SubTask("S4", "SD4", TaskStatus.NEW, id_3, 15,
                null);
        SubTask fifth = new SubTask("S5", "SD5", TaskStatus.NEW, id_3, 15,
                LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        int id_4 = manager.addSubTask(first);
        int id_5 = manager.addSubTask(second);
        int id_6 = manager.addSubTask(third);
        int id_7 = manager.addSubTask(fourth);
        int id_8 = manager.addSubTask(fifth);
        Set<Task> prioritized = manager.getPrioritizedTasks();
        System.out.println(prioritized);
        Assertions.assertTrue(prioritized.containsAll(manager.getAllTasks()));
        Assertions.assertTrue(prioritized.containsAll(manager.getAllSubTasks()));
        Assertions.assertEquals(7, prioritized.size());
        List<Task> tester = new ArrayList<>();
        tester.add(first);
        tester.add(fifth);
        tester.add(third);
        tester.add(task_2);
        tester.add(task_1);
        tester.add(second);
        tester.add(fourth);
        int i = 0;
        for (Task task: prioritized) {
            Assertions.assertEquals(task, tester.get(i));
            i++;
        }
    }
}