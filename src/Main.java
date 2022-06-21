import misc.Managers;
import taskManagers.FileBackedTasksManager;
import taskManagers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    /* Привет!
    * У меня тут выдалась просто жуткая неделька, работы было столько, что компьютер даже включить времени не было...
    * Выпал из обучения это мягко сказано. Только-только вот все наладилось, схватился за задание. Показалось самым
    * интересным из тех, что были. Но из-за загруза на работе и переживаний про дедлайны я тут наверное столько ошибок
    * навертел, столько моментов упустил и не заметил... Вообще не было времени углубиться, почитать дополнительные
    * материалы... А отставать очень не хочется, потому что не знаю, что по работе тут дальше будет. Надеюсь, что не
    * найдешь тут прям уж совсем жутких ошибок.
    * P.S. Все тесты делал в этом мейне, закомментил секциями и прогонял много раз. Вроде разрешали не создавать второй
    * мейн. */

    public static void main(String[] args) throws IOException {

        // Так я создал файл. Читал в слаке, что люди создавали файл каждый раз в конструкторе. Но это ведь не логично
        // как-то. Должен быть один файл которым программа будет пользоваться и все. Разве нет?
        // Path testFile = Files.createFile(Paths.get("C:\\Users\\maver\\IdeaProjects\\java-kanban\\out", "data.csv"));

        File data = new File("C:\\Users\\maver\\IdeaProjects\\java-kanban\\out\\data.csv");

        // Менеджер для проверки записи в файл
        // TaskManager fileBackedTasksManager = Managers.getFileBacked(data);

        // Менеджер для проверки восстановления из файла
        // FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(data);

/*      // ПРОВЕРКА ЗАГРУЗКИ МЕНЕДЖЕРА ИЗ ФАЙЛА
        System.out.println(manager.getHistory());
        System.out.println("");
        System.out.println(manager.getAllEpics());
        System.out.println("");
        System.out.println(manager.getAllSubTasks());
        System.out.println("");
        System.out.println(manager.getAllTasks());*/

/*      // СОЗДАНИЕ ЗАДАЧ
        Task task0 = new Task("Почта", "Забрать посылку", Status.NEW);
        Task task1 = new Task("Рынок", "Купить лук", Status.NEW);
        Epic task2 = new Epic("Настолки", "Продать, купить, и поиграть");
        Epic task3 = new Epic("Кухня", "большая уборка");
        SubTask task4 = new SubTask("Покорение Марса", "собрать людей", Status.NEW, 2);
        SubTask task5 = new SubTask("Корни", "продать", Status.NEW, 2);
        SubTask task6 = new SubTask("Агрикола", "купить", Status.NEW, 2);*/

/*      // ПРОВЕРКА ДОБАВЛЕНИЯ ЗАДАЧ В НОВЫЙ МЕНЕДЖЕР И В ФАЙЛ
        fileBackedTasksManager.addTask(task0);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addEpic(task2);
        fileBackedTasksManager.addEpic(task3);
        fileBackedTasksManager.addSubTask(task4);
        fileBackedTasksManager.addSubTask(task5);
        fileBackedTasksManager.addSubTask(task6);*/

/*
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
        System.out.println();*/

/*      // ПРОВЕРКА ИСТОРИИ И ФАЙЛА ПРИ УДАЛЕНИИ ЗАДАЧ
        fileBackedTasksManager.removeTask(1);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println();

        fileBackedTasksManager.removeEpic(2);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println();*/

    }
}
