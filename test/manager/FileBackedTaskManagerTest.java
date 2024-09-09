package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;


class FileBackedTaskManagerTest {
    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        // Создаём временный файл
        File tempFile = File.createTempFile("test", ".csv");

        // Создаём менеджер с временным файлом
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        // Сохраняем пустой менеджер в файл
        manager.save();

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи, эпики и подзадачи пусты
        Assertions.assertTrue(loadedManager.getTasks().isEmpty(), "Task list should be empty");
        Assertions.assertTrue(loadedManager.getEpics().isEmpty(), "Epic list should be empty");
        Assertions.assertTrue(loadedManager.getSubtasks().isEmpty(), "Subtask list should be empty");
    }

    @Test
    void testSaveAndLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        Epic epic = new Epic("Epic 1", "Epic description");

        // Используем createEpic для добавления эпика
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", Status.DONE, epic.getId());

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertEquals(2, loadedManager.getTasks().size());
        Assertions.assertEquals(1, loadedManager.getEpics().size());
        Assertions.assertEquals(2, loadedManager.getSubtasks().size());
    }


    @Test
    void testSaveAndLoadMultipleTasksAndEpics() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        manager.createTask(task1);  // Создаём задачу 1
        manager.createTask(task2);  // Создаём задачу 2

        Epic epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);  // Создаём эпик

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", Status.DONE, epic.getId());
        manager.createSubtask(subtask1);  // Создаём подзадачи
        manager.createSubtask(subtask2);

        manager.save();

        // Загрузим менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверим, что данные загружены корректно
        Assertions.assertEquals(task1.getName(), loadedManager.getTask(task1.getId()).getName(), "Task 1 name should match");
        Assertions.assertEquals(task2.getName(), loadedManager.getTask(task2.getId()).getName(), "Task 2 name should match");
    }

}