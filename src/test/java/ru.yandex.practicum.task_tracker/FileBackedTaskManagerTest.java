package test.java.ru.yandex.practicum.task_tracker;

import org.junit.jupiter.api.Test;
import main.java.ru.yandex.practicum.task_tracker.task_managers.FileBackedTaskManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    public FileBackedTaskManager createManager() {
        return new FileBackedTaskManager
                (new File("C:\\Users\\maver\\IdeaProjects\\java-kanban\\src\\main\\resources\\data.csv"));
    }

    @Test
    public void shouldLoadFromEmptyFile() {
        // Act
        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\src\\test\\resources\\test_CSV\\emptyData.csv"));

        // Assert
        assertEquals(0, emptyManager.getAllTasks().size());
        assertEquals(0, emptyManager.getAllSubTasks().size());
        assertEquals(0, emptyManager.getAllEpics().size());
        assertEquals(0, emptyManager.getHistory().size());
        assertEquals(0, emptyManager.getId());
        assertNotNull(emptyManager);
    }

    @Test
    public void shouldLoadWithOneEmptyEpic() {
        // Act
        FileBackedTaskManager oneEpicManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\src\\test\\resources\\test_CSV\\emptyEpic.csv"));

        // Assert
        assertEquals(2, oneEpicManager.getAllEpics().size());
        assertEquals(3, oneEpicManager.getAllTasks().size());
        assertEquals(2, oneEpicManager.getHistory().size());
        assertTrue(oneEpicManager.getHistory().containsAll(oneEpicManager.getAllEpics()));
        assertEquals(0, oneEpicManager.getEpic(0).getSubTasks().size());
    }

    @Test
    public void shouldLoadWithEmptyHistory() {
        // Act
        FileBackedTaskManager emptyHistoryManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\src\\test\\resources\\test_CSV\\noHistory.csv"));

        // Assert
        assertEquals(1, emptyHistoryManager.getAllEpics().size());
        assertEquals(3, emptyHistoryManager.getAllTasks().size());
        assertEquals(0, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldLoadWithoutTimings() {
        // Act
        FileBackedTaskManager emptyHistoryManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\src\\test\\resources\\test_CSV\\noTiming.csv"));

        // Assert
        assertEquals(2, emptyHistoryManager.getAllEpics().size());
        assertEquals(2, emptyHistoryManager.getAllTasks().size());
        assertEquals(1, emptyHistoryManager.getAllSubTasks().size());
        assertEquals(4, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldLoadFromFile() {
        // Act
        FileBackedTaskManager standartManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\src\\test\\resources\\test_CSV\\standartCase.csv"));

        // Assert
        assertEquals(1, standartManager.getAllEpics().size());
        assertEquals(6, standartManager.getAllTasks().size());
        assertEquals(3, standartManager.getAllSubTasks().size());
        assertEquals(6, standartManager.getHistory().size());
        assertEquals(72, standartManager.getId());
    }
}
