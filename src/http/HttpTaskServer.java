package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import controllers.FileBackedTaskManager;
import controllers.Managers;
import controllers.TaskManager;
import http.handlers.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    static Gson gson;

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        TaskManager fileBackedTaskManager = Managers.getDefault();

        httpServer.createContext("/tasks", new TasksHandler(fileBackedTaskManager));
        httpServer.createContext("/epics", new EpicsHandler(fileBackedTaskManager));
        httpServer.createContext("/subtask", new SubtasksHandler(fileBackedTaskManager));
        httpServer.createContext("/history", new HistoryHandler(fileBackedTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(fileBackedTaskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
