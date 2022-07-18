package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gson_adapters.FileAdapter;
import gson_adapters.HistoryManagerAdapter;
import gson_adapters.LocalDateTimeAdapter;
import history_managers.HistoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServerTest {

    HttpTaskServer server;
    KVServer kvServer;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
    public Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();
    protected Task task_1;
    protected Task task_2;
    protected Epic epic_1;
    protected Epic epic_2;
    protected SubTask subTask_1;
    protected SubTask subTask_2;

    @BeforeEach
    public void startServers() throws IOException {
        server = new HttpTaskServer();
        kvServer = new KVServer();
        server.start();
        kvServer.start();
    }

    @BeforeEach
    public void createEnvironment() {
        task_1 = new Task("T1", "TD1", TaskStatus.NEW, 15, null);
        task_2 = new Task("T2", "TD2", TaskStatus.NEW, 15, null);
        epic_1 = new Epic("E1", "ED1");
        epic_2 = new Epic("E2", "ED2");
        subTask_1 = new SubTask("S1", "SD1", TaskStatus.NEW, 0, 15, null);
        subTask_2 = new SubTask("S2", "SD2", TaskStatus.NEW, 0, 15, null);
    }

    @AfterEach
    public void stopServers() {
        server.stop();
        kvServer.stop();
    }

    public HttpRequest createGetRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().GET().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpRequest createDeleteRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().DELETE().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpResponse<String> addTaskToServer(Task task, String path) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        String body = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        return client.send(request, handler);
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(task_1, "/task");
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Задача успешно добавлена!", response.body());
    }

    @Test
    public void shouldNotAddWrongTask() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(null, "/task");
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Не удалось добавить задачу!", response.body());
    }

    @Test
    public void shouldNotAddEmptyTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        HttpResponse<String> response = client.send(request, handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("В теле запроса необходимо передать Task в формате JSON", response.body());
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        task_2.setId(0);
        HttpResponse<String> response = addTaskToServer(task_2, "/task?id=0");
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Задача 0 успешно обновлена!", response.body());
    }

    @Test
    public void shouldNotUpdateWrongTask() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        HttpResponse<String> response = addTaskToServer(task_2, "/task?id=1");
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Не удалось обновить задачу!", response.body());
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=0"), handler);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача 0 удалена!", response.body());
    }

    @Test
    public void shouldNotDeleteTaskWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=1"), handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Задача не найдена", response.body());
    }

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");
        HttpResponse<String> response = client.send(createDeleteRequest("/task"), handler);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Все задачи удалены!", response.body());
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");
        HttpResponse<String> response = client.send(createGetRequest("/task"), handler);
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetTask() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        task_1.setId(0);
        HttpResponse<String> response = client.send(createGetRequest("/task?id=0"), handler);
        Task task = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(task_1, task);
    }

    @Test
    public void shouldNotGetTaskWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(task_1, "/task");
        HttpResponse<String> response = client.send(createGetRequest("/task?id=1"), handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Задача не найдена", response.body());
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(epic_1, "/epic");
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Эпик успешно добавлен!", response.body());
    }

    @Test
    public void shouldNotAddNullEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(null, "/epic");
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Не удалось добавить эпик!", response.body());
    }

    @Test
    public void shouldNotAddEmptyEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        HttpResponse<String> response = client.send(request, handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("В теле запроса необходимо передать Epic в формате JSON", response.body());
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        epic_2.setId(0);
        HttpResponse<String> response = addTaskToServer(epic_2, "/epic?id=0");
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Эпик 0 успешно обновлен!", response.body());
    }

    @Test
    public void shouldNotUpdateEpicWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = addTaskToServer(epic_2, "/epic?id=1");
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Не удалось обновить эпик!", response.body());
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=0"), handler);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Эпик 0 удален!", response.body());
    }

    @Test
    public void shouldNotDeleteEpicWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=1"), handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Эпик не найден", response.body());
    }

    @Test
    public void shouldDeleteAllEpics() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(epic_2, "/epic");
        HttpResponse<String> response = client.send(createDeleteRequest("/epic"), handler);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Все эпики удалены!", response.body());
    }

    @Test
    public void shouldGetAllEpics() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(epic_2, "/epic");
        HttpResponse<String> response = client.send(createGetRequest("/epic"), handler);
        List<Epic> taskList = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        Assertions.assertEquals(2, taskList.size());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetEpic() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = client.send(createGetRequest("/epic?id=0"), handler);
        Epic epic = gson.fromJson(response.body(), Epic.class);
        epic_1.setId(0);
        Assertions.assertEquals(epic_1, epic);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetEpicWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = client.send(createGetRequest("/epic?id=1"), handler);
        Assertions.assertEquals("Эпик не найден", response.body());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldAddSubTask() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = addTaskToServer(subTask_1, "/subtask");
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Подзадача успешно добавлена!", response.body());
    }

    @Test
    public void shouldNotAddNullSubTask() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        HttpResponse<String> response = addTaskToServer(null, "/subtask");
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Не удалось добавить подзадачу!", response.body());
    }

    @Test
    public void shouldNotAddEmptySubTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        HttpResponse<String> response = client.send(request, handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("В теле запроса необходимо передать SubTask в формате JSON", response.body());
    }

    @Test
    public void shouldUpdateSubTask() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        HttpResponse<String> response = addTaskToServer(subTask_2, "/subtask?id=1");
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Подзадача 1 успешно обновлена!", response.body());
    }

    @Test
    public void shouldNotUpdateSubTaskWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        HttpResponse<String> response = addTaskToServer(subTask_2, "/subtask?id=2");
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Не удалось обновить подзадачу!", response.body());
    }

    @Test
    public void shouldDeleteSubTask() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask?id=1"), handler);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Подзадача 1 удалена!", response.body());
    }

    @Test
    public void shouldNotDeleteSubTaskWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask?id=2"), handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Подзадача не найдена", response.body());
    }

    @Test
    public void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask"), handler);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Все подзадачи удалены!", response.body());
    }

    @Test
    public void shouldGetAllSubTasks() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask"), handler);
        List<SubTask> subTasks = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        Assertions.assertEquals(2, subTasks.size());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetSubTask() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=1"), handler);
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);
        subTask_1.setId(1);
        Assertions.assertEquals(subTask_1, subTask);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetSubTaskWithWrongId() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=2"), handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Подзадача не найдена", response.body());
    }

    @Test
    public void shouldGetSubTasksFromEpic() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=0"), handler);
        Assertions.assertEquals(200, response.statusCode());
        List<SubTask> subTasks = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        Assertions.assertEquals(2, subTasks.size());
    }

    @Test
    public void shouldNotGetSubTasksFromWrongEpic() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=1"), handler);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Эпик не найден!", response.body());
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");
        client.send(createGetRequest("/task?id=3"), handler);
        client.send(createGetRequest("/task?id=2"), handler);
        HttpResponse<String> history = client.send(createGetRequest("/history"), handler);
        List<Task> historyList = gson.fromJson(history.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(2, historyList.size());
        Assertions.assertEquals(200, history.statusCode());
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("08:00 - 01.01.2022", FORMATTER));
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");
        HttpResponse<String> prioritized = client.send(createGetRequest(""), handler);
        List<Task> tasks = gson.fromJson(prioritized.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(2, tasks.size());
        task_2.setId(1);
        Assertions.assertEquals(task_2, tasks.get(0));
        Assertions.assertEquals(200, prioritized.statusCode());
    }
}
