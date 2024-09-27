package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    void clearTasks();

    Task getTask(int taskId);

    Task createTask(Task task);

    Task updateTask(Task task);

    void deleteTask(int taskId);


    List<Epic> getEpics();

    void clearEpics();

    Epic getEpic(int epicId);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpic(int epicId);


    List<Subtask> getSubtasks();

    void clearSubtasks();

    Subtask getSubtask(int subtaskId);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtask(int subtaskId);

    List<Task> getHistory();
}