package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import http.HttpTaskServer;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TasksHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String query = exchange.getRequestURI().getQuery();
        switch (exchange.getRequestMethod()) {
            case "GET": {
                exchange.sendResponseHeaders(200,0);
                ArrayList<Task> allTasks = taskManager.getAllTasks();
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(allTasks.toString().getBytes());
                }
                break;
            }
            case "POST": {
                break;
            }
            case "DELETE": {
                break;
            }
            default: {

            }
        }
    }
}
