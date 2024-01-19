import controllers.Managers;
import controllers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task1 = new Task("Первая", "простая");
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная");
        inMemoryTaskManager.createTask(task2);
        Epic epic = new Epic("Первый епик", "простой");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "простая");
        Subtask subtask2 = new Subtask("подзадача2", "сложная");
        inMemoryTaskManager.createSubtaskByEpic(epic, subtask);
        inMemoryTaskManager.createSubtaskByEpic(epic, subtask2);
        Epic epic2 = new Epic("Второй епик", "сложный");
        inMemoryTaskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("подзадача3", "очень сложная");
        inMemoryTaskManager.createSubtaskByEpic(epic2, subtask3);

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(6);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getSubtaskById(5);
        inMemoryTaskManager.getSubtaskById(7);

        printAllTasks(inMemoryTaskManager);
    }

    private static void printAllTasks(TaskManager inMemoryTaskManager) {
        System.out.println("\nЗадачи:");
        for (Task task : inMemoryTaskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Task epic : inMemoryTaskManager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : inMemoryTaskManager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Task subtask : inMemoryTaskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория:");
        for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }
}