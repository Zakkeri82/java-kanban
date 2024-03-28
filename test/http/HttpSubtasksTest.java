package http;

import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
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

public class HttpSubtasksTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    HttpClient client = HttpClient.newHttpClient();

    public HttpSubtasksTest() throws IOException {
    }

    class SubtaskListTypeToken extends TypeToken<List<Subtask>> {

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
    void checkGetSubtask200() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);
        Subtask subtask2 = new Subtask("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(7));
        taskManager.createSubtaskByEpic(epic, subtask2);

        URI url = URI.create("http://localhost:8080/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> subtasks = HttpTaskServer.getGson().fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(subtask1, subtasks.get(0));
        assertEquals(subtask2, subtasks.get(1));
    }

    @Test
    void checkGetSubtaskById200() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);

        URI url = URI.create("http://localhost:8080/subtask/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task getSubtask = HttpTaskServer.getGson().fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode());
        assertEquals(subtask1, getSubtask);
    }

    @Test
    void checkGetNotFoundSubtaskById404() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);

        URI url = URI.create("http://localhost:8080/subtask/100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void checkGetBadRequestSubtaskById400() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);

        URI url = URI.create("http://localhost:8080/subtask/two");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkPostCreateSubtask201() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));

        URI url = URI.create("http://localhost:8080/subtask?epic=1");
        String requestBody = HttpTaskServer.getGson().toJson(subtask1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(2, taskManager.getAllSubtasks().get(0).getId());
    }

    @Test
    void checkPostCreateSubtaskTrueIntersection406() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);
        Subtask subtask2 = new Subtask("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(30));

        URI url = URI.create("http://localhost:8080/subtask?epic=1");
        String requestBody = HttpTaskServer.getGson().toJson(subtask2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void checkPostUpdateSubtask201() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);
        subtask1.setStatus(Status.IN_PROGRESS);

        URI url = URI.create("http://localhost:8080/subtask?epic=1&id=2");
        String requestBody = HttpTaskServer.getGson().toJson(subtask1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(2).getStatus());
    }

    @Test
    void checkPostBadRequestSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);

        URI url = URI.create("http://localhost:8080/subtask?epic=1&id=two");
        String requestBody = HttpTaskServer.getGson().toJson(subtask1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkDelete200() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);

        URI url = URI.create("http://localhost:8080/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void checkDeleteBadRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createSubtaskByEpic(epic, subtask1);

        URI url = URI.create("http://localhost:8080/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkMissMethodTask() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/subtask?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }
}