package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager manager) {
        historyManager = manager;
    }

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }


    private int nextId = 0;

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return tasks.get(taskId);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null) {
            System.out.println("У таска должен быть идентификатор");
            return null;
        }
        tasks.put(taskId, task);
        return task;
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    // Epic
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(int epicId) {
        Task task = epics.get(epicId);
        historyManager.add(task);
        return epics.get(epicId);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null) {
            System.out.println("У эпика должен быть идентификатор");
            return null;
        }
        epics.put(epicId, epic);
        return epic;
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtaskList()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    // Subtask
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskList().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Task task = subtasks.get(subtaskId);
        historyManager.add(task);
        return subtasks.get(subtaskId);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtaskId == null) {
            System.out.println("У подзадачи должен быть идентификатор");
            return null;
        }
        subtasks.put(subtaskId, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.deleteSubtask(subtask);
                updateEpicStatus(epic);
            }
        }
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtaskList();

        if (subtaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : subtaskList) {
            if (!subtask.getStatus().equals(Status.NEW)) {
                allNew = false;
            }
            if (!subtask.getStatus().equals(Status.DONE)) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    private int getNextId() {
        return nextId++;
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpic(epic.getId()).getSubtaskList()) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}