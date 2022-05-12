import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    // Удаление всех задач
    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    // Создание новой задачи
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    // Обновление существующей задачи
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);


    void updateEpic(Epic epic);

    // Удаление задачи по идентификатору
    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    // Получение списка всех задач
    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, SubTask> getAllSubTasks();

    HashMap<Integer, Epic> getAllEpics();

    // Получение задачи по индентификатору
    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    // Полученмие списка всех подзадач Эпика
    ArrayList<SubTask> getSubTasksFromEpic(int id);

    // Генератор ID
    int idGenerator();

    // Просмотр последних десяти задач
    List<Task> getHistory();
}
