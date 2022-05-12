public class Main {

    public static void main(String[] args) {
        /*
        Клево! Спасибо тебе как всегда, за столь развернутое ревью.
        Сидел и скрипел мозгами насчет магической десятки. Доскрипелся до того, что решил вынести в константу. Красиво
        вроде и как у взрослых получилось.
        Использовать интерфейс List в данном случае - задел на будущее, или просто хорошая практика создавать переменные
        типа интерфейса, а не класса, его имплементирующего?
        По поводу генератора ID. Идея не давала сделать его мне приватным, потому что он публичный в интерфейсе. Тут я
        и понял, что в интерфейсе то он вроде как и не нужен вовсе. А если нужен, разве мы можем тогда сделать его
        приватным? Вроде нет.
        Ну а получение задач списком а не картой - отдельное тебе спасибо! теперь умею переносить данные из карты в
        список одной строкой))
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

        inMemoryTaskManager.addTask(task0);
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addEpic(task3);
        inMemoryTaskManager.addEpic(task4);
        inMemoryTaskManager.addSubTask(task5);
        inMemoryTaskManager.addSubTask(task6);
        inMemoryTaskManager.addSubTask(task7);
        inMemoryTaskManager.addSubTask(task8);

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(4);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getSubTask(5);
        inMemoryTaskManager.getSubTask(8);
        inMemoryTaskManager.getEpic(4);

        System.out.println(inMemoryTaskManager.getHistory());
    }
}
