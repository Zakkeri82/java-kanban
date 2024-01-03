public class Task {

    private final String nameTask;
    private final String description;
    private Status status;

    public String getNameTask() {
        return nameTask;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = Status.NEW;
    }


}
