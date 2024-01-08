import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int id;

    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        this.id = 1;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    /**
     * получение всех задач
     *
     * @return список объектов Task
     */
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * получение всех подзадач
     *
     * @return список объектов Subtask
     */
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * получение всех эпиков
     *
     * @return список объектов Epic
     */
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * очистка всех задач из списка (HashMap<Integer, Task> task)
     */
    public void clearAllTasks() {
        tasks.clear();
    }

    /**
     * очистка всех подзадач из списка (HashMap<Integer, Subtask> subTask)
     */
    public void clearAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.subsId.clear();
        }
        subTasks.clear();
    }

    /**
     * очистка всех эпиков из списка (HashMap<Integer, Epic> Epic)
     */
    public void clearAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    /**
     * получение задачи по ее Id
     *
     * @param id id задачи
     * @return возвращает один объект Task
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * получение подзадачи по ее Id
     *
     * @param id id подзадачи
     * @return возвращает один объект Subtask
     */
    public Subtask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    /**
     * получение эпика по Id
     *
     * @param id id эпика
     * @return возвращает один объект Epic
     */
    public Epic getEpicBuId(int id) {
        return epics.get(id);
    }

    /**
     * удаление задачи по ее Id
     *
     * @param id id задачи
     */
    public void deleteTaskById(int id) {
        tasks.keySet().removeIf(key -> key == id);
    }

    /**
     * удаление подзадачи по ее Id
     *
     * @param id id задачи
     */
    public void deleteSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            epics.get(epicId).subsId.removeIf(n -> n == id);
            subTasks.keySet().removeIf(key -> key == id);
        }
    }

    /**
     * удаление эпика по его Id
     *
     * @param id id эпика
     */
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subsId = epics.get(id).subsId;
            if (!subsId.isEmpty()) {
                for (int sub : subsId) {
                    subTasks.keySet().removeIf(key -> key == sub);
                }
            }
            epics.keySet().removeIf(key -> key == id);
        }
    }

    /**
     * создание задачи и присвоение id
     *
     * @param task объект новой задачи
     */
    public void createTask(Task task) {
        if (task != null && task.getId() < 0) {
            task.setId(id++);
            tasks.put(task.getId(), task);
        }
    }

    /**
     * создание эпика и присвоение id
     *
     * @param epic объект нового эпика
     */
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
    public void createSubtaskByEpic(Epic epic, Subtask subtask) {
        if (subtask != null && epic != null && subtask.getId() < 0) {
            subtask.setId(id++);
            subTasks.put(subtask.getId(), subtask);
            epic.subsId.add(subtask.getId());
            subtask.setEpicId(epic.getId());
        }
    }

    /**
     * получение всех подзадач эпика по его id
     *
     * @param id id эпика
     * @return список подзадач эпика
     */
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Integer> subsId = epics.get(id).subsId;
        ArrayList<Subtask> sub = new ArrayList<>();
        for (Integer subTaskId : subsId) {
            sub.add(subTasks.get(subTaskId));
        }
        return sub;
    }

    /**
     * обновление задачи со сменой статуса
     *
     * @param task   объект новой задачи
     * @param status новый статус задачи
     */
    public void updateTask(Task task, Status status) {
        if (task != null && task.getId() > 0) {
            task.status = status;
            tasks.put(task.getId(), task);
        }
    }

    /**
     * обновление подзадачи со сменой статуса
     *
     * @param subtask объект новой подзадачи
     * @param status  новый статус подзадачи
     */
    public void updateSubtask(Subtask subtask, Status status) {
        if (subtask != null && subtask.getId() > 0) {
            subtask.status = status;
            subTasks.put(subtask.getId(), subtask);
            ArrayList<Integer> subtasksId = epics.get(subtask.getEpicId()).subsId;
            epics.get(subtask.getEpicId()).status = checkStatusEpic(subtasksId);
        }
    }

    private Status checkStatusEpic(ArrayList<Integer> subtasksId) {
        Status tempStatus = Status.DONE;
        for (Integer taskId : subtasksId) {
            if (subTasks.get(taskId).status.equals(Status.IN_PROGRESS)
                    || subTasks.get(taskId).status.equals(Status.NEW)) {
                tempStatus = Status.IN_PROGRESS;
            }
        }
        return tempStatus;
    }
}