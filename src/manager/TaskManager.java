package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    void clearTasks();

    Task getTask(int taskId);

    Task createTask(Task task);

    Task updateTask(Task task);

    void deleteTask(int taskId);

    // Epic
    List<Epic> getEpics();

    void clearEpics();

    Epic getEpic(int epicId);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpic(int epicId);

    // Subtask
    List<Subtask> getSubtasks();

    void clearSubtasks();

    Subtask getSubtask(int subtaskId);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtask(int subtaskId);

    List<Task> getHistory();
}