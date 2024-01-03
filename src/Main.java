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
        Subtask subtask = new Subtask("подзадача1","простая");
        Subtask subtask2 = new Subtask("подзадача2","сложная");
        taskManager.createSubtaskByEpic(epic, subtask);
        taskManager.createSubtaskByEpic(epic, subtask2);
        Epic epic2 = new Epic("Второй епик", "сложный");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("подзадача3","очень сложная");
        taskManager.createSubtaskByEpic(epic2, subtask3);

        taskManager.getAllTasks();
        System.out.println("================================");
        taskManager.getAllSubtasks();
        System.out.println("================================");
        taskManager.getAllEpics();
    }
}
