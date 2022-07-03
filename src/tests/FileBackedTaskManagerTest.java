package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task_managers.FileBackedTaskManager;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    public FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File("C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\data.csv"));
    }

    @Test
    public void shouldLoadFromEmptyFile() {
        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\production\\test_files\\emptyData.csv"));
        Assertions.assertEquals(0, emptyManager.getAllTasks().size());
        Assertions.assertEquals(0, emptyManager.getAllSubTasks().size());
        Assertions.assertEquals(0, emptyManager.getAllEpics().size());
        Assertions.assertEquals(0, emptyManager.getHistory().size());
        Assertions.assertEquals(0, emptyManager.getId());
        Assertions.assertNotNull(emptyManager);
    }

    @Test
    public void shouldLoadWithOneEmptyEpic() {
        FileBackedTaskManager oneEpicManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\production\\test_files\\emptyEpic.csv"));
        Assertions.assertEquals(2, oneEpicManager.getAllEpics().size());
        Assertions.assertEquals(3, oneEpicManager.getAllTasks().size());
        Assertions.assertEquals(2, oneEpicManager.getHistory().size());
        Assertions.assertTrue(oneEpicManager.getHistory().containsAll(oneEpicManager.getAllEpics()));
        Assertions.assertEquals(0, oneEpicManager.getEpic(0).getSubTasks().size());
    }

    @Test
    public void shouldLoadWithEmptyHistory() {
        FileBackedTaskManager emptyHistoryManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\production\\test_files\\noHistory.csv"));
        Assertions.assertEquals(1, emptyHistoryManager.getAllEpics().size());
        Assertions.assertEquals(3, emptyHistoryManager.getAllTasks().size());
        Assertions.assertEquals(0, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldLoadWithoutTimings() {
        FileBackedTaskManager emptyHistoryManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\production\\test_files\\noTiming.csv"));
        Assertions.assertEquals(2, emptyHistoryManager.getAllEpics().size());
        Assertions.assertEquals(2, emptyHistoryManager.getAllTasks().size());
        Assertions.assertEquals(1, emptyHistoryManager.getAllSubTasks().size());
        Assertions.assertEquals(4, emptyHistoryManager.getHistory().size());
    }

    @Test
    public void shouldLoadFromFile() {
        FileBackedTaskManager standartManager = FileBackedTaskManager.loadFromFile(new File(
                "C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\production\\test_files\\standartCase.csv"));
        Assertions.assertEquals(1, standartManager.getAllEpics().size());
        Assertions.assertEquals(6, standartManager.getAllTasks().size());
        Assertions.assertEquals(3, standartManager.getAllSubTasks().size());
        Assertions.assertEquals(6, standartManager.getHistory().size());
        Assertions.assertEquals(72, standartManager.getId());
    }
}
