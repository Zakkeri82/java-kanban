package http;

import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
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

public class HttpEpicsTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    HttpClient client = HttpClient.newHttpClient();

    public HttpEpicsTest() throws IOException {
    }

    static class EpicListTypeToken extends TypeToken<List<Epic>> {

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
    void checkGetEpics200() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);
        Epic epic2 = new Epic("Второй епик", "сложный");
        taskManager.createEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> epics = HttpTaskServer.getGson().fromJson(response.body(), new EpicListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(epic, epics.get(0));
        assertEquals(epic2, epics.get(1));
    }

    @Test
    void checkGetEpicById200() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task getEpic = HttpTaskServer.getGson().fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(epic, getEpic);
    }

    @Test
    void checkGetNotFoundEpicById404() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void checkGetBadRequestEpicById400() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/two");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkPostCreateEpic201() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");

        URI url = URI.create("http://localhost:8080/epics");
        String requestBody = HttpTaskServer.getGson().toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllEpics().get(0).getId());
    }

    @Test
    void checkPostBadRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/two");
        String requestBody = HttpTaskServer.getGson().toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkDelete200() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void checkDeleteBadRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Первый епик", "простой");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkMissMethodEpic() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        URI url = URI.create("http://localhost:8080/epics?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }
}