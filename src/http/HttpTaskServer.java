package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import controllers.Managers;
import controllers.TaskManager;
import enums.Status;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import http.handlers.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final TaskManager taskManager;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() {
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/subtask", new SubtasksHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Поехали!");
        TaskManager fileBackedTaskManager = Managers.getDefault();


        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        fileBackedTaskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(7));
        fileBackedTaskManager.createTask(task2);
        task2.setStatus(Status.IN_PROGRESS);

        Epic epic = new Epic("Первый епик", "простой");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "простая", LocalDateTime.now().plusDays(2), Duration.ofMinutes(10));
        Subtask subtask2 = new Subtask("подзадача2", "сложная", LocalDateTime.now().plusHours(23), Duration.ofMinutes(35));
        Subtask subtask3 = new Subtask("подзадача3", "сложная", LocalDateTime.now().plusDays(3), Duration.ofMinutes(55));
        fileBackedTaskManager.createSubtaskByEpic(epic, subtask);
        fileBackedTaskManager.createSubtaskByEpic(epic, subtask2);
        fileBackedTaskManager.createSubtaskByEpic(epic, subtask3);
        Epic epic2 = new Epic("Второй епик", "сложный");
        fileBackedTaskManager.createEpic(epic2);

        fileBackedTaskManager.getTaskById(1);
        fileBackedTaskManager.getTaskById(2);
        fileBackedTaskManager.getEpicById(3);
        fileBackedTaskManager.getEpicById(7);
        fileBackedTaskManager.getSubtaskById(4);
        fileBackedTaskManager.getSubtaskById(5);
        fileBackedTaskManager.getSubtaskById(6);
        fileBackedTaskManager.getEpicById(3);
        fileBackedTaskManager.getTaskById(2);
        fileBackedTaskManager.getTaskById(1);

        HttpTaskServer httpTaskServer = new HttpTaskServer(fileBackedTaskManager);
        httpTaskServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
