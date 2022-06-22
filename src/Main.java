import task_managers.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

public class Main {

    /* Привет!
    Вроде разобрался с исключениями. Метод toString действительно гораздо лучше смотрится в тасках. А вот fromString
    может тоже нужно в таск переместить? Хотя непонятно тогда как решить проблему того, что он должен быть разным для
    каждого из трех типов. Остальные ошибки исправил, продумал. Убрал лишние геттеры, поставив protected для полей.
    Ведь в любом случае из файла загружать задачи надо напрямую в таблицы, поэтому к ним нужен доступ.
    Пока вроде все, жду следующих пометок.
 */

    public static void main(String[] args) throws IOException {

        // Path testFile = Files.createFile(Paths.get("C:\\Users\\maver\\IdeaProjects\\java-kanban\\out", "data.csv"));

        File data = new File("C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\data.csv");

        // Менеджер для проверки записи в файл
        // TaskManager fileBackedTasksManager = Managers.getDefault(data);

        // Менеджер для проверки восстановления из файла

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(data);

      // ПРОВЕРКА ЗАГРУЗКИ МЕНЕДЖЕРА ИЗ ФАЙЛА
        System.out.println(manager.getHistory());
        System.out.println("");
        System.out.println(manager.getAllEpics());
        System.out.println("");
        System.out.println(manager.getAllSubTasks());
        System.out.println("");
        System.out.println(manager.getAllTasks());

      /*  // СОЗДАНИЕ ЗАДАЧ
        Task task0 = new Task("Почта", "Забрать посылку", Status.NEW);
        Task task1 = new Task("Рынок", "Купить лук", Status.NEW);
        Epic task2 = new Epic("Настолки", "Продать, купить, и поиграть");
        Epic task3 = new Epic("Кухня", "большая уборка");
        SubTask task4 = new SubTask("Покорение Марса", "собрать людей", Status.NEW, 2);
        SubTask task5 = new SubTask("Корни", "продать", Status.NEW, 2);
        SubTask task6 = new SubTask("Агрикола", "купить", Status.NEW, 2);

        // ПРОВЕРКА ДОБАВЛЕНИЯ ЗАДАЧ В НОВЫЙ МЕНЕДЖЕР И В ФАЙЛ
        fileBackedTasksManager.addTask(task0);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addEpic(task2);
        fileBackedTasksManager.addEpic(task3);
        fileBackedTasksManager.addSubTask(task4);
        fileBackedTasksManager.addSubTask(task5);
        fileBackedTasksManager.addSubTask(task6);


        // ПРОВЕКРА СОХРАНЕНИЯ ИСТОРИИ
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(3);
        fileBackedTasksManager.getTask(0);
        fileBackedTasksManager.getSubTask(5);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println();

        fileBackedTasksManager.getTask(0);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getSubTask(4);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(3);
        fileBackedTasksManager.getTask(0);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println();

        // ПРОВЕРКА ИСТОРИИ И ФАЙЛА ПРИ УДАЛЕНИИ ЗАДАЧ
        fileBackedTasksManager.removeTask(1);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println();

        fileBackedTasksManager.removeEpic(2);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println();*/

    }
}
