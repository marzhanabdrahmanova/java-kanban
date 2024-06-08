package manager;


import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class TaskManager {

        private Map<Integer, Task> tasks = new HashMap<>();
        private Map<Integer, Epic> epics = new HashMap<>();
        private Map<Integer, Subtask> subtasks = new HashMap<>();

        private Map<Integer, Epic> subtaskToEpic = new HashMap<>();

       private int nextId = 0;

       public List<Task> getTasks() {
           return new ArrayList<>(tasks.values());
       }

        public void clearTasks() {
            this.tasks = new HashMap<>();
        }

        public Task getTask(int taskId) {
            return tasks.get(taskId);
        }
       public Task createTask(Task task) {
            task.setId(getNextId());
            tasks.put(task.getId(), task);
            return task;
        }

        public Task updateTask(Task task) {
            Integer taskId = task.getId();
            if(taskId == null) {
                System.out.println("У таска должен быть идентификатор");
                return null;
            }
            tasks.put(taskId, task);
            return task;
        }

        public void deleteTask(int taskId) {
           tasks.remove(taskId);
        }

        //  Epic
        public List<Epic> getEpics() {
            return new ArrayList<>(epics.values());
        }

        public void clearEpics() {
            epics.clear();
            subtasks.clear();
        }

        public Epic getEpic(int epicId) {
            return epics.get(epicId);
        }

        public Epic createEpic(Epic epic) {
            epic.setId(getNextId());
            epics.put(epic.getId(), epic);
            return epic;
        }

        public Epic updateEpic(Epic epic) {
            Integer epicId = epic.getId();
            if (epicId == null) {
                System.out.println("У эпика должен быть идентификатор");
                return null;
            }
            epics.put(epicId, epic);
            return epic;
        }

        public void deleteEpic(int epicId) {
            Epic epic = epics.remove(epicId);
            if (epic != null) {
                for (Subtask subtask : epic.getSubtaskList()) {
                    subtasks.remove(subtask.getId());
                }
            }
        }


        // Subtask
        public List<Subtask> getSubtasks() {
            return new ArrayList<>(subtasks.values());
        }

        public void clearSubtasks() {
            subtasks.clear();
        }

        public Subtask getSubtask(int subtaskId) {
            return subtasks.get(subtaskId);
        }

        public Subtask createSubtask(Subtask subtask, int epicId) {
            subtask.setId(getNextId());
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            subtaskToEpic.put(subtask.getId(), epic);
            if (epic != null) {
                epic.addSubtask(subtask);
                updateEpicStatus(epic, subtask.getStatus());
            }
            return subtask;
        }

        public Subtask updateSubtask(Subtask subtask) {
            Integer subtaskId = subtask.getId();
            if (subtaskId == null) {
                System.out.println("У подзадачи должен быть идентификатор");
                return null;
            }
            subtasks.put(subtaskId, subtask);
            Epic epic = subtaskToEpic.get(subtaskId);

            if (epic != null) {
                updateEpicStatus(epic, subtask.getStatus());
            }
            return subtask;
        }

        public void deleteSubtask(int subtaskId) {
            Subtask subtask = subtasks.remove(subtaskId);
            if (subtask != null) {
                Epic epic = subtaskToEpic.get(subtaskId);
                if (epic != null) {
                    epic.getSubtaskList().remove(subtask);
                }
            }
        }
        private void updateEpicStatus(Epic epic, Status status) {
           List<Subtask> subtaskList = epic.getSubtaskList();
           for(Subtask subtask: subtaskList) {
               if (!subtask.getStatus().equals(status)){
                   return;
               }
               epic.setStatus(status);
           }
        }

        private int getNextId() {
            return nextId++;
        }
    }
