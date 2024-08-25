package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addTaskToHistory() {
        Task task = new Task("Task 1", "Description", Status.NEW);
        taskManager.createTask(task);  // Создаем задачу через TaskManager
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void removeTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description", Status.NEW);
        Task task2 = new Task("Task 2", "Description", Status.IN_PROGRESS);
        taskManager.createTask(task1);  // Создаем задачи через TaskManager
        taskManager.createTask(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void addSameTaskMultipleTimes() {
        Task task = new Task("Task 1", "Description", Status.NEW);
        taskManager.createTask(task);  // Создаем задачу через TaskManager
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyOrderIsMaintained() {
        Task task1 = new Task("Task 1", "Description", Status.NEW);
        Task task2 = new Task("Task 2", "Description", Status.IN_PROGRESS);
        Task task3 = new Task("Task 3", "Description", Status.DONE);
        taskManager.createTask(task1);  // Создаем задачи через TaskManager
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void addTaskAfterRemoval() {
        Task task1 = new Task("Task 1", "Description", Status.NEW);
        Task task2 = new Task("Task 2", "Description", Status.IN_PROGRESS);
        taskManager.createTask(task1);  // Создаем задачи через TaskManager
        taskManager.createTask(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }
}
