package main.java.ru.yandex.practicum.task_tracker.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

import static main.java.ru.yandex.practicum.task_tracker.tasks.TaskType.SUBTASK;

public class SubTask extends Task {
    private int epicId;

    // Полная версия конструктора.
    public SubTask(String name, String description, TaskStatus status, int epicId, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        super.setType(SUBTASK);
        this.epicId = epicId;
    }

    // Короткая версия для тестов.
    public SubTask(String name, String description, int epicId) {
        super(name, description);
        super.setType(SUBTASK);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String start = "Start time not defined";
        String end = "End time cannot be calculated";
        if (getStartTime() != null) {
            start = getStartTime().format(FORMATTER);
        }
        if (getEndTime() != null) {
            end = getEndTime().format(FORMATTER);
        }
        return getId() + DELIMITER + getType() + DELIMITER + getName() + DELIMITER + getStatus()
                + DELIMITER + getDescription() + DELIMITER + start + DELIMITER + getDuration()
                + DELIMITER + end + DELIMITER + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
