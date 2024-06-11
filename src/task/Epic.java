package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        this.subtaskList.add(subtask);
    }

    public void deleteSubtask(Subtask subtask) {
        this.subtaskList.remove(subtask);
    }
}