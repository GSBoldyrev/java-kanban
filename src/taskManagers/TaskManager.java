package taskManagers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    // Удаление всех задач
    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    // Создание новой задачи
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    // Обновление существующей задачи
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);


    void updateEpic(Epic epic);

    // Удаление задачи по идентификатору
    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

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
