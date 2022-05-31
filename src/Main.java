import misc.Managers;
import taskManagers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        /*
        Да, работаю штурманом в море. Решил наконец вылезти на сушу и быть всегда с семьей, потому и учусь...
        Спасибо за замечания, с кодом конечно ты меня побаловал, но прежде чем его просто копировать, я разобрался.
        Как сам не догадался, что по списку подзадач эпика проходить гораздо удобнее! А по тестам в мейне, не знаю.
        Еще раз пересмотрел ТЗ - в тестах выполняю все пункты, которые спрашивают, ну а для себя делал еще больше.
        Главное - под конец. Я уже начал задумывать про разбивку по пакетам, но немного этого побаивался. Почитал
        рекомендации, документацию. Все что почерпнул - называть с маленькой буквы и в пакет помещать по смыслу. С
        обратным доменным именем я думаю в нашем случае не актуально ведь? Разбил вроде по пакетам так, как видел
        логичным, кроме класса Managers... Не додумался куда его засунуть и сделал пакет misc...
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

        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getSubTask(4);
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
