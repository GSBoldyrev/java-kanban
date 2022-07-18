package test.java.ru.yandex.practicum.task_tracker;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.ru.yandex.practicum.task_tracker.servers.HttpTaskServer;
import main.java.ru.yandex.practicum.task_tracker.servers.KVServer;
import main.java.ru.yandex.practicum.task_tracker.tasks.Epic;
import main.java.ru.yandex.practicum.task_tracker.tasks.SubTask;
import main.java.ru.yandex.practicum.task_tracker.tasks.Task;
import main.java.ru.yandex.practicum.task_tracker.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static main.java.ru.yandex.practicum.task_tracker.servers.HttpTaskServer.GSON;

public class HttpTaskServerTest {

    HttpTaskServer server;
    KVServer kvServer;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
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
        String body = GSON.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        return client.send(request, handler);
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        // Act
        HttpResponse<String> response = addTaskToServer(task_1, "/task");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Задача успешно добавлена!", response.body());
    }

    @Test
    public void shouldNotAddWrongTask() throws IOException, InterruptedException {
        // Act
        HttpResponse<String> response = addTaskToServer(null, "/task");

        // Assert
        assertEquals(400, response.statusCode());
        assertEquals("Не удалось добавить задачу!", response.body());
    }

    @Test
    public void shouldNotAddEmptyTask() throws IOException, InterruptedException {
        // Arrange
        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        // Act
        HttpResponse<String> response = client.send(request, handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("В теле запроса необходимо передать Task в формате JSON", response.body());
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");
        task_2.setId(0);

        // Act
        HttpResponse<String> response = addTaskToServer(task_2, "/task?id=0");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Задача 0 успешно обновлена!", response.body());
    }

    @Test
    public void shouldNotUpdateWrongTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");

        // Act
        HttpResponse<String> response = addTaskToServer(task_2, "/task?id=1");

        // Assert
        assertEquals(400, response.statusCode());
        assertEquals("Не удалось обновить задачу!", response.body());
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=0"), handler);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals("Задача 0 удалена!", response.body());
    }

    @Test
    public void shouldNotDeleteTaskWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=1"), handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена", response.body());
    }

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/task"), handler);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены!", response.body());
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/task"), handler);
        List<Task> tasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        // Assert
        assertEquals(2, tasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");
        task_1.setId(0);

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/task?id=0"), handler);
        Task task = GSON.fromJson(response.body(), Task.class);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals(task_1, task);
    }

    @Test
    public void shouldNotGetTaskWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(task_1, "/task");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/task?id=1"), handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена", response.body());
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        // Act
        HttpResponse<String> response = addTaskToServer(epic_1, "/epic");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Эпик успешно добавлен!", response.body());
    }

    @Test
    public void shouldNotAddNullEpic() throws IOException, InterruptedException {
        // Act
        HttpResponse<String> response = addTaskToServer(null, "/epic");

        // Assert
        assertEquals(400, response.statusCode());
        assertEquals("Не удалось добавить эпик!", response.body());
    }

    @Test
    public void shouldNotAddEmptyEpic() throws IOException, InterruptedException {
        // Arrange
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        // Act
        HttpResponse<String> response = client.send(request, handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("В теле запроса необходимо передать Epic в формате JSON", response.body());
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        epic_2.setId(0);

        // Act
        HttpResponse<String> response = addTaskToServer(epic_2, "/epic?id=0");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Эпик 0 успешно обновлен!", response.body());
    }

    @Test
    public void shouldNotUpdateEpicWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");

        // Act
        HttpResponse<String> response = addTaskToServer(epic_2, "/epic?id=1");

        // Assert
        assertEquals(400, response.statusCode());
        assertEquals("Не удалось обновить эпик!", response.body());
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=0"), handler);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals("Эпик 0 удален!", response.body());
    }

    @Test
    public void shouldNotDeleteEpicWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=1"), handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("Эпик не найден", response.body());
    }

    @Test
    public void shouldDeleteAllEpics() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(epic_2, "/epic");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/epic"), handler);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals("Все эпики удалены!", response.body());
    }

    @Test
    public void shouldGetAllEpics() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(epic_2, "/epic");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/epic"), handler);
        List<Epic> taskList = GSON.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        // Assert
        assertEquals(2, taskList.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetEpic() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        epic_1.setId(0);

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/epic?id=0"), handler);
        Epic epic = GSON.fromJson(response.body(), Epic.class);

        // Assert
        assertEquals(epic_1, epic);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetEpicWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/epic?id=1"), handler);

        // Assert
        assertEquals("Эпик не найден", response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldAddSubTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");

        // Act
        HttpResponse<String> response = addTaskToServer(subTask_1, "/subtask");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Подзадача успешно добавлена!", response.body());
    }

    @Test
    public void shouldNotAddNullSubTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");

        // Act
        HttpResponse<String> response = addTaskToServer(null, "/subtask");

        // Assert
        assertEquals(400, response.statusCode());
        assertEquals("Не удалось добавить подзадачу!", response.body());
    }

    @Test
    public void shouldNotAddEmptySubTask() throws IOException, InterruptedException {
        // Arrange
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        // Act
        HttpResponse<String> response = client.send(request, handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("В теле запроса необходимо передать SubTask в формате JSON", response.body());
    }

    @Test
    public void shouldUpdateSubTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");

        // Act
        HttpResponse<String> response = addTaskToServer(subTask_2, "/subtask?id=1");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Подзадача 1 успешно обновлена!", response.body());
    }

    @Test
    public void shouldNotUpdateSubTaskWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");

        // Act
        HttpResponse<String> response = addTaskToServer(subTask_2, "/subtask?id=2");

        // Assert
        assertEquals(400, response.statusCode());
        assertEquals("Не удалось обновить подзадачу!", response.body());
    }

    @Test
    public void shouldDeleteSubTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask?id=1"), handler);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals("Подзадача 1 удалена!", response.body());
    }

    @Test
    public void shouldNotDeleteSubTaskWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask?id=2"), handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("Подзадача не найдена", response.body());
    }

    @Test
    public void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask"), handler);

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals("Все подзадачи удалены!", response.body());
    }

    @Test
    public void shouldGetAllSubTasks() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/subtask"), handler);
        List<SubTask> subTasks = GSON.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());

        // Assert
        assertEquals(2, subTasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetSubTask() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        subTask_1.setId(1);

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=1"), handler);
        SubTask subTask = GSON.fromJson(response.body(), SubTask.class);

        // Assert
        assertEquals(subTask_1, subTask);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetSubTaskWithWrongId() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=2"), handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("Подзадача не найдена", response.body());
    }

    @Test
    public void shouldGetSubTasksFromEpic() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=0"), handler);
        List<SubTask> subTasks = GSON.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());

        // Assert
        assertEquals(200, response.statusCode());
        assertEquals(2, subTasks.size());
    }

    @Test
    public void shouldNotGetSubTasksFromWrongEpic() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(subTask_2, "/subtask");

        // Act
        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=1"), handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("Эпик не найден!", response.body());
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        // Arrange
        addTaskToServer(epic_1, "/epic");
        addTaskToServer(subTask_1, "/subtask");
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");
        client.send(createGetRequest("/task?id=3"), handler);
        client.send(createGetRequest("/task?id=2"), handler);

        // Act
        HttpResponse<String> history = client.send(createGetRequest("/history"), handler);
        List<Task> historyList = GSON.fromJson(history.body(), new TypeToken<List<Task>>() {
        }.getType());

        // Assert
        assertEquals(2, historyList.size());
        assertEquals(200, history.statusCode());
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        // Arrange
        task_1.setStartTime(LocalDateTime.parse("12:00 - 01.01.2022", FORMATTER));
        task_2.setStartTime(LocalDateTime.parse("08:00 - 01.01.2022", FORMATTER));
        addTaskToServer(task_1, "/task");
        addTaskToServer(task_2, "/task");
        task_2.setId(1);

        // Act
        HttpResponse<String> prioritized = client.send(createGetRequest(""), handler);
        List<Task> tasks = GSON.fromJson(prioritized.body(), new TypeToken<List<Task>>() {
        }.getType());

        // Assert
        assertEquals(2, tasks.size());
        assertEquals(task_2, tasks.get(0));
        assertEquals(200, prioritized.statusCode());
    }
}
