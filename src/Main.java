import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task 1", "Description of Task 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description of Task 2", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);


        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", Status.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1, epic1.getId());
        taskManager.createSubtask(subtask2, epic1.getId());

        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3", Status.DONE);
        taskManager.createSubtask(subtask3, epic2.getId());


        System.out.println("Tasks:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task.getName());
        }

        System.out.println("\nEpics:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.getName());
        }

        System.out.println("\nSubtasks:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.getName());
        }

        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        System.out.println("\nEpics after updates:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.getName()+" status: "+epic.getStatus());
        }

        taskManager.clearTasks();
        taskManager.clearEpics();

        System.out.println("\nTasks after deletions:");
        System.out.println(taskManager.getTasks().size());

        System.out.println("\nEpics size after deletions:");
        System.out.println(taskManager.getEpics().size());

        System.out.println("\nSubtasks size after deletions:");
        System.out.println(taskManager.getSubtasks().size());
    }
}
