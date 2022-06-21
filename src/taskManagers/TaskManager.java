package taskManagers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {

    // Удаление всех задач
    void deleteAllTasks() throws IOException;

    void deleteAllSubTasks() throws IOException;

    void deleteAllEpics() throws IOException;

    // Создание новой задачи
    void addTask(Task task) throws IOException;

    void addEpic(Epic epic) throws IOException;

    void addSubTask(SubTask subTask) throws IOException;

    // Обновление существующей задачи
    void updateTask(Task task) throws IOException;

    void updateSubTask(SubTask subTask) throws IOException;


    void updateEpic(Epic epic) throws IOException;

    // Удаление задачи по идентификатору
    void removeTask(int id) throws IOException;

    void removeSubTask(int id) throws IOException;

    void removeEpic(int id) throws IOException;

    // Получение списка всех задач
    List<Task> getAllTasks();

    List<Task> getAllSubTasks();

    List<Task> getAllEpics();

    // Получение задачи по индентификатору
    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    // Полученмие списка всех подзадач Эпика
    List<SubTask> getSubTasksFromEpic(int id);

    // Просмотр последних десяти задач
    List<Task> getHistory();
}
