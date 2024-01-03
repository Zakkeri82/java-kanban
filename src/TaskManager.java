import java.util.HashMap;

public class TaskManager {

    public static int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();


    //получение всех
    public void getAllTasks() {
        tasks.forEach((k, v) -> System.out.println(v.getNameTask()));
    }

    public void getAllSubtasks() {
        subTasks.forEach((k, v) -> System.out.println(v.getNameTask()));
    }

    public void getAllEpics() {
        epics.forEach((k, v) -> System.out.println(v.getNameTask()));
    }


    //удаление всех
    public void clearAllTasks() {
        tasks.clear();
    }

    public void clearAllSubtasks() {
        subTasks.clear();
    }

    public void clearAllEpics() {
        epics.clear();
    }

    //получение по id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicBuId(int id) {
        return epics.get(id);
    }

    //удаление по id

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        subTasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    //создание

    public void createTask(Task task) {
        tasks.put(id++, task);
    }

    public void createEpic(Epic epic) {
        epics.put(id++, epic);
    }

    public void createSubtaskByEpic(Epic epic, Subtask subtask) {
        epic.subtasks.add(subtask);
        subTasks.put(id++, subtask);
    }
}
