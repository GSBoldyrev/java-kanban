public class Main {

    public static void main(String[] args) {
        /*
        Привет! Ну вот, я уже на работе ,посреди Атлантики. Пока что, здесь учиться даже легче, чем дома, никто не
        отвлекает :). Задание показалось очень интересным, но вот в двух методах возникли серьезные вопросы, которые
        я расписал в комментариях к ним. Еще метод на удаление узла вызывает сомнения, кажется, что можно было сделать
        лучше. В общем, как всегда, жду разноса!))
         */

        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task0 = new Task("Почта", "Забрать посылку", Status.NEW);
        Task task1 = new Task("Рынок", "Купить лук", Status.NEW);
        Epic task2 = new Epic("Настолки", "Продать, купить, и поиграть");
        Epic task3 = new Epic("Кухня", "большая уборка");
        SubTask task4 = new SubTask("Покорение Марса", "собрать людей", Status.NEW, 2);
        SubTask task5 = new SubTask("Корни", "продать", Status.NEW, 2);
        SubTask task6 = new SubTask("Агрикола", "купить", Status.NEW, 2);

        inMemoryTaskManager.addTask(task0);
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addEpic(task2);
        inMemoryTaskManager.addEpic(task3);
        inMemoryTaskManager.addSubTask(task4);
        inMemoryTaskManager.addSubTask(task5);
        inMemoryTaskManager.addSubTask(task6);

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getSubTask(5);
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubTask(4);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getTask(0);
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

        inMemoryTaskManager.removeTask(1);
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

        inMemoryTaskManager.removeEpic(2);
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

    }
}
