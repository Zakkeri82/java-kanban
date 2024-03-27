package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import enums.Status;
import http.HttpTaskServer;
import http.utils.ResponseHelper;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicsHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
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
                    ArrayList<Epic> allEpic = taskManager.getAllEpics();
                    ResponseHelper.sendOk(exchange, gson, allEpic.toArray(new Task[0]));
                } else if (partsPaths.length == 3) {
                    int epicId;
                    try {
                        epicId = Integer.parseInt(partsPaths[2]);
                        Task resultEpic = taskManager.getEpicById(epicId);
                        if (resultEpic != null) {
                            ResponseHelper.sendOk(exchange, gson, resultEpic);
                        } else {
                            ResponseHelper.sendNotFound(exchange);
                        }
                    } catch (NumberFormatException e) {
                        ResponseHelper.sendBadRequest(exchange);
                    }
                } else if (partsPaths.length == 4 && partsPaths[3].equals("subtasks")) {
                    int epicId;
                    try {
                        epicId = Integer.parseInt(partsPaths[2]);
                        ArrayList<Subtask> resultSubtasksByEpic = taskManager.getSubtasksByEpicId(epicId);
                        if (resultSubtasksByEpic != null) {
                            ResponseHelper.sendOk(exchange, gson, resultSubtasksByEpic.toArray(new Task[0]));
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
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (partsPaths.length == 2) {
                        epic.setStatus(Status.NEW);
                        int beginSize = taskManager.getAllEpics().size();
                        taskManager.createEpic(epic);
                        int endSize = taskManager.getAllEpics().size();
                        if (beginSize != endSize) {
                            ResponseHelper.sendCreated(exchange);
                        } else {
                            ResponseHelper.sendNotAcceptable(exchange);
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
                            int epicId = Integer.parseInt(taskIdParam[1]);
                            taskManager.deleteEpicById(epicId);
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