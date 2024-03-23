package tasks;

import enums.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String nameTask, String description, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, startTime, duration);
        this.epicId = -1;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SubTask;
    }
}