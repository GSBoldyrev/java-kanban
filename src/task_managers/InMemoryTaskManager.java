package task_managers;

import exceptions.EpicNotFoundException;
import exceptions.NullTaskException;
import exceptions.TaskNotFoundException;
import exceptions.TimeIntersectionException;
import history_managers.HistoryManager;
import misc.Managers;
import tasks.Epic;
import tasks.TaskStatus;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        getAllTasks().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    // Во всех эпиках очищаются списки подзадач
    @Override
    public void deleteAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        getAllSubTasks().forEach(prioritizedTasks::remove);
        subTasks.clear();
        for (int id : epics.keySet()) {
            List<SubTask> subTasks = epics.get(id).getSubTasks();
            subTasks.clear();
        }
    }

    // При удалении эпиков также удаляются и подзадачи
    @Override
    public void deleteAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        getAllSubTasks().forEach(prioritizedTasks::remove);
        subTasks.clear();
    }

    // Создание новой задачи
    @Override
    public int addTask(Task task) {
        try {
            validateNotNull(task);
            validateTimes(task);
        } catch (NullTaskException | TimeIntersectionException e) {
            System.out.println(e.getMessage());
            return -2;
        }
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        try {
            validateNotNull(epic);
        } catch (NullTaskException e) {
            System.out.println(e.getMessage());
            return -2;
        }
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        try {
            validateNotNull(subTask);
            validateForEpic(subTask);
            validateTimes(subTask);
        } catch (TimeIntersectionException | NullTaskException | EpicNotFoundException e) {
            System.out.println(e.getMessage());
            return -2;
        }
        int id = generateId();
        subTask.setId(id);
        subTasks.put(id, subTask);
        prioritizedTasks.add(subTask);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        List<SubTask> subTasks = epic.getSubTasks();
        subTasks.add(subTask);
        setEpicStatus(epic);
        setEpicTimes(epic);
        return id;
    }

    // Обновление существующей задачи
    @Override
    public int updateTask(Task task) {
        try {
            validateNotNull(task);
            validateTimes(task);
        } catch (TimeIntersectionException | NullTaskException e) {
            System.out.println(e.getMessage());
            return -2;
        }
        try {
            validateId(task.getId(), getAllTasks());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return -3;
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.removeIf(task1 -> task1.getId() == task.getId());
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        try {
            validateNotNull(subTask);
            validateForEpic(subTask);
            validateTimes(subTask);
        } catch (TimeIntersectionException | NullTaskException | EpicNotFoundException e) {
            System.out.println(e.getMessage());
            return -2;
        }
        try {
            validateId(subTask.getId(), getAllSubTasks());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return -3;
        }
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        List<SubTask> subTasks = epic.getSubTasks();
        subTasks.removeIf(task -> task.getId() == subTask.getId());
        subTasks.add(subTask);
        setEpicStatus(epic);
        setEpicTimes(epic);
        prioritizedTasks.removeIf(task1 -> task1.getId() == subTask.getId());
        prioritizedTasks.add(subTask);
        return subTask.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        try {
            validateNotNull(epic);
        } catch (NullTaskException e) {
            System.out.println(e.getMessage());
            return -2;
        }
        try {
            validateId(epic.getId(), getAllEpics());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return -3;
        }
        int id = epic.getId();
        epics.get(id).getSubTasks().forEach(prioritizedTasks::remove);
        epics.get(id).getSubTasks().forEach(subTasks.values()::remove);
        epics.put(id, epic);
        return id;
    }

    // Удаление задачи по идентификатору
    @Override
    public Task removeTask(int id) {
        try {
            validateId(id, getAllTasks());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        historyManager.remove(id);
        Task task = tasks.remove(id);
        prioritizedTasks.removeIf(task1 -> task1.getId() == id);
        return task;
    }

    @Override
    public Task removeSubTask(int id) {
        try {
            validateId(id, getAllSubTasks());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        historyManager.remove(id);
        int epicId = subTasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        List<SubTask> subTasks = epic.getSubTasks();
        subTasks.removeIf(task -> task.getId() == id);
        Task task = this.subTasks.remove(id);
        prioritizedTasks.removeIf(task1 -> task1.getId() == id);
        setEpicStatus(epic);
        setEpicTimes(epic);
        return task;
    }

    @Override
    public Task removeEpic(int id) {
        try {
            validateId(id, getAllEpics());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        historyManager.remove(id);
        Epic epic = epics.remove(id);
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        return epic;
    }

    // Получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    // Получение задачи по идентификатору
    @Override
    public Task getTask(int id) {
        try {
            validateId(id, getAllTasks());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        try {
            validateId(id, getAllSubTasks());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        try {
            validateId(id, getAllEpics());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    // Получение списка всех подзадач Эпика
    @Override
    public List<SubTask> getSubTasksFromEpic(int id) {
        try {
            validateId(id, getAllEpics());
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        Epic epic = epics.get(id);
        return epic.getSubTasks();
    }

    // Просмотр истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Генератор ID
    private int generateId() {
        return id++;
    }

    // Вычисление статуса Эпика
    protected void setEpicStatus(Epic epic) {
        List<SubTask> subTasks = epic.getSubTasks();
        int counterNew = 0;
        int counterDone = 0;
        for (SubTask task : subTasks) {
            if (task.getStatus() == TaskStatus.DONE) {
                counterDone++;
            } else if (task.getStatus() == TaskStatus.NEW) {
                counterNew++;
            }
        }
        if (subTasks.size() == 0) {
            epic.setStatus(TaskStatus.NEW);
        } else if (counterDone == subTasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (counterNew == subTasks.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    // Вспомогательный метод, вычисляющий продолжительность, время начала и окончания Эпика.
    protected void setEpicTimes(Epic epic) {
        epic.calculateStartTime();
        epic.calculateDuration();
        epic.calculateEndTime();
    }

    // Валидатор, не пропускающий задачу, пересекающуюся по времени с уже имеющимися.
    private void validateTimes(Task task) throws TimeIntersectionException {
        for (Task checkedTask : prioritizedTasks) {
            LocalDateTime start = task.getStartTime();
            LocalDateTime end = task.getEndTime();
            LocalDateTime checkedStart = checkedTask.getStartTime();
            LocalDateTime checkedEnd = checkedTask.getEndTime();
            if (start == null || checkedStart == null) {
                continue;
            }
            boolean duringCheckedTask = start.isAfter(checkedStart) && start.isBefore(checkedEnd);
            boolean duringTask = end.isAfter(checkedStart) && end.isBefore(checkedEnd);
            boolean sameTask = task.getId() == checkedTask.getId();
            boolean sameStart = start.isEqual(checkedStart);
            if (sameTask && (sameStart || duringCheckedTask)) {
                continue;
            }
            if (sameStart) {
                throw new TimeIntersectionException("В это время у вас запланирована другая задача: "
                        + checkedTask.getName());
            } else if (duringCheckedTask) {
                throw new TimeIntersectionException("В это время вы будете заняты другой задачей: "
                        + checkedTask.getName());
            } else if (duringTask) {
                throw new TimeIntersectionException("Вы не успеете закончить эту задачу до начала задачи: "
                        + checkedTask.getName());
            }
        }
    }

    // Валидатор, проверяющий задачу на null.
    private void validateNotNull(Task task) throws NullTaskException {
        if (task == null) {
            throw new NullTaskException("Задача не обнаружена");
        }
    }

    // Валидатор, проверяющий существует ли эпик для подзадачи.
    private void validateForEpic(SubTask task) throws EpicNotFoundException {
        if (!epics.containsKey(task.getEpicId())) {
            throw new EpicNotFoundException("Подзадача не относится к существующим Эпикам");
        }
    }

    // Валидатор, проверяющий наличие в менеджере задачи по ее ID.
    private void validateId(int id, List<Task> list) throws TaskNotFoundException {
        for (Task task : list) {
            if (task.getId() == id) {
                return;
            }
        }
        throw new TaskNotFoundException("Задача с таким ID не найдена");
    }

    // Геттер для глобального ID, нужен для загрузки из файла.
    public int getId() {
        return id;
    }

    // Сеттер для глобального ID нужен для загрузки из файла.
    public void setId(int id) {
        this.id = id;
    }
}

