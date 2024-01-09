import controllers.TaskManager;
import enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Первая", "простая");
        taskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная");
        taskManager.createTask(task2);
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "простая");
        Subtask subtask2 = new Subtask("подзадача2", "сложная");
        taskManager.createSubtaskByEpic(epic, subtask);
        taskManager.createSubtaskByEpic(epic, subtask2);
        Epic epic2 = new Epic("Второй епик", "сложный");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("подзадача3", "очень сложная");
        taskManager.createSubtaskByEpic(epic2, subtask3);

        System.out.println("Вывод всех тасков");
        System.out.println(taskManager.getAllTasks());
        System.out.println("========================");
        System.out.println(taskManager.getAllEpics());
        System.out.println("========================");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();

        System.out.println("Проверка подзадач эпиков");
        System.out.println(taskManager.getSubtasksByEpicId(3));
        System.out.println("========================");
        System.out.println(taskManager.getSubtasksByEpicId(6));
        System.out.println();


        System.out.println("Изменяем задачи");
        task1.setStatus(Status.IN_PROGRESS);
        task1.setDescription("новое описание");
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        System.out.println();

        System.out.println("Проверка изменения у задач");
        System.out.println(taskManager.getAllTasks());
        System.out.println("========================");
        System.out.println();

        System.out.println("изменяем эпик");
        epic.setDescription("Длиииииииииииииииииииииииииииииииииииинный эпик и большой");
        taskManager.updateEpic(epic);
        System.out.println();

        System.out.println("Проверяем изменения в эпике");
        System.out.println(taskManager.getEpicById(3));
        System.out.println();

        System.out.println("Изменяем подзадачи эпиков");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setDescription("у подзадачи новое описание");
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask3);
        System.out.println();

        System.out.println("Проверка подзадач эпиков с изменениями");
        System.out.println(taskManager.getSubtasksByEpicId(3));
        System.out.println("========================");
        System.out.println(taskManager.getSubtasksByEpicId(6));
        System.out.println();

        System.out.println("Проверка изменения статусов эпиков");
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        System.out.println("Удаляем");
        taskManager.deleteTaskById(1);
        taskManager.deleteSubtaskById(4);
        taskManager.deleteEpicById(6);
        System.out.println();

        System.out.println("Проверяем удаление");
        System.out.println(taskManager.getAllTasks());
        System.out.println("========================");
        System.out.println(taskManager.getAllEpics());
        System.out.println("========================");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();
    }
}