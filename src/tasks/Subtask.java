package tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
        this.epicId = -1;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}