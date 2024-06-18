package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        TaskManager taskManager = Managers.getDefault();
        this.taskManager = taskManager;
        Task task1 = new Task("Task 1", "Description of Task 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description of Task 2", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", Status.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3", Status.DONE, epic2.getId());
        taskManager.createSubtask(subtask3);

    }

    @Test
    void getTasks() {
        List<Task> taskList = taskManager.getTasks();
        assertEquals(2, taskList.size());
    }

    @Test
    void clearTasks() {
        taskManager.clearTasks();
        List<Task> taskList = taskManager.getTasks();
        assertEquals(0, taskList.size());
    }

    @Test
    void getTask() {
        Task task = taskManager.getTask(0);
        assertNotNull(task);
        assertEquals("Task 1", task.getName());
    }

    @Test
    void createTask() {
        Task newTask = new Task("Task 3", "Description of Task 3", Status.NEW);
        taskManager.createTask(newTask);
        Task retrievedTask = taskManager.getTask(newTask.getId());
        assertNotNull(retrievedTask);
        assertEquals("Task 3", retrievedTask.getName());
    }

    @Test
    void updateTask() {
        Task task = taskManager.getTask(0);
        task.setDescription("Updated Description");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTask(0);
        assertEquals("Updated Description", updatedTask.getDescription());
    }

    @Test
    void getEpics() {
        List<Epic> epicList = taskManager.getEpics();
        assertEquals(2, epicList.size());
    }

    @Test
    void clearEpics() {
        taskManager.clearEpics();
        List<Epic> epicList = taskManager.getEpics();
        assertEquals(0, epicList.size());
        List<Subtask> subtaskList = taskManager.getSubtasks();
        assertEquals(0, subtaskList.size());
    }

    @Test
    void getEpic() {
        Epic epic = taskManager.getEpic(2);
        assertNotNull(epic);
        assertEquals("Epic 1", epic.getName());
    }

    @Test
    void createEpic() {
        Epic newEpic = new Epic("Epic 3", "Description of Epic 3");
        taskManager.createEpic(newEpic);
        Epic retrievedEpic = taskManager.getEpic(newEpic.getId());
        assertNotNull(retrievedEpic);
        assertEquals("Epic 3", retrievedEpic.getName());
    }

    @Test
    void updateEpic() {
        Epic epic = taskManager.getEpic(2);
        epic.setDescription("Updated Description");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpic(2);
        assertEquals("Updated Description", updatedEpic.getDescription());
    }

    @Test
    void deleteEpic() {
        taskManager.deleteEpic(2);
        assertNull(taskManager.getEpic(2));
        List<Subtask> subtaskList = taskManager.getSubtasks();
        assertEquals(1, subtaskList.size());
    }

    @Test
    void getSubtasks() {
        List<Subtask> subtaskList = taskManager.getSubtasks();
        assertEquals(3, subtaskList.size());
    }

    @Test
    void clearSubtasks() {
        taskManager.clearSubtasks();
        List<Subtask> subtaskList = taskManager.getSubtasks();
        assertEquals(0, subtaskList.size());
        for (Epic epic : taskManager.getEpics()) {
            assertEquals(0, epic.getSubtaskList().size());
        }
    }

    @Test
    void getSubtask() {
        Subtask subtask = taskManager.getSubtask(4);
        assertNotNull(subtask);
        assertEquals("Subtask 1", subtask.getName());
    }

    @Test
    void createSubtask() {
        Subtask newSubtask = new Subtask("Subtask 4", "Description of Subtask 4", Status.NEW, 2);
        taskManager.createSubtask(newSubtask);
        Subtask retrievedSubtask = taskManager.getSubtask(newSubtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals("Subtask 4", retrievedSubtask.getName());
    }

    @Test
    void updateSubtask() {
        Subtask subtask = taskManager.getSubtask(4);
        subtask.setDescription("Updated Description");
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtask(4);
        assertEquals("Updated Description", updatedSubtask.getDescription());
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteSubtask(4);
        assertNull(taskManager.getSubtask(4));
        Epic epic = taskManager.getEpic(2);
        assertEquals(1, epic.getSubtaskList().size());
    }

    @Test
    void add() {
        Task task = taskManager.getTask(0);
        taskManager.getEpic(2);
        taskManager.getSubtask(4);
        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size());
        assertEquals("Task 1", history.get(0).getName());
        assertEquals("Epic 1", history.get(1).getName());
        assertEquals("Subtask 1", history.get(2).getName());
    }

    @Test
    void getHistory() {
        Task task = taskManager.getTask(0);
        taskManager.getEpic(2);
        taskManager.getSubtask(4);
        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size());
        assertEquals("Task 1", history.get(0).getName());
        assertEquals("Epic 1", history.get(1).getName());
        assertEquals("Subtask 1", history.get(2).getName());
    }

    @Test
    void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(1);

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 1);
        subtask.setId(1);

        assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(subtask);
        }, "Epic cannot add itself as a subtask");
    }

    @Test
    void testSubtaskCannotSetItselfAsEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 1);
        subtask.setId(1);

        assertThrows(IllegalArgumentException.class, () -> {
            subtask.setEpicId(subtask.getId());
        }, "Subtask cannot set itself as its own epic");
    }

    @Test
    void testUtilityClassReturnsInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager should not be null");
        assertTrue(taskManager instanceof InMemoryTaskManager, "TaskManager should be an instance of InMemoryTaskManager");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager should not be null");
        assertTrue(historyManager instanceof InMemoryHistoryManager, "HistoryManager should be an instance of InMemoryHistoryManager");
    }

    @Test
    void testTaskManagerAddsDifferentTaskTypes() {
        Task task = new Task("Task", "Description", Status.NEW);
        taskManager.createTask(task);
        assertNotNull(taskManager.getTask(task.getId()), "Task should be added and retrievable");

        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        assertNotNull(taskManager.getEpic(epic.getId()), "Epic should be added and retrievable");

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        assertNotNull(taskManager.getSubtask(subtask.getId()), "Subtask should be added and retrievable");
    }

    @Test
    void testTaskIdsDoNotConflict() {
        Task taskWithId = new Task("Task 1", "Description 1", Status.NEW);
        taskWithId.setId(1);
        taskManager.createTask(taskWithId);

        Task generatedTask = new Task("Task 2", "Description 2", Status.NEW);
        taskManager.createTask(generatedTask);

        assertNotEquals(taskWithId.getId(), generatedTask.getId(), "Generated ID should not conflict with specified ID");
    }

    @Test
    void testTaskImmutabilityOnAddition() {
        Task task = new Task("Task", "Description", Status.NEW);
        task.setId(1);
        String initialName = task.getName();
        String initialDescription = task.getDescription();
        Status initialStatus = task.getStatus();

        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());

        assertEquals(initialName, retrievedTask.getName(), "Task name should remain unchanged");
        assertEquals(initialDescription, retrievedTask.getDescription(), "Task description should remain unchanged");
        assertEquals(initialStatus, retrievedTask.getStatus(), "Task status should remain unchanged");
    }

    @Test
    void testHistoryManagerPreservesTaskData() {
        Task task1 = new Task("Task1", "Description", Status.NEW);
        Task taskWithId1 = taskManager.createTask(task1);
        taskManager.getTask(taskWithId1.getId());

        Task task2 = new Task("Task2", "Description", Status.NEW);
        Task taskWithId2 = taskManager.createTask(task2);
        taskManager.getTask(taskWithId2.getId());

        List<Task> historyTask = taskManager.getHistory();

        assertTrue(historyTask.contains(task1));
    }
}