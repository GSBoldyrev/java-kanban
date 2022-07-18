package test.java.ru.yandex.practicum.task_tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.ru.yandex.practicum.task_tracker.task_managers.InMemoryTaskManager;
import main.java.ru.yandex.practicum.task_tracker.task_managers.TaskManager;
import main.java.ru.yandex.practicum.task_tracker.tasks.Epic;
import main.java.ru.yandex.practicum.task_tracker.tasks.SubTask;
import main.java.ru.yandex.practicum.task_tracker.tasks.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        // Assert
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusNewWithAllOfNew() {
        // Act
        addSubtasks();

        // Assert
        assertEquals(3, epic.getSubTasks().size());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressIfOneIsInProgress() {
        // Arrange
        task_1.setStatus(TaskStatus.IN_PROGRESS);

        // Act
        addSubtasks();

        // Assert
        assertEquals(3, epic.getSubTasks().size());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressIfOneIsDone() {
        // Arrange
        task_1.setStatus(TaskStatus.DONE);

        // Act
        addSubtasks();

        // Assert
        assertEquals(3, epic.getSubTasks().size());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusDone() {
        // Arrange
        task_1.setStatus(TaskStatus.DONE);
        task_2.setStatus(TaskStatus.DONE);
        task_3.setStatus(TaskStatus.DONE);

        // Act
        addSubtasks();

        // Assert
        assertEquals(3, epic.getSubTasks().size());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldCalculateZeroDuration() {
        // Arrange
        addSubtasks();

        // Act
        epic.calculateDuration();

        // Assert
        assertEquals(0, epic.getDuration());
    }

    @Test
    public void shouldCalculate60Duration() {
        // Arrange
        task_1.setDuration(23);
        task_2.setDuration(17);
        task_3.setDuration(20);
        addSubtasks();

        // Act
        epic.calculateDuration();

        // Assert
        assertEquals(60, epic.getDuration());
    }

    @Test
    public void startTimeShouldBeNullIfAllNull() {
        // Arrange
        addSubtasks();

        // Act
        epic.calculateStartTime();

        // Assert
        assertNull(epic.getStartTime());
    }

    @Test
    public void startTimeShouldBeSetIfOneNull() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        addSubtasks();

        // Act
        epic.calculateStartTime();

        // Assert
        assertEquals("13:00 - 01.01.2022", epic.getStartTime().format(FORMATTER));
    }

    @Test
    public void startTimeShouldBeCalculatedAsTask_2() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("11:00 - 01.01.2022", FORMATTER));
        task_3.setStartTime(LocalDateTime.parse("18:00 - 01.01.2022", FORMATTER));
        addSubtasks();

        // Act
        epic.calculateStartTime();

        // Assert
        assertEquals("11:00 - 01.01.2022", epic.getStartTime().format(FORMATTER));
    }

    @Test
    public void endTimeShouldBeNullIfAllNull() {
        // Arrange
        addSubtasks();

        // Act
        epic.calculateEndTime();

        // Assert
        assertNull(epic.getEndTime());
    }

    @Test
    public void endTimeShouldBeSetIfOneNull() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("13:00 - 01.01.2022", FORMATTER));
        task_1.setDuration(60);
        addSubtasks();

        // Act
        epic.calculateStartTime();

        // Assert
        assertEquals("14:00 - 01.01.2022", epic.getEndTime().format(FORMATTER));
    }

    @Test
    public void endTimeShouldBeCalculatedAsTask_2() {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("14:00 - 01.01.2022", FORMATTER));
        task_1.setDuration(60);
        task_2.setStartTime(LocalDateTime.parse("17:00 - 01.01.2022", FORMATTER));
        task_2.setDuration(90);
        task_3.setStartTime(LocalDateTime.parse("16:00 - 01.01.2022", FORMATTER));
        task_3.setDuration(10);
        addSubtasks();

        // Act
        epic.calculateStartTime();

        // Assert
        assertEquals("18:30 - 01.01.2022", epic.getEndTime().format(FORMATTER));
    }
}