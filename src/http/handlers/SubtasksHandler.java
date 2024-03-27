package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import enums.Status;
import http.HttpTaskServer;
import http.utils.ResponseHelper;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
        final String[] partsPaths = exchange.getRequestURI().getPath().split("/");
        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (partsPaths.length == 2) {
                    ArrayList<Subtask> allSubTasks = taskManager.getAllSubtasks();
                    ResponseHelper.sendOk(exchange, gson, allSubTasks.toArray(new Task[0]));
                } else if (partsPaths.length == 3) {
                    int subTaskId;
                    try {
                        subTaskId = Integer.parseInt(partsPaths[2]);
                        Task resultSubTask = taskManager.getSubtaskById(subTaskId);
                        if (resultSubTask != null) {
                            ResponseHelper.sendOk(exchange, gson, resultSubTask);
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
                    String[] partsQuery = query.split("&");
                    Subtask subTask = gson.fromJson(body, Subtask.class);
                    if (partsPaths.length == 2 && partsQuery.length == 1) {
                        String[] epicIdParam = partsQuery[0].split("=");
                        int epicId = Integer.parseInt(epicIdParam[1]);
                        subTask.setStatus(Status.NEW);
                        int beginSize = taskManager.getAllSubtasks().size();
                        taskManager.createSubtaskByEpic(taskManager.getEpicById(epicId), subTask);
                        int endSize = taskManager.getAllSubtasks().size();
                        if (beginSize != endSize) {
                            ResponseHelper.sendCreated(exchange);
                        } else {
                            ResponseHelper.sendNotAcceptable(exchange);
                        }
                    } else if (partsPaths.length == 2 && partsQuery.length == 2) {
                        String[] subTaskIdParam = partsQuery[1].split("=");
                        try {
                            int subTaskId = Integer.parseInt(subTaskIdParam[1]);
                            if (!taskManager.checkIntersectionAllTasks(subTask)) {
                                taskManager.deleteSubtaskById(subTaskId);
                                subTask.setId(subTaskId);
                                taskManager.updateSubtask(subTask);
                                ResponseHelper.sendCreated(exchange);
                            } else {
                                ResponseHelper.sendNotAcceptable(exchange);
                            }
                        } catch (NumberFormatException e) {
                            ResponseHelper.sendBadRequest(exchange);
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
                            int subtaskId = Integer.parseInt(taskIdParam[1]);
                            taskManager.deleteSubtaskById(subtaskId);
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