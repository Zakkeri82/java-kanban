package http;

import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
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

public class HttpPrioritizedTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    HttpClient client = HttpClient.newHttpClient();

    public HttpPrioritizedTest() throws IOException {
    }

    class TaskSetTypeToken extends TypeToken<List<Task>> {

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
    void checkGetPrioritized200() throws IOException, InterruptedException {
        Task task1 = new Task("Первая", "простая", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        Task task2 = new Task("Вторая", "сложная", LocalDateTime.now().minusMinutes(10), Duration.ofMinutes(7));
        taskManager.createTask(task2);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> usersList = HttpTaskServer.getGson().fromJson(response.body(), new TaskSetTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(2, usersList.get(0).getId());
        assertEquals(1, usersList.get(1).getId());
    }

    @Test
    void checkGetBadRequestPrioritized400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized/56");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void checkGetEmptyPrioritized() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void checkMissMethodPrioritized405() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

}
