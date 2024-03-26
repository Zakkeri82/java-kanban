package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import enums.HttpStatusCode;
import enums.Status;
import http.HttpTaskServer;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
        final String[] partsPaths = exchange.getRequestURI().getPath().split("/");
        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (partsPaths.length == 2) {
                    exchange.sendResponseHeaders(HttpStatusCode.OK.getCode(), 0);
                    ArrayList<Task> allTasks = taskManager.getAllTasks();
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson(allTasks).getBytes());
                    }
                } else if (partsPaths.length == 3) {
                    int taskId;
                    try {
                        taskId = Integer.parseInt(partsPaths[2]);
                        Task resultTask = taskManager.getTaskById(taskId);
                        if (resultTask != null) {
                            exchange.sendResponseHeaders(HttpStatusCode.OK.getCode(), 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(gson.toJson(resultTask).getBytes());
                            }
                        } else {
                            exchange.sendResponseHeaders(HttpStatusCode.NOT_FOUND.getCode(), 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(HttpStatusCode.NOT_FOUND.getMessage().getBytes());
                            }
                        }
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(HttpStatusCode.BAD_REQUEST.getMessage().getBytes());
                        }
                    }
                } else {
                    exchange.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(HttpStatusCode.BAD_REQUEST.getMessage().getBytes());
                    }
                }
                break;
            }
            case "POST": {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(body, Task.class);
                    if (partsPaths.length == 2 && query == null) {
                        task.setStatus(Status.NEW);
                        taskManager.createTask(task);
                        exchange.sendResponseHeaders(HttpStatusCode.CREATED.getCode(), 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(HttpStatusCode.CREATED.getMessage().getBytes());
                        }
                    } else if (partsPaths.length == 2) {
                        String[] partsQuery = query.split("&");
                        if(partsQuery.length > 1) {
                            exchange.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(HttpStatusCode.BAD_REQUEST.getMessage().getBytes());
                            }
                        } else {
                            String[] taskIdParam = partsQuery[0].split("=");
                            task.setStatus(Status.IN_PROGRESS);

                        }
                    } else {
                        exchange.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(HttpStatusCode.BAD_REQUEST.getMessage().getBytes());
                        }
                    }
                } catch (JsonSyntaxException | NullPointerException e) {
                    exchange.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(HttpStatusCode.BAD_REQUEST.getMessage().getBytes());
                    }
                }


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
