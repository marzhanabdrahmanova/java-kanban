package service;

import model.Task;

import java.util.*;

public final class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId()); // Удаляем, если задача уже была в истории
            linkLast(task); // Добавляем в конец истории
        }
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasksFromHistory(); // Возвращаем историю задач
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
        }
        tail = newNode;
        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
    }

    private List<Task> getTasksFromHistory() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }
}
