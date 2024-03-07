package tasks;

import enums.TypeTask;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subsId;

    public Epic(String nameTask, String description) {
        super(nameTask, description);
        subsId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubsId() {
        return subsId;
    }

    public void setSubsId(ArrayList<Integer> subsId) {
        this.subsId = subsId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.Epic;
    }
}