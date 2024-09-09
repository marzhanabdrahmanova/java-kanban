package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        if (this.getId().equals(subtask.getId())) {
            throw new IllegalArgumentException("Epic cannot add itself as a subtask");
        }
        this.subtaskList.add(subtask);
    }

    public void deleteSubtask(Subtask subtask) {
        this.subtaskList.remove(subtask);
    }
}