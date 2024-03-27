package http.utils;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import enums.HttpStatusCode;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseHelper {

    public static void sendOk(HttpExchange exchange, Gson gson, Task... tasks) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.OK.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            if(tasks.length == 1) {
                os.write(gson.toJson(tasks[0]).getBytes());
            } else {
                os.write(gson.toJson(tasks).getBytes());
            }
        }
    }

    public static void sendOkWithoutBody(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.OK.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(HttpStatusCode.OK.getMessage().getBytes());
        }
    }

    public static void sendBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(HttpStatusCode.BAD_REQUEST.getMessage().getBytes());
        }
    }

    public static void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.CREATED.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(HttpStatusCode.CREATED.getMessage().getBytes());
        }
    }

    public static void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.NOT_FOUND.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(HttpStatusCode.NOT_FOUND.getMessage().getBytes());
        }
    }

    public static void sendNotAcceptable(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.NOT_ACCEPTABLE.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(HttpStatusCode.NOT_ACCEPTABLE.getMessage().getBytes());
        }
    }

    public static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.METHOD_NOT_ALLOWED.getCode(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(HttpStatusCode.METHOD_NOT_ALLOWED.getMessage().getBytes());
        }
    }
}