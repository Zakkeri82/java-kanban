package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import http.HttpTaskServer;
import http.utils.ResponseHelper;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String[] partsPaths = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            if (partsPaths.length == 2) {
                List<Task> allTasks = taskManager.getPrioritizedTasks();
                ResponseHelper.sendOk(exchange, gson, allTasks.toArray(new Task[0]));
            } else {
                ResponseHelper.sendBadRequest(exchange);
            }
        } else {
            ResponseHelper.sendMethodNotAllowed(exchange);
        }
    }
}