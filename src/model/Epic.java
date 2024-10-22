package model;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Map<Integer, Subtask> getSubtaskMap() {
        return subtaskMap;
    }

    public void addSubtask(Subtask subtask) {
        if (this.getId().equals(subtask.getId())) {
            throw new IllegalArgumentException("Epic cannot add itself as a subtask");
        }
        subtaskMap.put(subtask.getId(), subtask);
    }

    public void deleteSubtask(Subtask subtask) {
        subtaskMap.remove(subtask.getId());
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
