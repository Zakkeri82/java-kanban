package tasks;

import enums.TypeTask;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
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