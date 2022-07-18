package main.java.ru.yandex.practicum.task_tracker.task_managers;

import main.java.ru.yandex.practicum.task_tracker.tasks.Epic;
import main.java.ru.yandex.practicum.task_tracker.tasks.SubTask;
import main.java.ru.yandex.practicum.task_tracker.tasks.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    // Удаление всех задач
    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    // Создание новой задачи
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubTask(SubTask subTask);

    // Обновление существующей задачи
    int updateTask(Task task);

    int updateSubTask(SubTask subTask);

    int updateEpic(Epic epic);

    // Удаление задачи по идентификатору
    Task removeTask(int id);

    Task removeSubTask(int id);

    Task removeEpic(int id);

    // Получение списка всех задач
    List<Task> getAllTasks();

    List<Task> getAllSubTasks();

    List<Task> getAllEpics();

    // Получение задачи по идентификатору
    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    // Получение списка всех подзадач Эпика
    List<SubTask> getSubTasksFromEpic(int id);

    // Просмотр последних десяти задач
    List<Task> getHistory();

    // Получение всех задач и подзадач, отсортированных по времени начала, начиная с самой ранней.
    Set<Task> getPrioritizedTasks();
}
