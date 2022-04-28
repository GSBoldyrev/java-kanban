public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        /*
        А я то подумал, что все тесты чисто для меня! Как я тут только не тестировал, и создавал и удалял, все методы
         прогнал по десять раз. Ну а перед отправкой на проверку все удалил, думал надо чтоб мейн чистый остался))
         Сейчас я тоже многое поудалял отсюда, оставил только то, что требовалось в ТЗ. А так проверил и equals, и
         все остальные методы. И главное! получилось с Enum! ))
         */
        Task task0 = new Task("Почта", "Забрать посылку", Status.NEW);
        Task task1 = new Task("Рынок", "Купить лук", Status.NEW);
        Task task2 = new Task("Пробежка", "10 километров", Status.NEW);
        Epic task3 = new Epic("Настолки", "Продать, купить, и поиграть");
        Epic task4 = new Epic("Кухня", "большая уборка");
        SubTask task5 = new SubTask("Покорение Марса", "собрать людей", Status.NEW, 3);
        SubTask task6 = new SubTask("Корни", "продать", Status.NEW, 3);
        SubTask task7 = new SubTask("Агрикола", "купить", Status.NEW, 3);
        SubTask task8 = new SubTask("Посуда", "вымыть тщательно", Status.NEW, 4);


        taskManager.createTask(task0);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(task3);
        taskManager.createEpic(task4);
        taskManager.createSubTask(task5);
        taskManager.createSubTask(task6);
        taskManager.createSubTask(task7);
        taskManager.createSubTask(task8);


        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
        System.out.println();
        System.out.println();

        Task task9 = new Task("Почта", "Забрать посылку", Status.IN_PROGRESS);
        task9.setId(0);
        taskManager.updateTask(task9);
        SubTask task10 = new SubTask("Покорение Марса", "отличная была партия!", Status.DONE, 3);
        task10.setId(5);
        taskManager.updateSubTask(task10);
        Epic task11 = new Epic("Спальня", "большая уборка теперь в спальне!");
        task11.setId(4);
        taskManager.updateEpic(task11);

        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
        System.out.println();
        System.out.println();

        taskManager.removeTask(1);
        taskManager.removeSubTask(5);
        taskManager.removeEpic(4);

        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
        System.out.println();
        System.out.println();
    }
}
