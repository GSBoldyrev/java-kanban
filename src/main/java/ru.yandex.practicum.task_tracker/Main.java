package main.java.ru.yandex.practicum.task_tracker;

import main.java.ru.yandex.practicum.task_tracker.servers.HttpTaskServer;
import main.java.ru.yandex.practicum.task_tracker.servers.KVServer;

import java.io.IOException;

public class Main {

    /*
    * Снова привет!
    * Ох, ну ты и удивил меня своим ревью! Я конечно знал, что все тесты проходят и приложение работает как просят в
    * ТЗ, но... Но сомнений было очень много. А оказалось все не так плохо, спасибо! Про константы - еще один плюсик
    * к опыту, и проект теперь по пакетам красиво разбит! А еще ты меня четко выдрессировал перед отправкой проекта
    * в каждом классе нажимать CTRL + ALT + L =)) */

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer().start();
    }
}
