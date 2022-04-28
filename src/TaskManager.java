import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int idGenerator = 0;

    // 1. Получение списка всех задач
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

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

    // 3. Получение задачи по индентификатору
    public Task getTaskByID (int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskByID (int id) {
        return subTasks.get(id);
    }

    public Epic getEpicTaskByID (int id) {
        return epics.get(id);
    }

    // 4. Создание новой задачи
    public void createTask (Task task) {
        if (task != null) {
            task.setId(idGenerator);
            tasks.put(idGenerator++, task);
        }
    }

    public void createEpicTask (Epic epic) {
        if (epic != null) {
            epic.setId(idGenerator);
            epic.setStatus("NEW");   // Новый эпик при создании не содержит подзадач, поэтому имеет статус NEW
            epics.put(idGenerator++, epic);
        }
    }

    public void createSubTask (SubTask subTask) {
        if (subTask != null) {
            subTask.setId(idGenerator);
            subTasks.put(idGenerator++, subTask);
            int id = subTask.getEpicId();    // id Эпика, в рамках которого существует создаваемая подзадача
            epics.get(id).tasks.add(subTask); // добавление подзадачи в список эпика
            switch (subTask.getStatus()) { //при создании подзадачи может поменяться статус эпика
                case "NEW":
                    if (epics.get(id).getStatus().equals("DONE")) {
                        epics.get(id).setStatus("IN_PROGRESS");
                    }
                    break;
                case "IN_PROGRESS":
                    epics.get(id).setStatus("IN_PROGRESS");
                    break;
                case "DONE":
                    int counter = 0; // считаем количество задач со статусом DONE в списке эпика
                    for (SubTask task : epics.get(id).tasks) {
                        if (task.getStatus().equals("DONE")) {
                            counter++;
                        }
                    }
                    if (counter == epics.get(id).tasks.size()) {
                        epics.get(id).setStatus("DONE"); // если все задачи "DONE", меняем статус эпика на DONE
                    } else {
                        epics.get(id).setStatus("IN_PROGRESS"); // в противном случаем эпик будет IN_PROGRESS
                    }
                    break;
            }
        }
    }

    // 5. Обновление существующей задачи
    public void updateTask (Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
                tasks.put(id, task);
        }
    }

    public void updateSubTask (SubTask subTask) {
        int id = subTask.getId();
        if (subTasks.containsKey(id)) {
                subTasks.put(id, subTask);
                int epicId = subTask.getEpicId(); // id Эпика, в рамках которого существует обновляемая подзадача
                epics.get(epicId).tasks.removeIf(task -> task.getId() == subTask.getId());// удаление старой версии подзадачи
                epics.get(epicId).tasks.add(subTask); // добавление новой версии подзадачи в список эпика
            switch (subTask.getStatus()) {
                case "NEW":
                    if (epics.get(epicId).getStatus().equals("DONE")) {
                        epics.get(epicId).setStatus("IN_PROGRESS");
                    }
                    break;
                case "IN_PROGRESS":
                    epics.get(epicId).setStatus("IN_PROGRESS");
                    break;
                case "DONE":
                    int counter = 0; // считаем количество задач со статусом DONE в списке эпика
                    for (SubTask task : epics.get(epicId).tasks) {
                        if (task.getStatus().equals("DONE")) {
                            counter++;
                        }
                    }
                    if (counter == epics.get(epicId).tasks.size()) {
                        epics.get(epicId).setStatus("DONE"); // если все задачи "DONE", меняем статус эпика на DONE
                    } else {
                        epics.get(epicId).setStatus("IN_PROGRESS"); // в противном случаем эпик будет IN_PROGRESS
                    }
                    break;
            }
        }
    }

    public void updateEpicTask (Epic epic) {
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
    public void removeTaskByID (int id) {
        tasks.remove(id);
    }

    public void removeSubTaskByID (int id) {
        int i = subTasks.get(id).getEpicId(); //получаем идентификатор эпика, к которому относится удаляемая задача
        epics.get(i).tasks.removeIf(task -> task.getId() == subTasks.get(id).getId());// удалеяем подзадачу из эпика
        int counterNEW = 0;
        int counterDONE = 0;// считаем количество задач со статусом DONE и NEW в эпике из которого удалили подзадачу
        for (SubTask task : epics.get(i).tasks) {
            if (task.getStatus().equals("DONE")) {
                counterDONE++;
            } else if (task.getStatus().equals("NEW")) {
                counterNEW++;
            }
        } // Меняем статус эпика после удаления задачи:
        if (counterDONE == epics.get(i).tasks.size()) {
            epics.get(i).setStatus("DONE"); //если все оставшиеся подзадачи имеют статус DONE, эпику ставим DONE
        } else if (counterNEW == epics.get(i).tasks.size()){
            epics.get(i).setStatus("NEW"); //если все оставшиеся подзадачи имеют статус NEW, эпику ставим NEW
        } else {
            epics.get(i).setStatus("IN_PROGRESS"); //в противном случае - IN_PROGRESS
        }
        subTasks.remove(id);
    }

    public void removeEpicTaskByID (int id) {
       for (int i : subTasks.keySet()) { // Удаление всех подзадач, которые относятся к удаляемому эпику
           if (subTasks.get(i).getEpicId() == id) {
               subTasks.remove(i);
           }
       }
        epics.remove(id);
    }

    // 7. Полученмие списка всех подзадач Эпика
    public ArrayList<SubTask> getSubTasksFromEpic (int id) {
        Epic epic = epics.get(id);
        return epic.tasks;
    }
}
