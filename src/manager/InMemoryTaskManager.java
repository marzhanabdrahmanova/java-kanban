package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

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
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
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

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    // Epic
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
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
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(epicId);
        }
    }

    // Subtask
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
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
            historyManager.remove(subtaskId);
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

    int getNextId() {
        return nextId++;
    }
}
