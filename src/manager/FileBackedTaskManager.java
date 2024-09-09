package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,epic\n"); // Заголовок CSV
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
                for (Subtask subtask : epic.getSubtaskList()) {
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks to file", e);
        }
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
        epic.setId(getNextId());  // Присваиваем уникальный ID
        epics.put(epic.getId(), epic);  // Сохраняем эпик
        save();  // Сохраняем текущее состояние в файл
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) { // Пропускаем заголовок
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.createSubtask((Subtask) task);
                } else {
                    manager.createTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading tasks from file", e);
        }
        return manager;
    }

    // Преобразование задачи в строку CSV
    private String toString(Task task) {
        String type = task instanceof Epic ? "EPIC" : task instanceof Subtask ? "SUBTASK" : "TASK";
        String status = task.getStatus() != null ? task.getStatus().name() : "NEW"; // Если статус null, устанавливаем NEW
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";

        return String.join(",",
                String.valueOf(task.getId()),  // id
                type,                          // type (TASK, EPIC, SUBTASK)
                task.getName(),                // name
                status,                        // статус задачи
                task.getDescription(),         // описание
                epicId                         // epic id (только для подзадач)
        );
    }

    // Создание задачи из строки
    private static Task fromString(String value) {
        String[] fields = value.split(",");  // разделяем строку на поля по запятой
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);  // преобразуем строку в enum
        String description = fields[4];

        Task task = switch (type) {
            case "TASK" -> new Task(name, description, status);
            case "EPIC" -> new Epic(name, description);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(fields[5]);
                yield new Subtask(name, description, status, epicId);
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
        task.setId(id);
        return task;
    }


    private static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }

        }
    }

