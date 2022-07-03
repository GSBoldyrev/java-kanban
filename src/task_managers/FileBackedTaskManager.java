package task_managers;

import exceptions.ManagerSaveException;
import history_managers.HistoryManager;
import tasks.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    File data;
    protected final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    public FileBackedTaskManager(File data) {
        this.data = data;
    }

    // Статический метод, создает менеджер из файла .CSV
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<String> StringsFromFile = new ArrayList<>();
        try (FileReader fileReader = new FileReader(file); BufferedReader buffer = new BufferedReader(fileReader)) {
            while (buffer.ready()) {
                StringsFromFile.add(buffer.readLine());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        int size = StringsFromFile.size();
        // Если в файле всего одна строка, значит менеджер пустой.
        if (size == 1) {
            return manager;
            // Если предпоследняя строка в файле пустая, значит в менеджере не пустая история вызовов.
        } else if (StringsFromFile.get(size - 2).isBlank()) {
            List<Integer> history = getHistoryFromString(StringsFromFile.get(size - 1));
            for (int i = 1; i < (size - 2); i++) {
                Task task = fromString(StringsFromFile.get(i));
                if (task.getId() > manager.getId()) {
                    manager.setId(task.getId());
                }
                putTask(task, manager);
                if (history.contains(task.getId())) {
                    manager.historyManager.add(task);
                }
            }
        } else {
            for (int i = 1; i < size; i++) {
                Task task = fromString(StringsFromFile.get(i));
                putTask(task, manager);
            }
        }
        // Все Эпики наполняются подзадачами, получают статус и тайминг.
        for (SubTask subtask : manager.subTasks.values()) {
            int epicId = subtask.getEpicId();
            manager.getSubTasksFromEpic(epicId).add(subtask);
        }
        for (Epic epic : manager.epics.values()) {
            manager.setEpicTimes(epic);
        }
        return manager;
    }

    // Вспомогательный метод добавления задачи в соответствующую таблицу менеджера.
    private static void putTask(Task task, FileBackedTaskManager manager) {
        if (task instanceof SubTask) {
            manager.subTasks.put(task.getId(), (SubTask) task);
        } else if (task instanceof Epic) {
            manager.epics.put(task.getId(), (Epic) task);
        } else {
            manager.tasks.put(task.getId(), task);
        }
    }

    // Метод сохранения менеджера в файл формата .CSV
    private void save() {
        try (FileWriter fileWriter = new FileWriter(data)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic\n");
            for (Task task : getAllTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Task epic : getAllEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (Task subtask : getAllSubTasks()) {
                fileWriter.write(subtask.toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(toString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + e.getMessage());
        }
    }

    // Возвращает объект Task из строкового представления
    private static Task fromString(String value) {
        String[] fields = value.split(",");
        switch (fields[1]) {
            case "TASK":
                Task task = new Task(fields[2], fields[4]);
                task.setStatus(TaskStatus.valueOf(fields[3]));
                task.setId(Integer.parseInt(fields[0]));
                if (fields[5].equals("Start time not defined")) {
                    task.setStartTime(null);
                } else {
                    task.setStartTime(LocalDateTime.parse(fields[5], FORMATTER));
                }
                task.setDuration(Integer.parseInt(fields[6]));
                return task;
            case "EPIC":
                Task epic = new Epic(fields[2], fields[4]);
                epic.setStatus(TaskStatus.valueOf(fields[3]));
                epic.setId(Integer.parseInt(fields[0]));
                return epic;
            case "SUBTASK":
                Task subtask = new SubTask(fields[2], fields[4], Integer.parseInt(fields[8]));
                subtask.setStatus(TaskStatus.valueOf(fields[3]));
                subtask.setId(Integer.parseInt(fields[0]));
                if (fields[5].equals("Start time not defined")) {
                    subtask.setStartTime(null);
                } else {
                    subtask.setStartTime(LocalDateTime.parse(fields[5], FORMATTER));
                }
                subtask.setDuration(Integer.parseInt(fields[6]));
                return subtask;
        }
        return null;
    }

    // Метод представления истории вызовов в строковом формате.
    private static String toString(HistoryManager manager) {
        StringBuilder history = new StringBuilder();
        for (Task task : manager.getHistory()) {
            history.append(",").append(task.getId());
        }
        history.delete(0, 1);
        return history.toString();
    }

    // Метод возвращает список ID задач из истории вызовов из строкового представления.
    private static List<Integer> getHistoryFromString(String value) {
        String[] tasksId = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String str : tasksId) {
            history.add(Integer.parseInt(str));
        }
        return history;
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    // Во всех эпиках очищаются списки подзадач
    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    // При удалении эпиков также удаляются и подзадачи
    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    // Создание новой задачи
    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        int id = super.addSubTask(subTask);
        save();
        return id;
    }

    // Обновление существующей задачи
    @Override
    public int updateTask(Task task) {
        int id = super.updateTask(task);
        save();
        return id;
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        int id = super.updateSubTask(subTask);
        save();
        return id;
    }

    @Override
    public int updateEpic(Epic epic) {
        int id = super.updateEpic(epic);
        save();
        return id;
    }

    // Удаление задачи по идентификатору
    @Override
    public Task removeTask(int id) {
        Task task = super.removeTask(id);
        save();
        return task;
    }

    @Override
    public Task removeSubTask(int id) {
        Task task = super.removeSubTask(id);
        save();
        return task;
    }

    @Override
    public Task removeEpic(int id) {
        Task task = super.removeEpic(id);
        save();
        return task;
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }
}
