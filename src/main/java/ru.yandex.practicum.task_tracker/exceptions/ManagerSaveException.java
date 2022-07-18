package main.java.ru.yandex.practicum.task_tracker.exceptions;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }
}
