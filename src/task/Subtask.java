package task;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (this.epicId == epicId) {
            throw new IllegalArgumentException("Subtask cannot set itself as its own epic");
        }
        this.epicId = epicId;
    }
}