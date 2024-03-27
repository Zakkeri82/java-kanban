package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import enums.Status;
import http.HttpTaskServer;
import http.utils.ResponseHelper;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
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
                    ArrayList<Task> allTasks = taskManager.getAllTasks();
                    ResponseHelper.sendOk(exchange, gson, allTasks.toArray(new Task[0]));
                } else if (partsPaths.length == 3) {
                    int taskId;
                    try {
                        taskId = Integer.parseInt(partsPaths[2]);
                        Task resultTask = taskManager.getTaskById(taskId);
                        if (resultTask != null) {
                            ResponseHelper.sendOk(exchange, gson, resultTask);
                        } else {
                            ResponseHelper.sendNotFound(exchange);
                        }
                    } catch (NumberFormatException e) {
                        ResponseHelper.sendBadRequest(exchange);
                    }
                } else {
                    ResponseHelper.sendBadRequest(exchange);
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
                        int beginSize = taskManager.getAllTasks().size();
                        taskManager.createTask(task);
                        int endSize = taskManager.getAllTasks().size();
                        if (beginSize != endSize) {
                            ResponseHelper.sendCreated(exchange);
                        } else {
                            ResponseHelper.sendNotAcceptable(exchange);
                        }
                    } else if (partsPaths.length == 2) {
                        String[] partsQuery = query.split("&");
                        if (partsQuery.length > 1) {
                            ResponseHelper.sendBadRequest(exchange);
                        } else {
                            String[] taskIdParam = partsQuery[0].split("=");
                            try {
                                int taskId = Integer.parseInt(taskIdParam[1]);
                                if (!taskManager.checkIntersectionAllTasks(task)) {
                                    taskManager.deleteTaskById(taskId);
                                    task.setId(taskId);
                                    taskManager.updateTask(task);
                                    ResponseHelper.sendCreated(exchange);
                                } else {
                                    ResponseHelper.sendNotAcceptable(exchange);
                                }
                            } catch (NumberFormatException e) {
                                ResponseHelper.sendBadRequest(exchange);
                            }
                        }
                    } else {
                        ResponseHelper.sendBadRequest(exchange);
                    }
                } catch (JsonSyntaxException | NullPointerException e) {
                    ResponseHelper.sendBadRequest(exchange);
                }
                break;
            }
            case "DELETE": {
                if (partsPaths.length == 2 && query == null) {
                    ResponseHelper.sendBadRequest(exchange);
                } else if (partsPaths.length == 2) {
                    String[] partsQuery = query.split("&");
                    if (partsQuery.length > 1) {
                        ResponseHelper.sendBadRequest(exchange);
                    } else {
                        String[] taskIdParam = partsQuery[0].split("=");
                        try {
                            int taskId = Integer.parseInt(taskIdParam[1]);
                            taskManager.deleteTaskById(taskId);
                            ResponseHelper.sendOkWithoutBody(exchange);
                        } catch (NumberFormatException e) {
                            ResponseHelper.sendBadRequest(exchange);
                        }
                    }
                } else {
                    ResponseHelper.sendBadRequest(exchange);
                }
                break;
            }
            default: {
                ResponseHelper.sendMethodNotAllowed(exchange);
            }
        }
    }
}