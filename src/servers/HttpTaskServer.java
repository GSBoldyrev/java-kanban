package servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gson_adapters.FileAdapter;
import gson_adapters.HistoryManagerAdapter;
import gson_adapters.LocalDateTimeAdapter;
import history_managers.HistoryManager;
import misc.Managers;
import task_managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager = Managers.getDefault("http://localhost:8078", "key");
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    public void handle(HttpExchange exchange) throws IOException {
        String response;
        String path = exchange.getRequestURI().getPath();
        String param = exchange.getRequestURI().getQuery();
        switch (path) {
            case "/tasks/task" -> handleTask(exchange);
            case "/tasks/subtask" -> handleSubTask(exchange);
            case "/tasks/epic" -> handleEpic(exchange);
            case "/tasks/subtask/epic" -> {
                int id = Integer.parseInt(param.split("=")[1]);
                List<SubTask> subTasks = manager.getSubTasksFromEpic(id);
                if (subTasks == null) {
                    exchange.sendResponseHeaders(404, 0);
                    response = "Эпик не найден!";
                } else {
                    response = gson.toJson(subTasks);
                    exchange.sendResponseHeaders(200, 0);
                }
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks/history" -> {
                response = gson.toJson(manager.getHistory());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks" -> {
                response = gson.toJson(manager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleTaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleTaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleTaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleSubTask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleSubTaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleSubTaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleSubTaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleEpic(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleEpicGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleEpicPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleEpicDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private String handleTaskDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.deleteAllTasks();
            h.sendResponseHeaders(200, 0);
            response = "Все задачи удалены!";
        } else {
            Task removedTask = manager.removeTask(id);
            if (removedTask == null) {
                h.sendResponseHeaders(404, 0);
                response = "Задача не найдена";
            } else {
                h.sendResponseHeaders(200, 0);
                response = "Задача " + id + " удалена!";
            }
        }
        return response;
    }

    private String handleTaskPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "В теле запроса необходимо передать Task в формате JSON";
        } else {
            Task task = gson.fromJson(body, Task.class);
            if (param == null) {
                int result = manager.addTask(task);
                if (result < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось добавить задачу!";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Задача успешно добавлена!";
                }
            } else {
                task.setId(id);
                int result = manager.updateTask(task);
                if (result < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить задачу!";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Задача " + id + " успешно обновлена!";
                }
            }
        }
        return response;
    }

    private String handleTaskGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = gson.toJson(manager.getAllTasks());
            h.sendResponseHeaders(200, 0);
        } else {
            Task task = manager.getTask(id);
            if (task == null) {
                h.sendResponseHeaders(404, 0);
                response = "Задача не найдена";
            } else {
                response = gson.toJson(task);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleSubTaskDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.deleteAllSubTasks();
            h.sendResponseHeaders(200, 0);
            response = "Все подзадачи удалены!";
        } else {
            Task removedTask = manager.removeSubTask(id);
            if (removedTask == null) {
                h.sendResponseHeaders(404, 0);
                response = "Подзадача не найдена";
            } else {
                h.sendResponseHeaders(200, 0);
                response = "Подзадача " + id + " удалена!";
            }
        }
        return response;
    }

    private String handleSubTaskPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "В теле запроса необходимо передать SubTask в формате JSON";
        } else {
            SubTask task = gson.fromJson(body, SubTask.class);
            if (param == null) {
                int result = manager.addSubTask(task);
                if (result < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось добавить подзадачу!";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Подзадача успешно добавлена!";
                }
            } else {
                task.setId(id);
                int result = manager.updateSubTask(task);
                if (result < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить подзадачу!";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Подзадача " + id + " успешно обновлена!";
                }
            }
        }
        return response;
    }

    private String handleSubTaskGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = gson.toJson(manager.getAllSubTasks());
            h.sendResponseHeaders(200, 0);
        } else {
            Task task = manager.getSubTask(id);
            if (task == null) {
                h.sendResponseHeaders(404, 0);
                response = "Подзадача не найдена";
            } else {
                response = gson.toJson(task);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleEpicDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.deleteAllEpics();
            h.sendResponseHeaders(200, 0);
            response = "Все эпики удалены!";
        } else {
            Task removedTask = manager.removeEpic(id);
            if (removedTask == null) {
                h.sendResponseHeaders(404, 0);
                response = "Эпик не найден";
            } else {
                h.sendResponseHeaders(200, 0);
                response = "Эпик " + id + " удален!";
            }
        }
        return response;
    }

    private String handleEpicPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "В теле запроса необходимо передать Epic в формате JSON";
        } else {
            Epic task = gson.fromJson(body, Epic.class);
            if (param == null) {
                int result = manager.addEpic(task);
                if (result < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось добавить эпик!";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Эпик успешно добавлен!";
                }
            } else {
                task.setId(id);
                int result = manager.updateEpic(task);
                if (result < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить эпик!";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Эпик " + id + " успешно обновлен!";
                }
            }
        }
        return response;
    }

    private String handleEpicGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = gson.toJson(manager.getAllEpics());
            h.sendResponseHeaders(200, 0);
        } else {
            Task task = manager.getEpic(id);
            if (task == null) {
                h.sendResponseHeaders(404, 0);
                response = "Эпик не найден";
            } else {
                response = gson.toJson(task);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен");
        server.stop(0);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseBody().write(resp);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}
