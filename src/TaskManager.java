import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int id = 0;
    /*
    Здесь исправил почти все имена, сократил "лапшу", перенес геттеры в конец. Появился метод для генератора id.
    Во всем разобрался, кроме нескольких моментов. Не знаю, нужны ли в Enum методы, дополнительные поля, или три
    константы - это все необходимое? Ну и про сокращение лапши через создание переменной Epic epic. Сделал методом
    тыка. Подставил переменную - работает. Но прям четкого и твердого понятия чувствую что нет. Мы создаем новую
    переменную и присваиваем ей значение той, что лежит в Хешмапе. И дальше оперируем с новой, а меняется получается
    и та, что в хешмапе. Не понимаю четко, почему так происходит. А догадки выставлять за истину побаиваюсь. Из-за
    того, что обе хранят ссылку на одну область, где и лежат все данные?
    Ох и расписал... Вообще не знаю, можно ли в комментариях расписывать вопросы и мнения. Слышал другие так делают,
    а я думал что комментарии в этих проектах как и в работе должны только пояснять код.*/

    // Удаление всех задач
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

    // Создание новой задачи
    public void createTask(Task task) {
        if (task != null) {
            int id = idGenerator();
            task.setId(id);
            tasks.put(id, task);
        }
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            int id = idGenerator();
            epic.setId(id);
            epic.setStatus(Status.NEW);   // Новый эпик при создании не содержит подзадач, поэтому имеет статус NEW
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
    public void removeTask(int id) {
        tasks.remove(id);
    }

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

    public void removeEpic(int id) {
        for (int i : subTasks.keySet()) { // Удаление всех подзадач, которые относятся к удаляемому эпику
            if (subTasks.get(i).getEpicId() == id) {
                subTasks.remove(i);
            }
        }
        epics.remove(id);
    }

    // Получение списка всех задач
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    // Получение задачи по индентификатору
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    // Полученмие списка всех подзадач Эпика
    public ArrayList<SubTask> getSubTasksFromEpic(int id) {
        Epic epic = epics.get(id);
        return epic.tasks;
    }

    // Генератор ID
    public int idGenerator() {
        return id++;
    }
}
