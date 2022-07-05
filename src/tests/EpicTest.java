package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_managers.InMemoryTaskManager;
import task_managers.TaskManager;
import tasks.Epic;
import tasks.TaskStatus;
import tasks.SubTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EpicTest {

    private Epic epic;
    private SubTask task_1;
    private SubTask task_2;
    private SubTask task_3;
    private TaskManager manager;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    @BeforeEach
    public void createEnvironment() {
        epic = new Epic("NAME", "DESCRIPTION");
        manager = new InMemoryTaskManager();
        manager.addEpic(epic);
        task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
    }

    public void addSubtasks() {
        manager.addSubTask(task_1);
        manager.addSubTask(task_2);
        manager.addSubTask(task_3);
    }

    @Test
    public void shouldBeStatusNewWithEmptyList() {
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusNewWithAllOfNew() {
        addSubtasks();
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressIfOneIsInProgress() {
        task_1.setStatus(TaskStatus.IN_PROGRESS);
        addSubtasks();
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressIfOneIsDone() {
        task_1.setStatus(TaskStatus.DONE);
        addSubtasks();
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusDone() {
        task_1.setStatus(TaskStatus.DONE);
        task_2.setStatus(TaskStatus.DONE);
        task_3.setStatus(TaskStatus.DONE);
        addSubtasks();
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldCalculateZeroDuration() {
        addSubtasks();
        epic.calculateDuration();
        Assertions.assertEquals(0, epic.getDuration());
    }

    @Test
    public void shouldCalculate60Duration() {
        task_1.setDuration(23);
        task_2.setDuration(17);
        task_3.setDuration(20);
        addSubtasks();
        epic.calculateDuration();
        Assertions.assertEquals(60, epic.getDuration());
    }

    @Test
    public void startTimeShouldBeNullIfAllNull() {
        addSubtasks();
        epic.calculateStartTime();
        Assertions.assertNull(epic.getStartTime());
    }

    @Test
    public void startTimeShouldBeSetIfOneNull() {
        task_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        addSubtasks();
        epic.calculateStartTime();
        Assertions.assertEquals("13:00 - 01.01.2022", epic.getStartTime().format(FORMATTER));
    }

    @Test
    public void startTimeShouldBeCalculatedAsTask_2() {
        task_1.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        task_3.setStartTime(LocalDateTime.parse("18:00 - 01.01.2022", FORMATTER));
        addSubtasks();
        epic.calculateStartTime();
        Assertions.assertEquals("11:00 - 01.01.2022", epic.getStartTime().format(FORMATTER));
    }

    @Test
    public void endTimeShouldBeNullIfAllNull() {
        addSubtasks();
        epic.calculateEndTime();
        Assertions.assertNull(epic.getEndTime());
    }

    @Test
    public void endTimeShouldBeSetIfOneNull() {
        task_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        task_1.setDuration(60);
        addSubtasks();
        epic.calculateStartTime();
        Assertions.assertEquals("14:00 - 01.01.2022", epic.getEndTime().format(FORMATTER));
    }

    @Test
    public void endTimeShouldBeCalculatedAsTask_2() {
        task_1.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_1.setDuration(60);
        task_2.setStartTime(LocalDateTime.parse("17:00 - 01.01.2022", FORMATTER));
        task_2.setDuration(90);
        task_3.setStartTime(LocalDateTime.parse("16:00 - 01.01.2022", FORMATTER));
        task_3.setDuration(10);
        addSubtasks();
        epic.calculateStartTime();
        Assertions.assertEquals("18:30 - 01.01.2022", epic.getEndTime().format(FORMATTER));
    }
}