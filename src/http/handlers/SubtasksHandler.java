package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import http.HttpTaskServer;

import java.io.IOException;

public class SubtasksHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String query = exchange.getRequestURI().getQuery();
        switch (exchange.getRequestMethod()) {
            case "GET": {
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
