package http;

import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTasksTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    HttpClient client = HttpClient.newHttpClient();

    public HttpTasksTest() throws IOException {
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {

    }

    @BeforeEach
    public void init() {
        taskManager.clearAllTasks();
        taskManager.clearAllSubtasks();
        taskManager.clearAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void checkGetTask200() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(7));
        taskManager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = HttpTaskServer.getGson().fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
    }

    @Test
    void checkGetTaskById200() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task getTask = HttpTaskServer.getGson().fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(task1, getTask);
    }

    @Test
    void checkGetNotFoundTaskById404() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void checkGetBadRequestTaskById400() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/two");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkPostCreateTask201() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));

        URI url = URI.create("http://localhost:8080/tasks");
        String requestBody = HttpTaskServer.getGson().toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllTasks().get(0).getId());
    }

    @Test
    void checkPostCreateTaskTrueIntersection406() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(20));

        URI url = URI.create("http://localhost:8080/tasks");
        String requestBody = HttpTaskServer.getGson().toJson(task2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void checkPostUpdateTask201() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        task1.setStatus(Status.IN_PROGRESS);

        URI url = URI.create("http://localhost:8080/tasks?id=1");
        String requestBody = HttpTaskServer.getGson().toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(1).getStatus());
    }

    @Test
    void checkPostBadRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks?id=two");
        String requestBody = HttpTaskServer.getGson().toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkDelete200() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void checkDeleteBadRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkMissMethodTask() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/tasks?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }
}