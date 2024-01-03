public class Subtask extends Task {

    private Status status;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
        status = Status.NEW;
    }
}
