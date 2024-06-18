package manager;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public final class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new LinkedList<>();
    private static final int HISTORY_LIMIT = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == HISTORY_LIMIT) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}


