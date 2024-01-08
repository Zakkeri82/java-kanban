import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subsId;


    public Epic(String nameTask, String description) {
        super(nameTask, description);
        subsId = new ArrayList<>();
    }
}