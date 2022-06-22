package task_managers;

import history_managers.HistoryManager;
import misc.Managers;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    // Во всех эпиках очишаются списки подзадач
    @Override
    public void deleteAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
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
        subTasks.clear();
    }

    // Создание новой задачи
    @Override
    public void addTask(Task task) {
        if (task != null) {
            int id = generateId();
            task.setId(id);
            tasks.put(id, task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            int id = generateId();
            epic.setId(id);
            epic.setStatus(Status.NEW);
            epics.put(id, epic);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            int id = generateId();
            subTask.setId(id);
            subTasks.put(id, subTask);
            int epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);
            List<SubTask> subTasks = epic.getSubTasks();
            subTasks.add(subTask);
            setEpicStatus(subTask, epic);
        }
    }

    // Обновление существующей задачи
    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (subTasks.containsKey(id)) {
            subTasks.put(id, subTask);
            int epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);
            List<SubTask> subTasks = epic.getSubTasks();
            subTasks.removeIf(task -> task.getId() == subTask.getId());
            subTasks.add(subTask);
            setEpicStatus(subTask, epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epic.setStatus(Status.NEW);
            epics.put(id, epic);
            for (int i : subTasks.keySet()) {
                if (subTasks.get(i).getEpicId() == id) {
                    subTasks.remove(i);
                }
            }
        }
    }

    // Удаление задачи по идентификатору
    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        historyManager.remove(id);
        int i = subTasks.get(id).getEpicId();
        Epic epic = epics.get(i);
        List<SubTask> subTasks = epic.getSubTasks();
        subTasks.removeIf(task -> task.getId() == subTasks.get(id).getId());
        int counterNew = 0;
        int counterDone = 0;
        for (SubTask task : subTasks) {
            if (task.getStatus() == Status.DONE) {
                counterDone++;
            } else if (task.getStatus() == Status.NEW) {
                counterNew++;
            }
        }
        if (counterDone == subTasks.size()) {
            epic.setStatus(Status.DONE);
        } else if (counterNew == subTasks.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        subTasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic == null) {
            return;
        }
        historyManager.remove(id);
        for (SubTask subTask : removedEpic.getSubTasks()) {
            subTasks.remove(subTask.getId());
            historyManager.remove(subTask.getId());
        }
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

    // Получение задачи по индентификатору
    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    // Полученмие списка всех подзадач Эпика
    @Override
    public List<SubTask> getSubTasksFromEpic(int id) {
        Epic epic = epics.get(id);
        return epic.getSubTasks();
    }

    // Генератор ID
    private int generateId() {
        return id++;
    }

    // Просмотр последний 10 задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void setEpicStatus(SubTask subTask, Epic epic) {
        List<SubTask> subTasks = epic.getSubTasks();
        switch (subTask.getStatus()) {
            case NEW:
                if (epic.getStatus() == Status.DONE) {
                    epic.setStatus(Status.IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                epic.setStatus(Status.IN_PROGRESS);
                break;
            case DONE:
                int counter = 0;
                for (SubTask task : subTasks) {
                    if (task.getStatus() == Status.DONE) {
                        counter++;
                    }
                }
                if (counter == subTasks.size()) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
                break;
        }
    }
}
