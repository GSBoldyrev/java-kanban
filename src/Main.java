public class Main {

    public static void main(String[] args) {
        /*
        Привет!
        Такие странные ощущения, вроде все сделал по ТЗ, вроде все работает. Но кажется, будто что-то не так, уж больно
        быстро и просто все сделалось. Не на сто процентов уверен в том, какие методы должен содержать интерфейс
        TaskManager. Особенно с методом getHistory(). Получается он у нас в двух интерфейсах объявлен, это нормально?
        Но ведь если его не объявить в интерфейсе TaskManager, то мы не будем иметь к нему доступа при создании
        менеджера через getDefault.
        В общем, жду разноса :)
         */

        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task0 = new Task("Почта", "Забрать посылку", Status.NEW);
        Task task1 = new Task("Рынок", "Купить лук", Status.NEW);
        Task task2 = new Task("Пробежка", "10 километров", Status.NEW);
        Epic task3 = new Epic("Настолки", "Продать, купить, и поиграть");
        Epic task4 = new Epic("Кухня", "большая уборка");
        SubTask task5 = new SubTask("Покорение Марса", "собрать людей", Status.NEW, 3);
        SubTask task6 = new SubTask("Корни", "продать", Status.NEW, 3);
        SubTask task7 = new SubTask("Агрикола", "купить", Status.NEW, 3);
        SubTask task8 = new SubTask("Посуда", "вымыть тщательно", Status.NEW, 4);


        inMemoryTaskManager.createTask(task0);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createEpic(task3);
        inMemoryTaskManager.createEpic(task4);
        inMemoryTaskManager.createSubTask(task5);
        inMemoryTaskManager.createSubTask(task6);
        inMemoryTaskManager.createSubTask(task7);
        inMemoryTaskManager.createSubTask(task8);

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(4);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getSubTask(5);
        inMemoryTaskManager.getSubTask(8);
        inMemoryTaskManager.getEpic(4);

        System.out.println(inMemoryTaskManager.getHistory());





    }
}
