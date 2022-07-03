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
    private TaskManager manager;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");


    @BeforeEach
    public void createEpic() {
         epic = new Epic("NAME", "DESCRIPTION");
         manager = new InMemoryTaskManager();
         manager.addEpic(epic);
    }

    @Test
    public void shouldBeStatusNewWithEmptyList() {
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusNewWithAllOfNew() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressIfOneIsInProgress() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressIfOneIsDone() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.DONE);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusDone() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.DONE);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.DONE);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.DONE);
        manager.addSubTask(task_3);
        Assertions.assertEquals(3, epic.getSubTasks().size());
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldCalculateZeroDuration() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        epic.calculateDuration();
        Assertions.assertEquals(0, epic.getDuration());
    }

    @Test
    public void shouldCalculate60Duration() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        task_1.setDuration(32);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        task_2.setDuration(9);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        task_3.setDuration(19);
        manager.addSubTask(task_3);
        epic.calculateDuration();
        Assertions.assertEquals(60, epic.getDuration());
    }

    @Test
    public void startTimeShouldBeNullIfAllNull() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        epic.calculateStartTime();
        Assertions.assertNull(epic.getStartTime());
    }

    @Test
    public void startTimeShouldBeSetIfOneNull() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        task_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        epic.calculateStartTime();
        Assertions.assertEquals("13:00 - 01.01.2022", epic.getStartTime().format(FORMATTER));
    }

    @Test
    public void startTimeShouldBeCalculatedAsTask_2() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        task_1.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        task_2.setStartTime(LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        task_3.setStartTime(LocalDateTime.parse("18:00 - 01.01.2022", FORMATTER));
        manager.addSubTask(task_3);
        epic.calculateStartTime();
        Assertions.assertEquals("11:00 - 01.01.2022", epic.getStartTime().format(FORMATTER));
    }

    @Test
    public void endTimeShouldBeNullIfAllNull() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        epic.calculateEndTime();
        Assertions.assertNull(epic.getEndTime());
    }

    @Test
    public void endTimeShouldBeSetIfOneNull() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        task_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        task_1.setDuration(60);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        manager.addSubTask(task_3);
        epic.calculateStartTime();
        Assertions.assertEquals("14:00 - 01.01.2022", epic.getEndTime().format(FORMATTER));
    }

    @Test
    public void endTimeShouldBeCalculatedAsTask_2() {
        SubTask task_1 = new SubTask("1", "1", epic.getId());
        task_1.setStatus(TaskStatus.NEW);
        task_1.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_1.setDuration(60);
        manager.addSubTask(task_1);
        SubTask task_2 = new SubTask("2", "2", epic.getId());
        task_2.setStatus(TaskStatus.NEW);
        task_2.setStartTime(LocalDateTime.parse("17:00 - 01.01.2022", FORMATTER));
        task_2.setDuration(90);
        manager.addSubTask(task_2);
        SubTask task_3 = new SubTask("3", "3", epic.getId());
        task_3.setStatus(TaskStatus.NEW);
        task_3.setStartTime(LocalDateTime.parse("16:00 - 01.01.2022", FORMATTER));
        task_3.setDuration(10);
        manager.addSubTask(task_3);
        epic.calculateStartTime();
        Assertions.assertEquals("18:30 - 01.01.2022", epic.getEndTime().format(FORMATTER));
    }
}