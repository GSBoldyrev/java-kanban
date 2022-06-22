package task_managers;

import exceptions.ManagerSaveException;
import history_managers.HistoryManager;
import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    File data;

    public FileBackedTaskManager(File data) {
        this.data = data;
    }

    // Не совсем понял, нужно ли в этом методе вообще ловить IOException, или только в Save()
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<String> fromFile = new ArrayList<>();
        try (FileReader fileReader = new FileReader(file); BufferedReader buffer = new BufferedReader(fileReader)) {
            while (buffer.ready()) {
                fromFile.add(buffer.readLine());
            }
        } catch (IOException e) {
            e.getMessage();
        }
        int size = fromFile.size();
        if (fromFile.get(size - 2).isBlank()) {
            List<Integer> history = getHistoryFromString(fromFile.get(size - 1));
            for (int i = 1; i < (size - 2); i++) {
                Task task = fromString(fromFile.get(i));
                putTask(task, manager);
                if (history.contains(task.getId())) {
                    manager.historyManager.add(task);
                }
            }
        } else {
            for (int i = 1; i < size; i++) {
                Task task = fromString(fromFile.get(i));
                putTask(task, manager);
            }
        }
        for (SubTask subtask : manager.subTasks.values()) {
            int epicId = subtask.getEpicId();
            Epic epic = manager.getEpic(epicId);
            epic.getSubTasks().add(subtask);
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

    // С исключением вроде разобрался.
    private void save() {
        try (FileWriter fileWriter = new FileWriter(data)) {
            fileWriter.write("id,type,name,status,description,epic\n");
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
                task.setStatus(Status.valueOf(fields[3]));
                task.setId(Integer.parseInt(fields[0]));
                return task;
            case "EPIC":
                Task epic = new Epic(fields[2], fields[4]);
                epic.setStatus(Status.valueOf(fields[3]));
                epic.setId(Integer.parseInt(fields[0]));
                return epic;
            case "SUBTASK":
                Task subtask = new SubTask(fields[2], fields[4], Integer.parseInt(fields[5]));
                subtask.setStatus(Status.valueOf(fields[3]));
                subtask.setId(Integer.parseInt(fields[0]));
                return subtask;
        }
        return null;
    }

    // А По ТЗ метод должен принимать именно HistoryManager...
    private static String toString(HistoryManager manager) {
        StringBuilder history = new StringBuilder();
        for (Task task : manager.getHistory()) {
            history.append(",").append(task.getId());
        }
        history.delete(0, 1);
        return history.toString();
    }

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
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    // Обновление существующей задачи
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    // Удаление задачи по идентификатору
    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
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
