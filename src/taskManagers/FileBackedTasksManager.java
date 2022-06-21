package taskManagers;

import historyManagers.HistoryManager;
import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    File data;

    public FileBackedTasksManager(File data) {
        this.data = data;
    }

    // Метод восстанавливает менеджер из файла.
    // Здесь вроде все понятно, но, как и всегда, не уверен в реализации.
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        List<String> fromFile = new ArrayList<>(); // список для хранения каждой отдельной строки из файла .CSV
        try (FileReader fileReader = new FileReader(file); BufferedReader buffer = new BufferedReader(fileReader)) {
            while (buffer.ready()) {
                fromFile.add(buffer.readLine()); // читаем каждую строку из файла
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int size = fromFile.size(); // переменная хранит количество строк в файле.
        if (fromFile.get(size - 2).isBlank()) { //если предпоследняя строка файла пустая, значит имеется история вызовов
            List<Integer> history = getHistoryFromString(fromFile.get(size - 1)); // список ID задач из истории вызовов.
            for (int i = 1; i < (size - 2); i++) { // задачи в файле во всех строках, кроме 1-й и последних 2-х.
                Task task = fromString(fromFile.get(i)); // преобразуем строку в задачу.
                putTask(task, manager);
                if (history.contains(task.getId())) { // добавляем задачу в историю вызовов.
                    manager.getHistoryManager().add(task);
                }
            }
        } else { //если предпоследняя строка не пустая, значит история вызовов отсутствует.
            for (int i = 1; i < (size-1); i++) {
                Task task = fromString(fromFile.get(i)); // преобразуем строку в задачу.
                putTask(task, manager);
            }
        }
        for (SubTask subtask : manager.getSubTasks().values()) { // восстанавливаем связь между эпиками и подзадачами.
            int epicId = subtask.getEpicId();
            Epic epic = manager.getEpic(epicId);
            epic.getSubTasks().add(subtask);
        }
        return manager;
    }

    // Вспомогательный метод добавления задачи в соответствующую таблицу менеджера.
    private static void putTask(Task task, FileBackedTasksManager manager) {
        if (task instanceof SubTask) { // определяем тип задачи и помещаем в соответствующую таблицу.
            manager.getSubTasks().put(task.getId(), (SubTask) task);
        } else if (task instanceof Epic) {
            manager.getEpics().put(task.getId(), (Epic) task);
        } else {
            manager.getTasks().put(task.getId(), task);
        }
    }

    // сохраняет все задачи менеджерa и историю просмотров в файл .CSV
    // Не расскажешь, почему при каждом вызове метода файл перезаписывается по новой? Это вроде как раз хорошо, просто
    // не понимаю, почему так, ведь никакого метода для удаления старой информации не было...
    private void save() {
        List<Task> tasks = new ArrayList<>(this.getAllTasks());
        tasks.addAll(this.getAllSubTasks());
        tasks.addAll(this.getAllEpics());
        try (FileWriter fileWriter = new FileWriter(data)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : tasks) {
                fileWriter.write(toString(task) + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(toString(super.getHistoryManager()));
        } catch (IOException e) {
            System.out.println("Unknown error");
        }
    }

    // Преобразует объект Task в строку. Все поля объекта перечисляются через запятую без пробелов.
    // Что-то я совсем тут в статиках запутался...
    private String toString(Task task) {
        String value = String.format("%d,%s,%s,%s,%s,",
                task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription());
        if (task instanceof SubTask) {
            value = value + ((SubTask) task).getEpicId();
        }
        return value;
    }

    // Возвращает объект Task из строкового представления
    // Здесь как-то все громоздко получилось, но никак не могу придумать что-то получше...
    private static Task fromString(String value) {
        String[] fields = value.split(",");
        Status status = null;
        switch (fields[3]) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            case "DONE":
                status = Status.DONE;
                break;
        }
        switch (fields[1]) {
            case "TASK":
                Task task = new Task(fields[2], fields[4]);
                task.setStatus(status);
                task.setId(Integer.parseInt(fields[0]));
                return task;
            case "EPIC":
                Task epic = new Epic(fields[2], fields[4]);
                epic.setStatus(status);
                epic.setId(Integer.parseInt(fields[0]));
                return epic;
            case "SUBTASK":
                Task subtask = new SubTask(fields[2], fields[4], Integer.parseInt(fields[5]));
                subtask.setStatus(status);
                subtask.setId(Integer.parseInt(fields[0]));
                return subtask;
        }
        return null;
    }

    // Преобразует объект HistoryManager в строку. Строка состоит из ID задач, перечисленных через запятую.
    // Не понял, зачем попросили делать этот метод статическим. И не уверен, что нашел лучший способ расставить запятые.
    private static String toString(HistoryManager manager) {
        StringBuilder history = new StringBuilder();
        for (Task task : manager.getHistory()) {
            history.append(",").append(task.getId());
        }
        history.delete(0, 1);
        return history.toString();
    }

    // Возвращает список ID задач, составляющих историю просмотров.
    // Опять же, зачем статический? И в ТЗ метод называется тоже fromString, но как ужиться двум методам с одинаковой
    // сигнатурой? Переименовал... Но может методы эти вообще в другом классе лежать должны? =/
    private static List<Integer> getHistoryFromString(String value) {
        String[] tasksId = value.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : tasksId) {
            idList.add(Integer.parseInt(s));
        }
        return idList;
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() throws IOException {
        super.deleteAllTasks();
        save();
    }

    // Во всех эпиках очищаются списки подзадач
    @Override
    public void deleteAllSubTasks() throws IOException {
        super.deleteAllSubTasks();
        save();
    }

    // При удалении эпиков также удаляются и подзадачи
    @Override
    public void deleteAllEpics() throws IOException {
        super.deleteAllEpics();
        save();
    }

    // Создание новой задачи
    @Override
    public void addTask(Task task) throws IOException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) throws IOException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) throws IOException {
        super.addSubTask(subTask);
        save();
    }

    // Обновление существующей задачи
    @Override
    public void updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) throws IOException {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws IOException {
        super.updateEpic(epic);
        save();
    }

    // Удаление задачи по идентификатору
    @Override
    public void removeTask(int id) throws IOException {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) throws IOException {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) throws IOException {
        super.removeEpic(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }
}
