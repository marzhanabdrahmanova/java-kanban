package service;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String CSV_HEADER = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    private static void addTaskToManager(FileBackedTaskManager manager, Task task) {
        if (task instanceof Epic) {
            manager.getMapEpics().put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            manager.getSubtasksMap().put(task.getId(), (Subtask) task);
        } else {
            manager.getTasksMaps().put(task.getId(), task);
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int maxId = 0;
            for (String line : lines.subList(1, lines.size())) {
                System.out.println("Reading line: " + line);

                // Проверяем, что строка не пустая
                if (line.trim().isEmpty()) {
                    System.out.println("Skipping empty line.");
                    continue;
                }

                Task task = createTaskFromLine(line); // Создаем задачу из строки
                if (task != null) {
                    addTaskToManager(manager, task);  // Добавляем задачу напрямую в менеджер
                    maxId = Math.max(maxId, task.getId());  // Находим максимальный ID
                } else {
                    System.out.println("Task creation failed for line: " + line);
                }
            }
            manager.setNextId(maxId + 1);  // Устанавливаем следующий ID
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading tasks from file", e);
        }
        return manager;
    }


    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write(CSV_HEADER + "\n"); // Используем константу для заголовка

            // Сохраняем задачи
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }

            // Сохраняем эпики и их подзадачи
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");

                // Проходимся по подзадачам эпика
                for (Subtask subtask : epic.getSubtaskMap().values()) { // Используем .values() для получения подзадач из Map
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks to file", e);
        }
    }


    private String toString(Task task) {
        String type = task.getType().name();  // Получаем тип через getType
        String status = task.getStatus() != null ? task.getStatus().name() : "NEW";
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                status,
                task.getDescription(),
                epicId
        );
    }

    // Создание задачи из строки
    private static Task createTaskFromLine(String value) {
        String[] fields = value.split(",");

        // Проверяем, что массив содержит достаточно элементов
        if (fields.length < 5) {
            System.out.println("Invalid line format: " + value);
            return null;
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        Task task;
        switch (type) {
            case TASK -> task = new Task(name, description, status);
            case EPIC -> task = new Epic(name, description);
            case SUBTASK -> {
                if (fields.length > 5) {
                    int epicId = Integer.parseInt(fields[5]);
                    task = new Subtask(name, description, status, epicId);
                } else {
                    System.out.println("Invalid subtask format: " + value);
                    return null;
                }
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        }

        task.setId(id);
        return task;
    }
}
