public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task0 = new Task("Почта", "Забрать посылку", "NEW");
        Task task1 = new Task("Рынок", "Купить лук", "NEW");
        Task task2 = new Task("Пробежка", "10 километров", "NEW");
        Epic task3 = new Epic("Настолки", "Продать, купить, и поиграть");
        Epic task4 = new Epic("Кухня", "большая уборка");
        SubTask task5 = new SubTask("Покорение Марса", "собрать людей", "NEW", 3);
        SubTask task6 = new SubTask("Корни", "продать", "NEW", 3);
        SubTask task7 = new SubTask("Агрикола", "купить", "NEW", 3);
        SubTask task8 = new SubTask("Посуда", "вымыть тщательно", "NEW", 4);


        taskManager.createTask(task0);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpicTask(task3);
        taskManager.createEpicTask(task4);
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

        Task task9 = new Task("Почта", "Забрать посылку", "IN_PROGRESS");
        task9.setId(0);
        taskManager.updateTask(task9);
        SubTask task10 = new SubTask("Покорение Марса", "отличная была партия!", "DONE", 3);
        task10.setId(5);
        taskManager.updateSubTask(task10);
        Epic task11 = new Epic("Спальня", "большая уборка теперь в спальне!");
        task11.setId(4);
        taskManager.updateEpicTask(task11);

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
