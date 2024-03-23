package tasks;

import enums.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subsId;

    private LocalDateTime endTime;

    public Epic(String nameTask, String description) {
        super(nameTask, description, null, null);
        subsId = new ArrayList<>();
    }


    public ArrayList<Integer> getSubsId() {
        return subsId;
    }

    public void setSubsId(ArrayList<Integer> subsId) {
        this.subsId = subsId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.Epic;
    }
}