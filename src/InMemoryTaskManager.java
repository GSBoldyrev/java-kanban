import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (int i : epics.keySet()) { // Во всех эпиках очишаются списки подзадач
            epics.get(i).tasks.clear();
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear(); // При удалении эпиков также удаляются и подзадачи
    }

    // Создание новой задачи
    @Override
    public void createTask(Task task) {
        if (task != null) {
            int id = idGenerator();
            task.setId(id);
            tasks.put(id, task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            int id = idGenerator();
            epic.setId(id);
            epic.setStatus(Status.NEW);   // Новый эпик при создании не содержит подзадач, поэтому имеет статус NEW
            epics.put(id, epic);
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (subTask != null) {
            int id = idGenerator();
            subTask.setId(id);
            subTasks.put(id, subTask);
            int i = subTask.getEpicId();// id Эпика, в рамках которого существует создаваемая подзадача
            Epic epic = epics.get(i);
            epic.tasks.add(subTask); // добавление подзадачи в список эпика
            switch (subTask.getStatus()) { //при создании подзадачи может поменяться статус эпика
                case NEW:
                    if (epic.getStatus() == Status.DONE) {
                        epic.setStatus(Status.IN_PROGRESS);
                    }
                    break;
                case IN_PROGRESS:
                    epic.setStatus(Status.IN_PROGRESS);
                    break;
                case DONE:
                    int counter = 0; // считаем количество задач со статусом DONE в списке эпика
                    for (SubTask task : epic.tasks) {
                        if (task.getStatus() == Status.DONE) {
                            counter++;
                        }
                    }
                    if (counter == epic.tasks.size()) {
                        epic.setStatus(Status.DONE); // если все задачи "DONE", меняем статус эпика на DONE
                    } else {
                        epic.setStatus(Status.IN_PROGRESS); // в противном случаем эпик будет IN_PROGRESS
                    }
                    break;
            }
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
            int epicId = subTask.getEpicId(); // id Эпика, в рамках которого существует обновляемая подзадача
            Epic epic = epics.get(epicId);
            epic.tasks.removeIf(task -> task.getId() == subTask.getId());// удаление старой версии подзадачи
            epic.tasks.add(subTask); // добавление новой версии подзадачи в список эпика
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
                    int counter = 0; // считаем количество задач со статусом DONE в списке эпика
                    for (SubTask task : epic.tasks) {
                        if (task.getStatus() == Status.DONE) {
                            counter++;
                        }
                    }
                    if (counter == epic.tasks.size()) {
                        epic.setStatus(Status.DONE); // если все задачи "DONE", меняем статус эпика на DONE
                    } else {
                        epic.setStatus(Status.IN_PROGRESS); // в противном случаем эпик будет IN_PROGRESS
                    }
                    break;
            }
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
        tasks.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        int i = subTasks.get(id).getEpicId(); //получаем идентификатор эпика, к которому относится удаляемая задача
        Epic epic = epics.get(i);
        epic.tasks.removeIf(task -> task.getId() == subTasks.get(id).getId());// удалеяем подзадачу из эпика
        int counterNew = 0;
        int counterDone = 0;// считаем количество задач со статусом DONE и NEW в эпике из которого удалили подзадачу
        for (SubTask task : epic.tasks) {
            if (task.getStatus() == Status.DONE) {
                counterDone++;
            } else if (task.getStatus() == Status.NEW) {
                counterNew++;
            }
        } // Меняем статус эпика после удаления задачи:
        if (counterDone == epic.tasks.size()) {
            epic.setStatus(Status.DONE); //если все оставшиеся подзадачи имеют статус DONE, эпику ставим DONE
        } else if (counterNew == epic.tasks.size()) {
            epic.setStatus(Status.NEW); //если все оставшиеся подзадачи имеют статус NEW, эпику ставим NEW
        } else {
            epic.setStatus(Status.IN_PROGRESS); //в противном случае - IN_PROGRESS
        }
        subTasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        for (int i : subTasks.keySet()) { // Удаление всех подзадач, которые относятся к удаляемому эпику
            if (subTasks.get(i).getEpicId() == id) {
                subTasks.remove(i);
            }
        }
        epics.remove(id);
    }

    // Получение списка всех задач
    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
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
    public ArrayList<SubTask> getSubTasksFromEpic(int id) {
        Epic epic = epics.get(id);
        return epic.tasks;
    }

    // Генератор ID
    @Override
    public int idGenerator() {
        return id++;
    }

    // Просмотр последний 10 задач
    @Override
    public List<Task> getHistory() {
      return historyManager.getHistory();
    }



}

