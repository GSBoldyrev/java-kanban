import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<SubTask> tasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }


    @Override
    public String toString() {
        return "EpicTask{" +
                "tasks=" + tasks +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                '}';
    }

    public ArrayList<SubTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<SubTask> tasks) {
        this.tasks = tasks;
    }
}
