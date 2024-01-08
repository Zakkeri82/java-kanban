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


        System.out.println("Изменены статусы задач");
        taskManager.updateTask(task1, Status.IN_PROGRESS);
        taskManager.updateTask(task2, Status.DONE);
        System.out.println();

        System.out.println("Проверка статустов задач");
        System.out.println(taskManager.getAllTasks());
        System.out.println("========================");
        System.out.println();


        System.out.println("Изменил статусы подзадач эпиков");
        taskManager.updateSubtask(subtask, Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3, Status.DONE);
        System.out.println();

        System.out.println("Проверка подзадач эпиков с измененными статусами");
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