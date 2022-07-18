package main.java.ru.yandex.practicum.task_tracker.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static main.java.ru.yandex.practicum.task_tracker.tasks.TaskStatus.*;
import static main.java.ru.yandex.practicum.task_tracker.tasks.TaskType.*;

public class Task {

    private final String name;
    private final String description;
    private TaskType type = TASK;
    private TaskStatus status = NEW;
    private LocalDateTime startTime;
    private int duration;
    private int id = -1;

    protected final static String DELIMITER = ",";
    protected final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    // Полная версия конструктора
    public Task(String name, String description, TaskStatus status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Конструктор для Эпиков и быстрого создания задач
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        String start = "Start time not defined";
        String end = "End time cannot be calculated";
        if (startTime != null) {
            start = startTime.format(FORMATTER);
        }
        if (getEndTime() != null) {
            end = getEndTime().format(FORMATTER);
        }
        return id + DELIMITER + type + DELIMITER + name + DELIMITER + status + DELIMITER
                + description + DELIMITER + start + DELIMITER + duration + DELIMITER + end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name)
                && description.equals(task.description) && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
