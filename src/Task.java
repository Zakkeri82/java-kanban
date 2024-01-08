import java.util.Objects;

public class Task {

    private final String nameTask;
    private final String description;
    protected Status status = Status.NEW;
    private int id = -1;


    public String getNameTask() {
        return nameTask;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(nameTask, otherTask.nameTask) &&
                Objects.equals(description, otherTask.description) &&
                (id == otherTask.id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        String result = "\n" + getClass().getName() + "{" +
                "id='" + id + "', " +
                "nameTask='" + nameTask + "', " +
                "status='" + status + "', ";
        if(description != null) {
            result = result + "description.length='" + description.length();
        } else {
            result = result + "description=null";
        }
        return result + "}";
    }
}