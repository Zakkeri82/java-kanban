import controllers.Managers;
import controllers.TaskManager;
import enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(7));
        inMemoryTaskManager.createTask(task2);
        task2.setStatus(Status.IN_PROGRESS);

        Epic epic = new Epic("Первый епик", "простой");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "простая", LocalDateTime.now().plusDays(2), Duration.ofMinutes(10));
        Subtask subtask2 = new Subtask("подзадача2", "сложная", LocalDateTime.now().plusHours(23), Duration.ofMinutes(35));
        Subtask subtask3 = new Subtask("подзадача3", "сложная", LocalDateTime.now().plusDays(3), Duration.ofMinutes(55));
        inMemoryTaskManager.createSubtaskByEpic(epic, subtask);
        inMemoryTaskManager.createSubtaskByEpic(epic, subtask2);
        inMemoryTaskManager.createSubtaskByEpic(epic, subtask3);
        Epic epic2 = new Epic("Второй епик", "сложный");
        inMemoryTaskManager.createEpic(epic2);

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(7);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getSubtaskById(5);
        inMemoryTaskManager.getSubtaskById(6);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getTaskById(1);


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
        if (inMemoryTaskManager.getHistoryManager().getHistory() != null) {
            for (Task task : inMemoryTaskManager.getHistoryManager().getHistory()) {
                System.out.println(task);
            }
        } else {
            System.out.println("Истории запросов нет!");
        }
        System.out.println("\nПо приоритету:");
        for (Task task : inMemoryTaskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}