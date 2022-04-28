import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int id = 0;

    // 2. Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (int i : epics.keySet()) { // Во всех эпиках очишаются списки подзадач
            epics.get(i).tasks.clear();
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear(); // При удалении эпиков также удаляются и подзадачи
    }

    // 4. Создание новой задачи
    public void createTask(Task task) {
        if (task != null) {
            int id = idGenerator();
            task.setId(id);
            tasks.put(id, task);
        }
    }

    public void createEpicTask(Epic epic) {
        if (epic != null) {
            int id = idGenerator();
            epic.setId(id);
            epic.setStatus("NEW");   // Новый эпик при создании не содержит подзадач, поэтому имеет статус NEW
            epics.put(id, epic);
        }
    }

    public void createSubTask(SubTask subTask) {
        if (subTask != null) {
            int id = idGenerator();
            subTask.setId(id);
            subTasks.put(id, subTask);
            int i = subTask.getEpicId();// id Эпика, в рамках которого существует создаваемая подзадача
            Epic epic = epics.get(i);
            epic.tasks.add(subTask); // добавление подзадачи в список эпика
            switch (subTask.getStatus()) { //при создании подзадачи может поменяться статус эпика
                case "NEW":
                    if (epic.getStatus().equals("DONE")) {
                        epic.setStatus("IN_PROGRESS");
                    }
                    break;
                case "IN_PROGRESS":
                    epic.setStatus("IN_PROGRESS");
                    break;
                case "DONE":
                    int counter = 0; // считаем количество задач со статусом DONE в списке эпика
                    for (SubTask task : epic.tasks) {
                        if (task.getStatus().equals("DONE")) {
                            counter++;
                        }
                    }
                    if (counter == epic.tasks.size()) {
                        epic.setStatus("DONE"); // если все задачи "DONE", меняем статус эпика на DONE
                    } else {
                        epic.setStatus("IN_PROGRESS"); // в противном случаем эпик будет IN_PROGRESS
                    }
                    break;
            }
        }
    }

    // 5. Обновление существующей задачи
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (subTasks.containsKey(id)) {
            subTasks.put(id, subTask);
            int epicId = subTask.getEpicId(); // id Эпика, в рамках которого существует обновляемая подзадача
            Epic epic = epics.get(epicId);
            epic.tasks.removeIf(task -> task.getId() == subTask.getId());// удаление старой версии подзадачи
            epic.tasks.add(subTask); // добавление новой версии подзадачи в список эпика
            switch (subTask.getStatus()) {
                case "NEW":
                    if (epic.getStatus().equals("DONE")) {
                        epic.setStatus("IN_PROGRESS");
                    }
                    break;
                case "IN_PROGRESS":
                    epic.setStatus("IN_PROGRESS");
                    break;
                case "DONE":
                    int counter = 0; // считаем количество задач со статусом DONE в списке эпика
                    for (SubTask task : epic.tasks) {
                        if (task.getStatus().equals("DONE")) {
                            counter++;
                        }
                    }
                    if (counter == epic.tasks.size()) {
                        epic.setStatus("DONE"); // если все задачи "DONE", меняем статус эпика на DONE
                    } else {
                        epic.setStatus("IN_PROGRESS"); // в противном случаем эпик будет IN_PROGRESS
                    }
                    break;
            }
        }
    }

    public void updateEpicTask(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epic.setStatus("NEW");
            epics.put(id, epic);
            for (int i : subTasks.keySet()) {
                if (subTasks.get(i).getEpicId() == id) {
                    subTasks.remove(i);
                }
            }
        }
    }

    // 6. Удаление задачи по идентификатору
    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubTask(int id) {
        int i = subTasks.get(id).getEpicId(); //получаем идентификатор эпика, к которому относится удаляемая задача
        Epic epic = epics.get(i);
        epic.tasks.removeIf(task -> task.getId() == subTasks.get(id).getId());// удалеяем подзадачу из эпика
        int counterNEW = 0;
        int counterDONE = 0;// считаем количество задач со статусом DONE и NEW в эпике из которого удалили подзадачу
        for (SubTask task : epic.tasks) {
            if (task.getStatus().equals("DONE")) {
                counterDONE++;
            } else if (task.getStatus().equals("NEW")) {
                counterNEW++;
            }
        } // Меняем статус эпика после удаления задачи:
        if (counterDONE == epic.tasks.size()) {
            epic.setStatus("DONE"); //если все оставшиеся подзадачи имеют статус DONE, эпику ставим DONE
        } else if (counterNEW == epic.tasks.size()) {
            epic.setStatus("NEW"); //если все оставшиеся подзадачи имеют статус NEW, эпику ставим NEW
        } else {
            epic.setStatus("IN_PROGRESS"); //в противном случае - IN_PROGRESS
        }
        subTasks.remove(id);
    }

    public void removeEpic(int id) {
        for (int i : subTasks.keySet()) { // Удаление всех подзадач, которые относятся к удаляемому эпику
            if (subTasks.get(i).getEpicId() == id) {
                subTasks.remove(i);
            }
        }
        epics.remove(id);
    }

    // 1. Получение списка всех задач
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    // 3. Получение задачи по индентификатору
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    // 7. Полученмие списка всех подзадач Эпика
    public ArrayList<SubTask> getSubTasksFromEpic(int id) {
        Epic epic = epics.get(id);
        return epic.tasks;
    }

    // Генератор ID
    public int idGenerator() {
        return id++;
    }
}
