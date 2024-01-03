import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Subtask> subtasks;

    private Status status;

    public Epic(String nameTask, String description) {
        super(nameTask, description);
        status = Status.NEW;
        subtasks = new ArrayList<>();
    }
}
