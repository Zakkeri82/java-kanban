package controllers;

import enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int id;

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subTasks;
    protected final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.id = 1;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    }

    /**
     * получение менеджера истории просмотров
     *
     * @return объект менеджера истории
     */
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    /**
     * получение всех задач
     *
     * @return список объектов tasks.Task
     */
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * получение всех подзадач
     *
     * @return список объектов tasks.Subtask
     */
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * получение всех эпиков
     *
     * @return список объектов tasks.Epic
     */
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * очистка всех задач из списка (HashMap<Integer, tasks.Task> task)
     */
    @Override
    public void clearAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        prioritizedTasks.removeIf(task -> !(task instanceof Subtask));
    }

    /**
     * очистка всех подзадач из списка (HashMap<Integer, tasks.Subtask> subTask)
     */
    @Override
    public void clearAllSubtasks() {
        epics.values().forEach(epic -> {
            epic.getSubsId().clear();
            epic.setEndTime(null);
            epic.setStartTime(null);
            epic.setDuration(null);
        });
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }

    /**
     * очистка всех эпиков из списка (HashMap<Integer, tasks.Epic> tasks.Epic)
     */
    @Override
    public void clearAllEpics() {
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }

    /**
     * получение задачи по ее Id
     *
     * @param id id задачи
     * @return возвращает один объект tasks.Task
     */
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    /**
     * получение подзадачи по ее Id
     *
     * @param id id подзадачи
     * @return возвращает один объект tasks.Subtask
     */
    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    /**
     * получение эпика по Id
     *
     * @param id id эпика
     * @return возвращает один объект tasks.Epic
     */
    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    /**
     * удаление задачи по ее Id
     *
     * @param id id задачи
     */
    @Override
    public void deleteTaskById(int id) {
        tasks.keySet().removeIf(key -> key == id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    /**
     * удаление подзадачи по ее Id
     *
     * @param id id задачи
     */
    @Override
    public void deleteSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            epics.get(epicId).getSubsId().removeIf(n -> n == id);
            setTimeEpic(epicId);
            subTasks.keySet().removeIf(key -> key == id);
            historyManager.remove(id);
            prioritizedTasks.removeIf(subTask -> subTask.getId() == id);
        }
    }

    /**
     * удаление эпика по его Id
     *
     * @param id id эпика
     */
    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subsId = epics.get(id).getSubsId();
            if (!subsId.isEmpty()) {
                for (int sub : subsId) {
                    subTasks.keySet().removeIf(key -> key == sub);
                    historyManager.remove(sub);
                }
            }
            epics.keySet().removeIf(key -> key == id);
            historyManager.remove(id);
        }
    }

    /**
     * создание задачи и присвоение id
     *
     * @param task объект новой задачи
     */
    @Override
    public void createTask(Task task) {
        boolean intersection = checkIntersectionAllTasks(task);
        if (task != null && task.getId() < 0 && !intersection) {
            task.setId(id++);
            tasks.put(task.getId(), task);
            addTaskToPrioritizedTasksSet(task);
        }
    }

    /**
     * создание эпика и присвоение id
     *
     * @param epic объект нового эпика
     */
    @Override
    public void createEpic(Epic epic) {
        if (epic != null && epic.getId() < 0) {
            epic.setId(id++);
            epics.put(epic.getId(), epic);
        }
    }

    /**
     * создание подзадачи для эпика
     *
     * @param epic    объект эпика для которого создается подзадача
     * @param subtask объект новой подзадачи
     */
    @Override
    public void createSubtaskByEpic(Epic epic, Subtask subtask) {
        boolean intersection = checkIntersectionAllTasks(subtask);
        if (subtask != null && epic.getId() != -1 && subtask.getId() < 0 && !intersection) {
            subtask.setId(id++);
            subTasks.put(subtask.getId(), subtask);
            epic.getSubsId().add(subtask.getId());
            subtask.setEpicId(epic.getId());
            setTimeEpic(epic.getId());
            addTaskToPrioritizedTasksSet(subtask);
        }
    }

    /**
     * получение всех подзадач эпика по его id
     *
     * @param id id эпика
     * @return список подзадач эпика
     */
    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Integer> subsId = epics.get(id).getSubsId();
        ArrayList<Subtask> sub = new ArrayList<>();
        for (Integer subTaskId : subsId) {
            sub.add(subTasks.get(subTaskId));
        }
        return sub;
    }

    /**
     * обновление задачи
     *
     * @param task объект задачи
     */
    @Override
    public void updateTask(Task task) {
        boolean intersection = checkIntersectionAllTasks(task);
        if (task != null && task.getId() > 0 && !intersection) {
            tasks.put(task.getId(), task);
            prioritizedTasks.remove(task);
            addTaskToPrioritizedTasksSet(task);
        }
    }

    /**
     * обновление эпика
     *
     * @param epic объект эпика
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epic.getId() > 0) {
            epics.put(epic.getId(), epic);
        }
    }

    /**
     * обновление подзадачи
     *
     * @param subtask объект новой подзадачи
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        boolean intersection = checkIntersectionAllTasks(subtask);
        if (subtask != null && subtask.getId() > 0 && !intersection) {
            subTasks.put(subtask.getId(), subtask);
            ArrayList<Integer> subtasksId = epics.get(subtask.getEpicId()).getSubsId();
            epics.get(subtask.getEpicId()).setStatus(checkStatusEpic(subtasksId));
            setTimeEpic(subtask.getEpicId());
            prioritizedTasks.remove(subtask);
            addTaskToPrioritizedTasksSet(subtask);
        }
    }

    /**
     * получение приоритизированного списка
     *
     * @return список
     */
    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(this.prioritizedTasks);
    }

    /**
     * проверка наличия пересечения задачи с уже существующими
     *
     * @param task задача
     * @return ркзультат проверки
     */
    @Override
    public boolean checkIntersectionAllTasks(Task task) {
        return getPrioritizedTasks().stream()
                .filter(task1 -> checkIntersectionTime(task, task1))
                .anyMatch(task1 -> true);
    }

    private Status checkStatusEpic(ArrayList<Integer> subtasksId) {
        Status tempStatus = Status.DONE;
        for (Integer taskId : subtasksId) {
            if (subTasks.get(taskId).getStatus().equals(Status.IN_PROGRESS)
                    || subTasks.get(taskId).getStatus().equals(Status.NEW)) {
                tempStatus = Status.IN_PROGRESS;
            }
        }
        return tempStatus;
    }

    private void setTimeEpic(int id) {
        ArrayList<Subtask> subtask = getSubtasksByEpicId(id);
        Subtask withMinStartTime = subtask.stream().min(Comparator.comparing(Subtask::getStartTime)).orElse(null);
        Subtask withMaxEndTime = subtask.stream().max(Comparator.comparing(Subtask::getEndTime)).orElse(null);
        epics.get(id).setStartTime(withMinStartTime != null ? withMinStartTime.getStartTime() : null);
        epics.get(id).setEndTime(withMaxEndTime != null ? withMaxEndTime.getEndTime() : null);
        if (withMinStartTime != null && withMaxEndTime != null) {
            epics.get(id).setDuration(Duration.between(withMinStartTime.getStartTime(), withMaxEndTime.getEndTime()));
        } else {
            epics.get(id).setDuration(null);
        }
    }

    private void addTaskToPrioritizedTasksSet(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    static boolean checkIntersectionTime(Task task1, Task task2) {
        if ((task1 != null) && (task2 != null) && (task1.getId() != task2.getId())) {
            return task1.getStartTime().isBefore(task2.getEndTime())
                    && task1.getEndTime().isAfter(task2.getStartTime());
        }
        return false;
    }
}